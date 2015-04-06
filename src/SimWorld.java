/**
 * Created by LinaHu on 3/28/15.
 */
import logic.DriverLogic;
import logic.PassengerLogic;
import simulation.*;

import java.util.ArrayList;
import java.util.Arrays;


/*===============Todo:====================
1. Need to modify distance generator. Now all the trips are very short.
2. Each driver should record their total revenue and revenue in last 30 mins.
3. Each driver should record their hours_working;
4. Need to validate driver_check() function.
5. change the setup in driver and passenger logic so that passengers and drivers can make different decisions.
 */

public class SimWorld implements SimEventHandler {
	// class variables: dispatcher and list of drivers and passengers

	SimulationEngine scheduler;

    int n_passengers;
    int n_drivers;
    Driver[] drivers;
	Passenger[] passengers;

	Dispatcher dispatcher;

    PassengerLogic plogic;
    DriverLogic dlogic;

	double totalRevenue;
    int randomSeed = 123;
    RandomNumber mapGenerator;
    RandomNumber interarrival;
    RandomNumber distanceGenerator;
    int n_grid;

	public void initialize(int n_drivers, int max_passenger,int n_grid) {
		totalRevenue=0;
        n_passengers = 0;
        this.n_drivers=n_drivers;
        this.n_grid=n_grid;

		plogic = new PassengerLogic(randomSeed);
		dlogic = new DriverLogic(randomSeed);

        mapGenerator = new RandomNumber(123,n_grid);
        interarrival = new RandomNumber(123,10);
        distanceGenerator = new RandomNumber(123,n_grid/4);

        dispatcher = new Dispatcher();
        dispatcher.initialize_map(n_grid);

		// generate drivers
		drivers = new Driver[n_drivers];
		for (int i = 0; i < n_drivers; i++) {
            drivers[i] = new Driver(i, dispatcher, dlogic);
        }

        Passenger.setGenerator(mapGenerator,distanceGenerator);


		passengers = new Passenger[max_passenger];


		scheduler = new SimulationEngine();
		scheduler.initialize(this);

		// schedule initial events
		// to be implemented
		scheduler.scheduleEvent(0.1, "arrival", new
		 ArrayList<String>(Arrays.asList(0 + "")));
        scheduler.scheduleEvent(0, "driver_check", new ArrayList<String>());


	}

	// events
	public void arrival(int pid) {
		// generate a new customer
        double currentTime=scheduler.getTime();
		Passenger newPassenger = new Passenger(pid,dispatcher,plogic);
        passengers[pid]=newPassenger;
        scheduler.scheduleEvent(currentTime+0.1,"query",new ArrayList<String>(Arrays.asList(pid+"")));
		scheduler.scheduleEvent(currentTime+Math.round(interarrival.nextDouble()), "arrival", new ArrayList<String>(Arrays.asList(pid+1 + "")));
		System.out.println("time " + currentTime + ": arrival of passenger " + pid);
	}


	public void query(int pid) {
		// passenger query the dispatcher for eta and price
		// if passenger decides to take the uber, will schedule a request event

        double currentTime=scheduler.getTime();
        System.out.println("time "+ currentTime+": passenger "+pid+" made a query");
        Passenger p = passengers[pid];
		double cost=dispatcher.get_price(p);
        if (cost==-1){
            System.out.println("No Driver available.");
            return;
        }
        double eta=dispatcher.get_eta(pid);
        if (p.decide_uber(cost,eta)) {
			scheduler.scheduleEvent(currentTime+0.5,"request" , new ArrayList<String>(Arrays.asList(pid+"")));
            System.out.println("Passenger "+pid+" decided to take uber");
		}else {System.out.println("Passenger "+pid+" decided not to take uber");}

	}

	
	// passenger requests to ride uber
	// schedules a drop off event at +t time
	public void request(int pid) {
        double currentTime=scheduler.getTime();
        int did = dispatcher.assign_driver(pid);
        drivers[did].on_service();
        Passenger p=passengers[pid];
		scheduler.scheduleEvent(currentTime+p.getTravelDistance()/30*60, "drop_off", new ArrayList<String>(Arrays.asList(pid+"",did+"")));
        System.out.println("time "+ currentTime+": passenger "+pid+" requested a car");
	}

	public void drop_off(int pid,int did) {
        double currentTime=scheduler.getTime();
        System.out.println("time "+ currentTime+": passenger "+pid+" was dropped off.");
        Passenger p = passengers[pid];
        int[] coords = p.getDestination();
		dispatcher.update_driver_position(did, coords[0], coords[1]);
		
		// driver moves to an new location and becomes free to take new
		// passengers

    }

    public void driver_check() {
		// all the drivers, except for those who taking a passanger, will
		// periodically check to make certain decisions
		// active drivers will decide whether they should take a rest.
		// inactive drivers will decide whether they should go on road
        double currentTime = scheduler.getTime();
        for (int i=0;i<n_drivers;i++){
            Driver d =drivers[i];
            if (d.isActive() && !d.isOnService())
                if (d.decide_rest()) d.become_inactive();
            if (!d.isActive()) if(d.decide_work()) d.become_active(mapGenerator);
        }

        scheduler.scheduleEvent(currentTime+10,"driver_check",new ArrayList<String>());
	}

	@Override
	public void executeEvent(SimEvent e) {
		String s = e.getType();
		ArrayList<String> data = e.getData();
		if (s.equals("arrival")) {
			int id = Integer.parseInt(data.get(0));
			arrival(id);
		}

		if (s.equals("query")) {
			int pid = Integer.parseInt(data.get(0));
			query(pid);
		}

		if (s.equals("drop_off")) {
			int pid = Integer.parseInt(data.get(0));
			int did=Integer.parseInt(data.get(1));
			drop_off(pid,did);
		}

		if (s.equals("request")) {
			int pid = Integer.parseInt(data.get(0));
			request(pid);
		}
		if (s.equals("driver_check")) {
			driver_check();
		}

	}

	public void runSimulation(int t) {
		scheduler.runSimulation(t);
	}

	public static void main(String[] args) {
		// main program
		//int max_time = Integer.parseInt(args[0]);
        SimWorld sim = new SimWorld(); // create new simulation
        int max_time=10;
		sim.initialize(10, 1000, max_time);
		sim.runSimulation(max_time);
	}
}
