import logic.DriverLogic;
import simulation.RandomNumber;

import java.util.Random;

//become_active() randomly set the initial position
//decide_work/rest() need consider the demand and present number of vehicles. 
//idleTime not implemented yet.

public class Driver {
    private int id;
	//private double minimumSurge;

	private boolean onService;
    private boolean active;
    private double hours_working;
    private double idleTime;
    private double revenue;

    Dispatcher dispatcher;
    double rest_preference; //driver's preference to work
    DriverLogic logic;

	
	public Driver(int did, Dispatcher dispatcher, DriverLogic logic){
        id = did;
		onService=false;
		idleTime=0;
        this.dispatcher=dispatcher;
        dispatcher.add_driver(id,-1,-1);
        hours_working=0;
        revenue=0;
        this.logic=logic;

	}

    public boolean isOnService(){return onService;}

    public boolean isActive(){return active;}

    public void become_active(){
        active = true;
        // to be implemented: randomly assign a position to driver.
        dispatcher.update_driver_position(id,0,0);//
    }

    public void become_active(RandomNumber generator){
        active = true;
        int[] coords = generator.nextCoordinate();
        dispatcher.update_driver_position(id,coords[0],coords[1]);
        System.out.println("Driver "+id+" added on ("+coords[0]+", "+coords[1]+")");
    }

    public void on_service(){
        onService=true;
        dispatcher.remove_driver(id);
    }

    public void become_inactive(){
        active = false;
        // to be implemented: randomly assign a position to driver.
        dispatcher.remove_driver(id);
    }

    public boolean decide_work(){
        double revenue_estimate = dispatcher.getRevenueEstimate();
        return logic.decideWork(revenue_estimate);
    }

    public boolean decide_rest(){
        return logic.decideRest(hours_working,rest_preference,revenue);
<<<<<<< HEAD
    }

    public void setRevenue(double revenue){
        this.revenue+=revenue;
    }    

    public double getRevenue(){
        return this.revenue;
    }

    public void  setHours_working(double time){
        this.hours_working+=time;
    }

    public double getHours_working(){
        return this.hours_working;
    }
	
=======
    }	
>>>>>>> origin/master
}
