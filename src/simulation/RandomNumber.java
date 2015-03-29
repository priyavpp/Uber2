package simulation;

import java.util.*;

public class RandomNumber {
    Random RNG;
    long seed;
    double mu;
    double sigma;

    RandomNumber(long seed){
        this.seed = seed;
        this.RNG = new Random(seed);
    }

    RandomNumber(long seed, double mu){
        this.seed = seed;
        this.RNG = new Random(seed);
        this.mu = mu;
    }

    RandomNumber(long seed, double mu, double sigma){
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

    public double nextNormal(){
        double z = RNG.nextGaussian();
        double next= (z*sigma+mu);
        if (next > 0){
            return next;
        }else{
            return nextNormal();
        }
    }




    public void reset(){
        RNG = new Random(seed);
    }





}
