package simulation;

import java.util.PriorityQueue;
import java.util.ArrayList;

public class SimulationEngine{
    private PriorityQueue<SimEvent> eventList;
    private SimEventHandler eventHandler;
    private double currentTime;

    public SimulationEngine(){
        eventList = new PriorityQueue<SimEvent>();
        currentTime = 0;
    }

    public void initialize(SimEventHandler eh){
        eventHandler = eh;
        eventList = new PriorityQueue<SimEvent>();
    }

    // ts = time stamp;  eventType= arrival, driver_check....     
    public void scheduleEvent(double ts, String eventType, ArrayList<String> eventData){
        SimEvent e = new SimEvent(ts,eventType,eventData);
        eventList.add(e);
    }

    public double getTime(){
        return currentTime;
    }

    public void runSimulation(double timeLimit){
        while (eventList.size()>0) {
            SimEvent e =eventList.poll();
            currentTime = e.getTime();
            if (currentTime>timeLimit) return;
            eventHandler.executeEvent(e);
        }
    }
}
