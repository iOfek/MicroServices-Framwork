package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private int tickTime;
    private LinkedList<Model> models;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public LinkedList<Model> getModels() {
        return models;
    }
    
    

    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
        this.tickTime = 1;
        models = new LinkedList<Model>(); 
    }

    public void setModels(){
        models = new LinkedList<Model>();
    }

    public void  addModel(Model model){
        models.add(model);
    }

    public int getTickTime(){
        return tickTime;
    }

    public void advanceTick(){
        tickTime+=1;
    }
    public int getDate(){
        return date;
    }

    
}
