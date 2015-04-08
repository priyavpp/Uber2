package  simulation;

import java.util.ArrayList;

public class SimEvent implements Comparable<SimEvent> {
    private double timestamp;
    private ArrayList<String> eventData;
    private String eventType;

    public double getTime(){
        return timestamp;
    }

    public SimEvent(double time, String type, ArrayList<String>d){
        timestamp = time;
        eventType = type;
        eventData = d;
    }

    public String getType(){
        return eventType;
    }

    public ArrayList<String> getData(){
        return eventData;
    }

    public int compareTo(SimEvent e){
        return (this.getTime()-e.getTime()>0?1:-1);

    }

}
