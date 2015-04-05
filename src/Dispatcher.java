import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linahu on 3/28/15.
 */
public class Dispatcher {
     class Coordinate{
        private final double x;
        private final double y;

        Coordinate(){
            x = 0;
            y = 0;
        }

        Coordinate(double x, double y){
            this.x= x;
            this.y= y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Coordinate)) return false;
            Coordinate key = (Coordinate) o;
            return x == key.x && y == key.y;
        }

        @Override
        public int hashCode() {
            double result = x;
            result = 31 * result + y;
            return (int) result;
        }
    }

    class GridDetail{
        Coordinate coords;
        ArrayList<Integer> drivers;
        ArrayList<Integer> passengers;

        GridDetail(Coordinate coords){
            this.coords=coords;
            drivers=new ArrayList<Integer>();
            passengers = new ArrayList<Integer>();
        }


    }
    //maps passenger id to passenger position;
    private HashMap<Integer,Coordinate> passengerPostion;

    //maps driver id to passenger position;
    private HashMap<Integer,Coordinate> driverPostion;

    //stores the driver candidate for each passenger request
    private HashMap<Integer,Integer> driverCandidate;

    private HashMap<Coordinate,GridDetail> mapDetail;

    private double surge_price;

    Dispatcher(){
        passengerPostion=new HashMap<Integer, Coordinate>();
        driverPostion = new HashMap<Integer, Coordinate>();
        mapDetail = new HashMap<Coordinate, GridDetail>();
        driverCandidate=new HashMap<Integer, Integer>();
        surge_price = 1.0;
    }

    public void add_driver(int did, int x, int y){
        driverPostion.put(did,new Coordinate(x,y));
        mapDetail.get(new Coordinate(x,y)).drivers.add(did);
    }

    public void add_passenger(int pid, int x, int y){
        passengerPostion.put(pid,new Coordinate(x,y));
    }
    public void initialize_map(int n){
        for (int i = 0; i<n; i++){
            for (int j = 0; j <n; j++){
                GridDetail gd = new GridDetail(new Coordinate(i,j));
                mapDetail.put(new Coordinate(i,j),gd);
            }
        }
        GridDetail gd = new GridDetail(new Coordinate(-1,-1));
        mapDetail.put(new Coordinate(-1,-1),gd);
    }

    public void reset_surge_price(double new_price){
        surge_price=new_price;
    }

    public double getRevenueEstimate(){
        return surge_price*100;
    }

    
    public void update_driver_position(int id,double newX,double newY){

        Coordinate coords = driverPostion.get(id);
        mapDetail.get(coords).drivers.remove(new Integer(id));
        mapDetail.get(new Coordinate(newX,newY)).drivers.add(id);
        driverPostion.put(id,new Coordinate(newX,newY));
    }

    //remove inactive driver from the map
    public void remove_driver(int id){
        Coordinate coords = driverPostion.get(id);
        mapDetail.get(coords).drivers.remove(id);
        driverPostion.put(id,new Coordinate(-1,-1));
    }

    // find the nearest driver, update driver candidates and return eta
    // not finish calculation for eta yet
	public double get_eta(int pid){
		Coordinate coordinate=passengerPostion.get(pid);
        GridDetail current_grid = mapDetail.get(new Coordinate(coordinate.x,coordinate.y));
        if (current_grid.drivers.size()>0){
            driverCandidate.put(pid,current_grid.drivers.get(0));
            return 1.0;
        } 
        // use bfs to find the nearest driver
        // to be implemented
        return 3.0;
    }

    public int get_driver(int pid){
        int did= driverCandidate.get(pid);
        driverCandidate.remove(pid);
        return did;
    }
    public double get_price(double travel_distance){
//    	for Atlanta, the basic price is calculated by the following equation:
//    	https://www.uber.com/cities/atlanta
//    		base fee 1.15
//    		$0.16 per minute
//    		$0.95 per mile
//    	    $1 safe ride
//		     assume 40 mph is the average speed

        double basicPrice= 1.15+0.16*travel_distance/40+0.95*travel_distance+1;
        return surge_price*basicPrice;
    }
    public double get_price(Passenger p){
        return get_price(p.getTravelDistance());

    }


}
