import java.util.Random;

import org.apache.commons.math3.distribution.ExponentialDistribution;



public class Passenger {
	private double altitude;
	private double latitude;
	private double Des_altitude;
	private double Des_latitude;
	private double travelDistance;
	private double maximumWaitingTime;
	private double maximumPrice;
	private int Uber_preference;
	private int id;
	Dispatcher dispatcher;//Not necessary



	
	
	public Passenger(Dispatcher dispatcher, int pid){
		double[] geo= Helper.initGeoLocation();
		altitude=geo[0];
		latitude=geo[1];
		double[] Des_geo=Helper.initGeoLocation();
		Des_altitude=Des_geo[0];
		Des_latitude=Des_geo[1];
		travelDistance=Math.sqrt((altitude-Des_altitude)*(altitude-Des_altitude)+(latitude-Des_latitude)*(latitude-Des_latitude));     // average travel 7 miles
		maximumWaitingTime=new ExponentialDistribution(3).sample();   //average like to wait for 3 minutes
		maximumPrice=new ExponentialDistribution(20).sample();  // need to be changed later, follow by normal distribution?
		Uber_preference=(int) (Math.random()*100);  // need to be changed later
		this.id=pid;
        this.dispatcher=dispatcher;
	}
	
	public int getID(){
		return this.id;
	}

	public double getTravelDistance(){
		return this.travelDistance;
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

	
}
