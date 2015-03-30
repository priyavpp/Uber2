import java.util.Random;


public class Driver {
    private int id;
	private double minimumSurge;
	private int servingTime;
	private int idleTime;
	private double moneyEarned;
	private boolean onService;
    private boolean active;
    private double altitude;
    private double latitude;
    Dispatcher dispatcher;

	
	public Driver(Dispatcher dispatcher){
		onService=false;
		servingTime=0;
		idleTime=0;
		moneyEarned=0;
		Random rm= new Random();
		minimumSurge=rm.nextDouble()*3;
        this.dispatcher=dispatcher;
        double[] geo=Helper.initGeoLocation();
        altitude=geo[0];
        latitude=geo[1];
	}

    public boolean isActive(){
        return active;
    }

    public void become_active(){
        active = true;
        // to be implemented: randomly assign a position to driver.
        dispatcher.update_driver_position(id,0,0);
    }

    public void become_active(int x,int y){
        active = true;
        dispatcher.update_driver_position(id,x,y);
    }


    public void become_inactive(){
        active = false;
        // to be implemented: randomly assign a position to driver.
        dispatcher.remove_driver(id);
    }

	
}
