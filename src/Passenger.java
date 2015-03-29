import java.util.Random;

import org.apache.commons.math3.distribution.ExponentialDistribution;


public class Passenger {
	private double altitude;
	private double latitude;
	private double travelTime;
	private double travelDistance;
	private double maximumWaitingTime;
	private double maximumPrice;
	private int Uber_preference;
	private int id;
	private boolean takeUber;
	Dispatcher dispatcher;
    PassengerLogic logic;


	
	
	public Passenger(Dispatcher dispatcher, int pid){
		double[] geo= Helper.initGeoLocation();
		altitude=geo[0];
		latitude=geo[1];
		travelTime=0;
		travelDistance=new ExponentialDistribution(7).sample();      // average travel 7 miles
		maximumWaitingTime=new ExponentialDistribution(3).sample();   //average like to wait for 3 minutes
		maximumPrice=0;  // need to be changed later, follow by normal distribution?
		maximumWaitingTime=0; // need to be changed later
		Uber_preference=(int) (Math.random()*1000);  // need to be changed later
		takeUber=false;
		this.id=pid;
        this.dispatcher=dispatcher;
	}
	
	public int getID(){
		return this.id;
	}

	public double getTravelDistance(){
		return this.travelDistance;
	}
	
	public void setTravelTime(double travelTime){
		this.travelTime=travelTime;
	}
	
	public double getMaximumWaitingTime(){
		return this.maximumWaitingTime;
	}
	public double getMaximumPrice(){
		return this.maximumPrice;
	}
	public int getPerference(){
		return this.Uber_preference;
	}
	public double getTravelTime(){
		return this.travelTime;
	}

	
}
