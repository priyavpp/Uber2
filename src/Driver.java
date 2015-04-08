import logic.DriverLogic;
import simulation.RandomNumber;

import java.util.Random;

//become_active() randomly set the initial position
//decide_work/rest() need consider the demand and present number of vehicles. 
//idleTime not implemented yet.
//For on_service(), record the corresponding start time and collect the free time and working time based on the recording.

public class Driver {
	//Data collection
	private double TotalWorkingHour;
	private double TotalActiveHour;
	private double TotalIdleHour;
	private double revenue;
	
	private double ActiveStartTime; //Only record the latest activate time
	
    private int id;
    //private double minimumSurge;
    private boolean onService;
    private boolean active;
    private double hours_working;

   

    Dispatcher dispatcher;
    double rest_preference; //driver's preference to work
    DriverLogic logic;


    public Driver(int did, Dispatcher dispatcher, DriverLogic logic) {
        id = did;
        onService = false;
        active = false;
        this.dispatcher = dispatcher;
        dispatcher.add_driver(id, -1, -1);
        hours_working = 0;
        revenue = 0;
        this.logic = logic;
        
        TotalWorkingHour = 0;
        TotalActiveHour = 0;
        TotalIdleHour = 0;
    }

    public boolean isOnService() {
        return onService;
    }
    
    public boolean isActive() {
        return active;
    }


    public void become_active(double currentTime){
        active = true;
        onService = false;
        // to be implemented: randomly assign a position to driver.
        dispatcher.update_driver_position(id,0,0);//
        ActiveStartTime = currentTime;
    }

    public void become_active(RandomNumber generator, double currentTime) {
        active = true;
        onService = false;
        int[] coords = generator.nextCoordinate();
        dispatcher.update_driver_position(id, coords[0], coords[1]);
        ActiveStartTime = currentTime;
        System.out.println("Driver " + id + " added on (" + coords[0] + ", " + coords[1] + ")");
    }
    
    public void offservice(){
        onService=false;
    }

    public void on_service() {
        //For on_service(), record the corresponding start time and collect the free time and working time based on the recording.
        onService = true;
        dispatcher.remove_driver(id);
    }

    public void become_inactive(double currentTime) {
    	TotalActiveHour += (currentTime - ActiveStartTime);
    	TotalWorkingHour += hours_working;
    	TotalIdleHour = TotalActiveHour - TotalWorkingHour;
    	
        active = false;
        onService = false;
        hours_working = 0;
        dispatcher.remove_driver(id);
    }
    
    public boolean decide_work() {
        double revenue_estimate = dispatcher.getRevenueEstimate();
        return logic.decideWork(revenue_estimate);
    }

    public boolean decide_rest() {
        return logic.decideRest(hours_working, rest_preference, revenue);

    }

    public void addRevenue(double revenue) {
        this.revenue += revenue;
    }

    public double getRevenue() {
        return this.revenue;
    }

    public void addHours_working(double time) {
        this.hours_working += time;
    }

    public double getHours_working() {
        return this.hours_working;
    }

}