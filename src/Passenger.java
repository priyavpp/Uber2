import logic.PassengerLogic;
import simulation.RandomNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

//import org.apache.commons.math3.distribution.ExponentialDistribution;

//GetDestination return int[] instead of Coord class.
//TravelDistance why multiply 0.5?
//why exponential distribution?
//Uber_preference

public class Passenger {
    private int[] start_coords;
    private int[] end_coords;
	private int travelDistance;
	private double wait_preference;
	private double cost_preference;
	private int Uber_preference;

    private int id;
    private static RandomNumber passengerGenerator;
    private static RandomNumber distanceGenerator;
	Dispatcher dispatcher;
    PassengerLogic logic;
    private double cost;
    private double arriveTime;
    private double dropOffTime;



    private double waitingTime;


    private boolean chooseUber;

	public static void setGenerator(RandomNumber pgenerator,RandomNumber dgenerator){
        passengerGenerator = pgenerator;
        distanceGenerator = dgenerator;
    }

    public int[] getDestination(){
        return end_coords;
    }
	
	public Passenger(int pid,Dispatcher dispatcher, PassengerLogic logic){
        start_coords = passengerGenerator.nextCoordinate();
        end_coords = new int[2];
        travelDistance=(int)distanceGenerator.nextExp() + 1;
        int[] end_coords=distanceGenerator.nextEndCoord(travelDistance, start_coords);

        // int[] distance=distanceGenerator.nextCoordinate();
        // end_coords[0]=start_coords[0]+distance[0];
        // end_coords[1]=start_coords[1]+distance[1];
//		travelDistance=(Math.abs(distance[0])+Math.abs(distance[1]))*0.5;     // average travel 7 miles
		//maximumWaitingTime=new ExponentialDistribution(3).sample();   //average like to wait for 3 minutes
		//maximumPrice=new ExponentialDistribution(20).sample();  // need to be changed later, follow by normal distribution?


        arriveTime=0;
        dropOffTime=0;
        chooseUber = false;
        wait_preference=logic.genWaitPreference();
        cost_preference=logic.genCostPreference();
		Uber_preference=(int) (Math.random()*100);  // need to be changed later
		this.id=pid;
        this.cost=0;
        this.dispatcher=dispatcher;
        dispatcher.add_passenger(pid,start_coords[0],start_coords[1]);
        // System.out.println("Passenger " + pid + " start at " + start_coords[0] + "," + start_coords[1]);
        // System.out.println("Passenger "+pid+" tries to go to "+end_coords[0]+","+end_coords[1]);

        this.logic=logic;
	}

    public boolean decide_uber(double cost,double wait){
        return logic.decideUber(cost_preference,wait_preference,travelDistance,cost,wait);
    }


    
	public double getTravelDistance(){
		return this.travelDistance;
	}
    public boolean isChooseUber() {
        return chooseUber;
    }

    public void takeUber(){
        this.chooseUber=true;
    }

    public double getCost(){
        return this.cost;
    }
	
    public void setCost(double cost){
        this.cost=cost;
    }

    public void setWaitingTime(double waitT){
    	this.waitingTime = waitT;
    }
    public double getArrivalTime(){
        return this.arriveTime;
    }

    public void setArrivalTime(double currentTime){
        this.arriveTime=currentTime;
    }

    public double getDropOffTime(){
        return this.dropOffTime;
    }

    public void setDropOffTime(double currentTime){
        this.dropOffTime=currentTime;
    }
    public int getId() {
        return id;
    }
    public double getWaitingTime() {
        return waitingTime;
    }

}
