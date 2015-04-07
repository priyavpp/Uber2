package logic;

import simulation.RandomNumber;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by linahu on 4/4/15.
 */
public class PassengerLogic extends Logic{
    private RandomNumber cost_preference_dist;
    private RandomNumber wait_preference_dist;

    private double cost_preference=0;
    private double wait_preference=0;

    public void setPreference(double c, double w){
        cost_preference=c;
        wait_preference=w;
    }

    public PassengerLogic(long seed){
        cost_preference_dist = new RandomNumber(seed,0,1);
        wait_preference_dist = new RandomNumber(seed,0,1);
        addAlternative("uber");
        addAlternative("taxi",new ArrayList<Double>(Arrays.asList(20.0,5.0)));
    }

    public void setCostPreference(long seed, double mean, double std){
        cost_preference_dist = new RandomNumber(seed,mean,std);
    }

    public double genCostPreference(){
        return cost_preference_dist.nextNormal();
    }

    public void setWaitPreference(long seed, double mean, double std){
        wait_preference_dist = new RandomNumber(seed,mean,std);
    }

    public double genWaitPreference(){
        return wait_preference_dist.nextNormal();
    }
    
    @Override
    public double computeScore(String name, ArrayList<Double> data){
        double cost= data.get(0);
        double wait = data.get(1);
        return cost*cost_preference-wait*wait_preference;
    }

    public boolean decideUber(double cost_preference,double wait_preference,double distance,double cost,double wait){
        this.cost_preference=cost_preference;
        this.wait_preference=wait_preference;
        updateAlternative("uber",new ArrayList<Double>(Arrays.asList(cost,wait)));
        updateAlternative("taxi",new ArrayList<Double>(Arrays.asList(distance*2.0,wait)));
        return decide("uber");
    }
}
