/**
 * Created by LinaHu on 3/28/15.
 */
import simulation.*;

import java.util.ArrayList;
import java.util.Arrays;

//import org.apache.commons.math3.distribution.ExponentialDistribution;

public class SimWorld implements SimEventHandler {
	// class variables: dispatcher and list of drivers and passengers

	SimulationEngine scheduler;
	Driver[] drivers;
	int active_Driver_Number;
	Passenger[] passengers;
	int demands;
	Dispatcher dispatcher;
	int passenger_interval;
	double totalMoney;

	public void initialize(int n_drivers, int max_passenger, int max_time) {
		active_Driver_Number = 1;
		demands = 0;
		totalMoney=0;
//		plogic = new PassengerLogic();
//		dlogic = new DriverLogic();

        dispatcher = new Dispatcher();
        dispatcher.initialize_map(10);

		// generate drivers
		drivers = new Driver[n_drivers];
		for (int i = 0; i < drivers.length; i++)
			drivers[i] = new Driver(i,dispatcher);

		// at first, assume there are 10 drivers active
		for (int i = 0; i < n_drivers; i++)
			drivers[i].become_active();

		passengers = new Passenger[max_passenger];


		scheduler = new SimulationEngine();
		scheduler.initialize(this);

		// schedule initial events
		// to be implemented
		scheduler.scheduleEvent(0, "arrival", new
		 ArrayList<String>(Arrays.asList(0 + "")));
		// scheduler.scheduleEvent(0, "driver_check", new ArrayList<String>());


	}

	// events: to be implemented pid=passenger id
	public void arrival(int pid) {
		// generate a new customer
        double currentTime=scheduler.getTime();
		Passenger curPassenger = new Passenger(dispatcher, pid);
		scheduler.scheduleEvent(currentTime+2, "arrival", new ArrayList<String>(Arrays.asList(pid+1 + "")));
//		scheduler.scheduleEvent(currentTime+new ExponentialDistribution(passenger_interval).sample(), "arrival", new ArrayList<String>(
//				Arrays.asList(pid+1 + ""+curPassenger.getTravelDistance()+""+curPassenger.getMaximumPrice()+""+curPassenger.getMaximumWaitingTime()+""+curPassenger.getPerference()));
        System.out.println("time " + currentTime + ": arrival of passenger " + pid);
	}

	public void query(int pid, double travel_distance, double max_price, double max_time, int prefer) {
		// passenger query the dispatcher for eta and price
		// if passenger decides to take the uber, will schedule a request event
        double currentTime=scheduler.getTime();
		demands++;
		Helper.updateDriverStatus(demands, active_Driver_Number);
		double price=Helper.get_price(travel_distance,demands, active_Driver_Number);
		double eta=dispatcher.get_eta_price(pid)[0];
		double et_price= dispatcher.get_eta_price(pid)[1];
		if(Helper.Decide_Take_Uber(travel_distance, max_price, max_time, prefer, demands, active_Driver_Number,eta, et_price)){
			scheduler.scheduleEvent(currentTime+1,"request" , new ArrayList<String>(Arrays.asList(pid+"")));
			totalMoney+=et_price;
		}
        System.out.println("time "+ currentTime+": passenger "+pid+" made a query");
	}

	
	// passenger requests to ride uber
	// schedules a drop off event at +t time
	public void request(int did, int pid, double eta, double X, double Y) {
        double currentTime=scheduler.getTime();
		did=dispatcher.find_nearest_driver(X, Y);
		scheduler.scheduleEvent(currentTime+eta, "drop_off", new ArrayList<String>(Arrays.asList(pid+""+did+""+eta+""+X+""+Y+"")));
	}

	public void drop_off(int did, double X, double Y) {
		demands--;
		dispatcher.update_driver_position(did, X, Y);
		
		// driver moves to an new location and becomes free to take new
		// passengers
	}

//	public void driver_check() {
//		// all the drivers, except for those who taking a passanger, will
//		// periodically check to make certain decisions
//		// active drivers will decide whether they should take a rest.
//		// inactive drivers will decide whether they should go on road
//	}

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
			double travel_distance=Double.parseDouble(data.get(1));
			double max_price=Double.parseDouble(data.get(2));
			double max_time=Double.parseDouble(data.get(3));
			int preference=Integer.parseInt(data.get(4));
			query(pid, travel_distance,max_price, max_time, preference);
		}

		if (s.equals("drop_off")) {
			int pid = Integer.parseInt(data.get(0));
			int did=Integer.parseInt(data.get(1));
			double eta=Double.parseDouble(data.get(2));
			double Des_X=Double.parseDouble(data.get(3));
			double Des_Y=Double.parseDouble(data.get(4));
			drop_off(did,Des_X, Des_Y);
		}

		if (s.equals("request")) {
			int did = Integer.parseInt(data.get(0));
			int pid = Integer.parseInt(data.get(1));
			double eta=Double.parseDouble(data.get(2));
			double Des_X=Double.parseDouble(data.get(3));
			double Des_Y=Double.parseDouble(data.get(4));
			request(did, pid, eta,Des_X, Des_Y);
		}
//		if (s.equals("driver_check")) {
//			driver_check();
//		}

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
