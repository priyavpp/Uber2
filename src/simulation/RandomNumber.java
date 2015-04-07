package simulation;

import java.util.*;

public class RandomNumber {
    Random RNG;
    long seed;
    double mu;
    double sigma;

    public RandomNumber(long seed){
        this.seed = seed;
        this.RNG = new Random(seed);
    }

    public RandomNumber(long seed, double mu){
        this.seed = seed;
        this.RNG = new Random(seed);
        this.mu = mu;
    }

    public RandomNumber(long seed, double mu, double sigma){
        this.seed = seed;
        this.RNG = new Random(seed);
        this.mu = mu;
        this.sigma = sigma;
    }

    public double nextDouble(){
        return RNG.nextDouble();
    }

    public double nextExp(){
        double r = RNG.nextDouble();
        return ((-mu)*Math.log(1.0-r));
    }

    public double nextExp(double new_mu){
        double r = RNG.nextDouble();
        return ((-new_mu)*Math.log(1.0-r));
    }    

    public double nextNormal(){
        double z = RNG.nextGaussian();
        double next= (z*sigma+mu);
        if (next > 0){
            return next;
        }else{
            return nextNormal();
        }
    }


    public double nextNormal(double limit){
        double z = RNG.nextGaussian();
        double next= (z*sigma+mu);
        if (next > 0 && next<=limit){
            return next;
        }else{
            return nextNormal(limit);
        }
    }


    public int[] nextCoordinate(){
        int x = (int) (RNG.nextDouble()*mu);
        int y = (int) (RNG.nextDouble()*mu);
        return new int[]{x, y};
    }

    public int[] nextEndCoord(double mu, double limit){
        int[] end_coor= new double[2];
        int distance= (int)nextExp(mu);
        int x= (int)nextNormal(limit);
        int y= distance-x;
        if (nextDouble()>0.5) {
            x=-x;
        }
        if (nextDouble()>0.5) {
            y=-y;
        }
        end_coor[0]=x;
        end_coor[1]=y;
        return end_coor;
    }


    public void reset(){
        RNG = new Random(seed);
    }





}
