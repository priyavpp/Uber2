package logic;

/**
 * Created by linahu on 4/4/15.
 */
import java.util.ArrayList;
import java.util.HashMap;

public class Logic {
    class Alternative{
        String name;
        int index;
        double score;
        ArrayList<Double> data;


        Alternative(int index, String name, ArrayList<Double> data){
            this.index = index;
            this.name=name;
            this.data=data;
            score=0.0;
        }
    }
    ArrayList<Alternative> alternatives;
    HashMap<String,Alternative> nameMap;

    public Logic(){
        nameMap= new HashMap<String, Alternative>();
        alternatives = new ArrayList<Alternative>();
    }

    public double computeScore(String name, ArrayList<Double> data){
        if (data.size()>0) return data.get(0);
        return 0.0;
    }
    public void addAlternative(String name){
        int index = alternatives.size();
        Alternative a = new Alternative(index,name,new ArrayList<Double>());
        a.score=0.0;
        alternatives.add(index,a);
        nameMap.put(name,a);
    }

    public void addAlternative(String name, ArrayList<Double> data){
        int index = alternatives.size();
        Alternative a = new Alternative(index,name,data);
        a.score=computeScore(name,data);
        alternatives.add(index,a);
        nameMap.put(name,a);
    }

    public void updateAlternative(String name, ArrayList<Double> data){
        Alternative a = nameMap.get(name);
        a.data=data;
        a.score=computeScore(name,data);
        alternatives.set(a.index,a);
    }

    public void updateScore(){
        for (Alternative a:alternatives){
            a.score=computeScore(a.name,a.data);
        }
    }

    public int choose(){
        //updateScore();
        double max = 0;
        int maxi = 0;
        for (int i=0;i<alternatives.size();i++){
            double score = alternatives.get(i).score;
            if (score>max){
                max=score;
                maxi=i;
            }
        }
        return maxi;
    }

    public boolean decide(String choice){
        if (alternatives.get(choose()).name==choice) return true;
        return false;
    }
}
