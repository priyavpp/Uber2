import logic.DriverLogic;
import simulation.RandomNumber;

import java.util.Random;

//become_active() randomly set the initial position
//decide_work/rest() need consider the demand and present number of vehicles. 
//idleTime not implemented yet.
//For on_service(), record the corresponding start time and collect the free time and working time based on the recording.

public class Driver {
    
    //Data collection
    private double TotalServiceHour;
    private double TotalActiveHour;
    private double TotalRevenue;
    
    private double ActiveStartTime; //Only record the latest activate time
    private double ServiceStartTime; //Only record the latest onservice time

    private int id;
    public int getId() {return id;}
    
    //private double minimumSurge;
    private boolean onService;
    private boolean active;
    
    Dispatcher dispatcher;
    double rest_preference; //driver's preference to work
    DriverLogic logic;
    
    public Driver(int did, Dispatcher dispatcher, DriverLogic logic) {
        id = did;
        onService = false;
        active = false;
        this.dispatcher = dispatcher;
        dispatcher.add_driver(id, -1, -1);
        TotalRevenue = 0;
        this.logic = logic;

        TotalServiceHour = 0;
        TotalActiveHour = 0;
    }

    public double getTotalServiceHour(double currentTime)
    {
    	if (this.isOnService())
           return this.TotalServiceHour + currentTime - this.ServiceStartTime;
    	else
    	   return TotalServiceHour;
    }
    
    public double getTotalActiveHour(double currentTime)
    {
    	if (this.isActive())
    		return this.TotalActiveHour + currentTime - this.ActiveStartTime;
    	else
    		return TotalActiveHour;
  	}
    
    public double getTotalIdleHour(double currentTime)
    {
    	return this.getTotalActiveHour(currentTime) - this.getTotalServiceHour(currentTime);
    }
    
    public double getTotalRevenue(){return this.TotalRevenue;}
    
    public boolean isOnService(){return onService;}
    public boolean isActive(){return active;}


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

    public void on_service(double currentTime) {
        //For on_service(), record the corresponding start time and collect the free time and working time based on the recording.
        onService = true;
        this.ServiceStartTime = currentTime;
        dispatcher.remove_driver(id);
    }

    public void become_inactive(double currentTime) {
        TotalActiveHour += (currentTime - ActiveStartTime);
        active = false;
        onService = false;
        dispatcher.remove_driver(id);
    }

    public boolean decide_work() {
        double surge = dispatcher.getSurge();
        return logic.decideWork(surge);//=========Based on SURGE, need modification ====/
    }

    public boolean decide_rest(double currentTime) {
        return logic.decideRest(currentTime - ActiveStartTime, rest_preference, TotalRevenue);
    }

    public void addRevenue(double revenue) {
        this.TotalRevenue += revenue;
    }

    public void addServiceHour(double time) {
        this.TotalServiceHour += time;
    }
    
    public double getPresentActiveHour(double currentTime){
    	if (this.isActive())
    		return currentTime - this.ActiveStartTime;
    	else
    		return 0;
    }
}