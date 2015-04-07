import java.util.Random;


public class Helper {
	public static double[] initGeoLocation(){
		Random rn= new Random();
		double[] geo=new double[2];
		geo[1]=rn.nextDouble()*100;
		geo[0]=rn.nextDouble()*100;
		return geo;
	}
  
    public static void updateDriverStatus(int demands, int driver_numbers){
    	// not implement yet
    }	
}
