package logic; /**
 * Created by linahu on 4/4/15.
 */
import simulation.RandomNumber;
import java.util.ArrayList;
import java.util.Arrays;

public class DriverLogic extends Logic {

    private RandomNumber rest_preference_dist;
    private double rest_value = 7.0; //average dollar value for rest/hour


    public DriverLogic(long seed){
        rest_preference_dist = new RandomNumber(seed,0,1);
        addAlternative("work");
        addAlternative("rest");
    }
    //not useed
    public void setRestPreference(long seed, double mean, double std){
        rest_preference_dist = new RandomNumber(seed,mean,std);
    }

    public double genRestPreference(){
        return rest_preference_dist.nextNormal();
    }

    @Override
    public double computeScore(String name, ArrayList<Double> data){
        if (name=="rest"){
            double hours_working = data.get(0);
            double rest_preference=data.get(1);
            if (hours_working==0) return rest_value*rest_preference;
            return rest_value*rest_preference*(1-Math.log(12.0 / hours_working));
        }
        if (name=="work"){
            double revenue = data.get(0);
            return revenue;
        }
        return 0.0;
    }

    public boolean decideWork(double revenue_estimate){
        updateAlternative("work", new ArrayList<Double>(Arrays.asList(revenue_estimate)));
        return decide("work");
    }

    /*  Still need to discuss about its detail  */
    public boolean decideRest(double hours_working, double rest_preference,double revenue){
        updateAlternative("work", new ArrayList<Double>(Arrays.asList(revenue)));
        updateAlternative("rest", new ArrayList<Double>(Arrays.asList(hours_working,rest_preference)));
        return decide("rest");
    }

}
