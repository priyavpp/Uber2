import java.util.Random;


public class Helper {
	public static double[] initGeoLocation(){
		Random rn= new Random();
		double[] geo=new double[2];
		geo[1]=rn.nextDouble()*100;
		geo[0]=rn.nextDouble()*100;
		return geo;
	}
	

    public static double get_price(double travel_distance, int demands, int driver_numbers){
//    	for Atlanta, the basic price is calculated by the following equation:
//    	https://www.uber.com/cities/atlanta
//    		base fee 1.15
//    		$0.16 per minute
//    		$0.95 per mile
//    	    $1 safe ride
//		     assume 40 mph is the average speed
    	
    	double basicPrice= 1.15+0.16*travel_distance/40+0.95*travel_distance+1;
    	double surcharge= demands/driver_numbers/5;    // temporary calculate by that. Modify later   
    	return surcharge*basicPrice;
    }

    
    public static void updateDriverStatus(int demands, int driver_numbers){
    	// not implement yet
    }
    
    
    public static boolean Decide_Go_On_Road(double surge, int demands, int onroad_driver){

        if(surge*(demands-onroad_driver)>100) return true;         // details need to be implemented
        return false;
    }

    public static boolean Decide_Rest(int time_on_road, int revenue){
        if(time_on_road>100) return true;         // details need to be implemented
        return false;
    }

    
    public static boolean Decide_Take_Uber(double travel_disance, double max_price, double max_time,
    		int UberPrefer, int demands, int active_Driver_Number, double eta, double et_price){
//    	need to be modified later
    	if (travel_disance>10) {
    		return true;
    	}
    	return false;
    }
	
    public static boolean TakeUber(int travelDistance, double UberPrice, int waitingTime, int Uber_preference_rate ){
        if(Uber_preference_rate>100){   //need to be implemented
            return true;
        }
        return false;
    }
	
	
}
