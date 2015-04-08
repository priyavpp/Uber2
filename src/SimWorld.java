/**
 * Created by LinaHu on 3/28/15.
 */
import logic.DriverLogic;
import logic.PassengerLogic;
import simulation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


/*===============Todo:====================
1. Need to modify distance generator. Now all the trips are very short.
2. Each driver should record their total revenue and revenue in last 30 mins.
3. Each driver should record their hours_working;
4. Need to validate driver_check() function.
5. change the setup in driver and passenger logic so that passengers and drivers can make different decisions.
6. In the query, need to record whether the passenger chooses uber and takes one
 */

/*==============Some potential problem and corrected mistakes======
 * 1.Out of grid possibility by randomly generate distance, use mod.
 * 2.max_passenger should be the average number of passenger appears in 1 min
 * 3.interarrival time follows exp(1/averagepassenger) distribution, so use nextExp() instead
 * 4.Passenger get total price for the whole travel distance without knowing the surge. (need discuss)
 * 5.Request should be scheduled at the same time with receiving the query. Or, it's possible that one drivers get two assignment
 * 6.Need recording the onRoadTime to get the EmptyCar Time by onRoadTime - WorkingTime
 */

public class SimWorld implements SimEventHandler {
	// class variables: dispatcher and list of drivers and passengers
	SimulationEngine scheduler;

	int n_grid;
    int n_drivers;
    Driver[] drivers;
	ArrayList<Passenger> passengers;
	double averSpeed = 40.0/3600.0; //unit: miles/s
	double interCheckTime = 10.0*60.0; //unit s. Driver decide whether to work or rest every 10min
 
	Dispatcher dispatcher;

    PassengerLogic plogic;
    DriverLogic dlogic;

	double totalRevenue;
    int randomSeed = 123;
    RandomNumber mapGenerator;
    RandomNumber interarrival;
    RandomNumber distanceGenerator;

	int PassengerCount;
    
	public void initialize(int n_drivers, int aver_passenger,int n_grid) {
		totalRevenue=0;
        this.n_drivers=n_drivers;
        this.n_grid=n_grid;

		plogic = new PassengerLogic(randomSeed);
		dlogic = new DriverLogic(randomSeed);

        mapGenerator = new RandomNumber(randomSeed,n_grid);
        interarrival = new RandomNumber(randomSeed,60.0/aver_passenger);
        distanceGenerator = new RandomNumber(randomSeed,n_grid/4.0);  //Average Travel Distance is set as n_grid/4.0)
        distanceGenerator.setGridSize(n_grid); 

        dispatcher = new Dispatcher();
        dispatcher.initialize_map(n_grid);

		// generate drivers
		drivers = new Driver[n_drivers];
		for (int i = 0; i < n_drivers; i++) {
            drivers[i] = new Driver(i, dispatcher, dlogic);
        }

        Passenger.setGenerator(mapGenerator,distanceGenerator);
        
		passengers = new ArrayList<Passenger>();

		scheduler = new SimulationEngine();
		scheduler.initialize(this);

		// schedule initial events
		// to be implemented
		scheduler.scheduleEvent(0.1, "arrival", new
		 ArrayList<String>(Arrays.asList(0 + "")));
        scheduler.scheduleEvent(0, "driver_check", new ArrayList<String>());
		scheduler.scheduleEvent(10,"exportData", new ArrayList<String>());
	}

	// Events
	public void arrival(int pid) {
		//Generate the new passenger and schedule next arrival
        double currentTime=scheduler.getTime();
		Passenger newPassenger = new Passenger(pid,dispatcher,plogic);
		passengers.add(newPassenger);
        scheduler.scheduleEvent(currentTime + 0.1, "query", new ArrayList<String>(Arrays.asList(pid + "")));
		scheduler.scheduleEvent(currentTime+Math.round(10*interarrival.nextExp())/10.0 + 0.1, "arrival", new ArrayList<String>(Arrays.asList(pid+1 + "")));
		System.out.println("time " + Helper.round(currentTime, 1) + ": arrival of passenger " + pid);
		PassengerCount=pid+1;
    }

	public void query(int pid) {
		// passenger query the dispatcher for eta and price
		// if passenger decides to take the uber, will schedule a request event
        double currentTime=scheduler.getTime();
        System.out.println("time " + Helper.round(currentTime, 1) + ": passenger " + pid + " made a query");
        Passenger p = passengers.get(pid);
		double cost=dispatcher.get_price(p);
        double eta=dispatcher.get_eta(pid);
		if (eta==-1){
			System.out.println("No Driver available.");
			return;
		}
        if (p.decide_uber(cost,eta)) {
        	p.takeUber();
        	p.setCost(cost);
        	p.setWaitingTime(eta);
			p.setArrivalTime(currentTime+eta);
			scheduler.scheduleEvent(currentTime,"request" , new ArrayList<String>(Arrays.asList(pid+"")));
            System.out.println("Passenger "+pid+" decided to take uber");
		}else {System.out.println("Passenger "+pid+" decided not to take uber");}
	}

	public void request(int pid) {
        double currentTime=scheduler.getTime();
        int did = dispatcher.assign_driver(pid);
        drivers[did].on_service(); 
        Passenger p = passengers.get(pid);
		scheduler.scheduleEvent(currentTime+p.getTravelDistance()/averSpeed, "drop_off", new ArrayList<String>(Arrays.asList(pid+"",did+"")));
        System.out.println("time "+ Helper.round(currentTime,1)+": passenger "+pid+" requested a car");
	}

	public void drop_off(int pid,int did) {
        double currentTime=scheduler.getTime();
        System.out.println("time "+ Helper.round(currentTime,1)+": passenger "+pid+" was dropped off.");
        Passenger p = passengers.get(pid);
		p.setDropOffTime(currentTime);
        int[] coords = p.getDestination();
        Driver d= drivers[did];
		d.addHours_working(p.getDropOffTime()-p.getArrivalTime());
        d.addRevenue(p.getCost());
        totalRevenue+=p.getCost();
		dispatcher.update_driver_position(did, coords[0], coords[1]);
		drivers[did].offservice(); //
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
        scheduler.scheduleEvent(currentTime + interCheckTime, "driver_check", new ArrayList<String>());
	}


	public void exportData(){
		double currentTime=scheduler.getTime();
		System.out.println("Data time " +Helper.round(currentTime,1)+" dataExport funtion is excuted!");
		scheduler.scheduleEvent(currentTime+10,"exportData", new ArrayList<String>());

		String fileName_p="Passenger_"+Helper.round(currentTime,1)+".csv";
		System.out.println(fileName_p);
		try{
			FileWriter writer= new FileWriter(fileName_p);
			writer.append("Passenger ID");
			writer.append(',');
			writer.append("TravelDistance");
			writer.append(',');
			writer.append("TravelTime");
			writer.append(',');
			writer.append("Cost");
			writer.append(',');
			writer.append("Take Uber");
			writer.append(',');
			writer.append("Waiting Time");
			writer.append('\n');    // For windows, Please replace to \r\n

			for (int i=0; i<PassengerCount; i++){
				Passenger p=passengers.get(i);
				writer.append(String.valueOf(p.getId()));
				writer.append(',');
				writer.append(Double.toString(p.getTravelDistance()));
				writer.append(',');
				writer.append((Double.toString((p.getDropOffTime()-p.getArrivalTime()))));
				writer.append(',');
				writer.append((Double.toString(p.getCost())));
				writer.append(',');
				writer.append(Boolean.toString(p.isChooseUber()));
				writer.append(',');
				writer.append((Double.toString(p.getWaitingTime())));
				writer.append('\n');
			}
			writer.flush();
			writer.close();
		} catch (IOException e){
			System.out.println("There is an IO exception:  "+ e.getMessage());
			e.printStackTrace();
		}


		String fileName_d="Driver_"+Helper.round(currentTime,1)+".csv";
		try{
			FileWriter writer=new FileWriter(fileName_d);
			writer.append("Driver ID");
			writer.append(',');
			writer.append("Location X");
			writer.append(',');
			writer.append("Location Y");
			writer.append(',');
			writer.append("Is OnService");
			writer.append(',');
			writer.append("Is Active");
			writer.append(',');
			writer.append("Working Hours");
			writer.append(',');
			writer.append("idleTime");
			writer.append(',');
			writer.append("Revenus");
			writer.append('\n');
			for( Driver d : drivers){
				writer.append(String.valueOf(d.getId()));
				writer.append(',');
				writer.append(String.valueOf(dispatcher.getDriverPostion().get(d.getId()).getX()));
				writer.append(',');
				writer.append(String.valueOf(dispatcher.getDriverPostion().get(d.getId()).getY()));
				writer.append(',');
				writer.append(Boolean.toString(d.isOnService()));
				writer.append(',');
				writer.append(Boolean.toString(d.isActive()));
				writer.append(',');
				writer.append(Double.toString(d.getHours_working()));
				writer.append(',');
				writer.append(Double.toString(d.getIdleTime()));
				writer.append(',');
				writer.append(Double.toString((d.getRevenue())));
			}
			writer.flush();
			writer.close();
		} catch (IOException e){
			System.out.println(e.toString());
			e.fillInStackTrace();
		}
	}


    // Others
	@Override
	public void executeEvent(SimEvent e) {
		String s = e.getType();
		ArrayList<String> data = e.getData();
		if (s.equals("arrival")) {
			int pid = Integer.parseInt(data.get(0));
			arrival(pid);
		}

		if (s.equals("query")) {
			int pid = Integer.parseInt(data.get(0));
			query(pid);
		}

		if (s.equals("drop_off")) {
			int pid = Integer.parseInt(data.get(0));
			int did = Integer.parseInt(data.get(1));
			drop_off(pid,did);
		}

		if (s.equals("request")) {
			int pid = Integer.parseInt(data.get(0));
			request(pid);
		}

		if (s.equals("driver_check")) {
			driver_check();
		}
		
		if (s.equals("exportData")) {
			//Write Status every 1min or 10min
			exportData();
		}

	}

	public void runSimulation(int t) {
		scheduler.runSimulation(t);
	}

	public static void main(String[] args) {
		// main program
        SimWorld sim = new SimWorld();  //Create new simulation
        int Simtime=60;         		//Simulation Time (sec).
        int n_grid = 10;        		//N by N district
        int n_drivers = 10;    		    //Number of drivers
        int averPassenger = 100; 		//Average number of passenger in one minute(person/min)
		sim.initialize(n_drivers, averPassenger, n_grid);
		sim.runSimulation(Simtime);
	}
}
