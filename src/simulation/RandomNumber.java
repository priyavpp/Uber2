package simulation;

import java.util.*;

public class RandomNumber {
    Random RNG;
    long seed;
    double mu;
    double sigma;
    int grid_size;

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

    public void setGridSize(int n){
    	grid_size = n;
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
        System.out.println("The next number is "+next);
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


    public int[] nextEndCoord(int travelDistance,int[] start_coor ){
        int[] end_coor= new int[2];
        //Distance Size
        int x= (int)(RNG.nextDouble()*travelDistance);
        int y= travelDistance-x;
        
        //Distance Direction
        if (RNG.nextDouble()>0.5) {
            x=-x;
        }
        if (RNG.nextDouble()>0.5) {
            y=-y;
        }
        
        //Correction of out of boundary
        end_coor[0]=x+start_coor[0];
        if(end_coor[0]<0){
            end_coor[0]+=grid_size;
        }
        if(end_coor[0]>=grid_size){
            end_coor[0]-=grid_size;
        }
        
        end_coor[1]=y+start_coor[1];
        if (end_coor[1]<0){
            end_coor[1]+=grid_size;
        }
        if(end_coor[1]>=grid_size){
            end_coor[1]-=grid_size;
        }
        
        return end_coor;
    }


    public void reset(){
        RNG = new Random(seed);
    }
}
