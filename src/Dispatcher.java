import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linahu on 3/28/15.
 */
public class Dispatcher {
    class Coordinate{
        public int getX() {
            return x;
        }

        private final int x;

        public int getY() {
            return y;
        }

        private final int y;

        Coordinate(){
            x = 0;
            y = 0;
        }

        Coordinate(int x, int y){
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

    public HashMap<Integer, Coordinate> getDriverPostion() {
        return driverPostion;
    }

    //maps driver id to passenger position;
    private HashMap<Integer,Coordinate> driverPostion;

    //stores the driver candidate for each passenger request
    private HashMap<Integer,Integer> driverCandidate;

    private HashMap<Coordinate,GridDetail> mapDetail;

    private double surge_price;
    private int n_grid;

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
        //don't need mapDetail.get(new Coordinate(x,y)).passengers.add(pid)?
    }
    
    public void initialize_map(int n){
        for (int i = 0; i<n; i++){
            for (int j = 0; j <n; j++){
                GridDetail gd = new GridDetail(new Coordinate(i,j));
                mapDetail.put(new Coordinate(i,j),gd);
            }
        }
        n_grid=n;
        GridDetail gd = new GridDetail(new Coordinate(-1,-1)); //What for grid(-1,-1)
        mapDetail.put(new Coordinate(-1,-1),gd);
    }

    public void reset_surge_price(double surge){
        surge_price=surge;
    }

    // need to be modified
    public double getRevenueEstimate(){
        return surge_price*100;
    }
  
    public void update_driver_position(int id,int newX,int newY){
        Coordinate coords = driverPostion.get(id);
        mapDetail.get(coords).drivers.remove(new Integer(id));
        mapDetail.get(new Coordinate(newX,newY)).drivers.add(id);
        driverPostion.put(id,new Coordinate(newX,newY));
    }

    //remove inactive driver from the map
    public void remove_driver(int id){
        Coordinate coords = driverPostion.get(id);
        mapDetail.get(coords).drivers.remove(new Integer(id));

        driverPostion.put(id,new Coordinate(-1,-1));
    }

    // find the nearest driver, update driver candidates and return eta
    // not finish calculation for eta yet
	public double get_eta(int pid){
        int[] dx ={0,0,-1,1};
        int[] dy = {1,-1,0,0};
        boolean[][] visited = new boolean[n_grid][n_grid];

		Coordinate coordinate=passengerPostion.get(pid);
        GridDetail current_grid = mapDetail.get(new Coordinate(coordinate.x,coordinate.y));

        // use bfs to find the nearest driver
        ArrayList<GridDetail> queue = new ArrayList<GridDetail>();
        double wait =0.5;
        queue.add(current_grid);
        visited[coordinate.x][coordinate.y]=true;

        while (!queue.isEmpty()){
            GridDetail g = queue.remove(0);
            coordinate=g.coords;
          //  System.out.println(g.coords.x+" "+g.coords.y);

            if (g.drivers.size()>0){
                System.out.println("Assigned candidate driver "+g.drivers.get(0)+" to passenger "+pid);
                driverCandidate.put(pid, g.drivers.get(0));
                //assuming each grid takes exact 1 min to travel
                return Math.abs(g.coords.x-current_grid.coords.x)+Math.abs(g.coords.y-current_grid.coords.y);
            } else{
                for(int i = 0;i<4;i++){
                    int newX = coordinate.x+dx[i];
                    int newY = coordinate.y+dy[i];
                    if (newX>=0 && newX<n_grid&&newY>=0 && newY<n_grid && !visited[newX][newY]){
                        visited[newX][newY]=true;
                        queue.add(mapDetail.get(new Coordinate(newX,newY)));
                    }
                }

            }
        }

        return -1;
    }

    public int assign_driver(int pid){
        if (pid==14){
            System.out.println(driverCandidate.size());
        }

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
