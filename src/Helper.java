import java.math.RoundingMode;
import java.util.Random;
import java.math.BigDecimal;

public class Helper {
	public static double[] initGeoLocation(){
		Random rn= new Random();
		double[] geo=new double[2];
		geo[1]=rn.nextDouble()*100;
		geo[0]=rn.nextDouble()*100;
		return geo;
	}


	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
