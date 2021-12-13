package bgu.spl.mics.application.objects;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private int tickTime;

    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
        this.tickTime = 1;//TODO change to atomic
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
