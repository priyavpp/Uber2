/**
 * Created by linahu on 3/28/15.
 */
public class DriverLogic {
    public boolean Decide_Go_On_Road(double surge, int demands, int onroad_driver){

        if(surge*(demands-onroad_driver)>100) return true;         // details need to be implemented
        return false;
    }

    public boolean Decide_Rest(int time_on_road, int revenue){
        if(time_on_road>100) return true;         // details need to be implemented
        return false;
    }

}
