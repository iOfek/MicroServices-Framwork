package bgu.spl.mics.application.services;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.KillEmAllBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Cluster;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private int tickTime;
	private int duration;
	private TickBroadcast broadcast;

	//TODO constructor like cluster

	private TimeService( ) {
		super("Time Service");
		broadcast = new TickBroadcast();
	}
	
	public TimeService( int tickTime, int duration) {
		super("Time Service");
		this.tickTime = tickTime;
		this.duration = duration;
		broadcast = new TickBroadcast();
	}
	public void  setTicktime(int tickTime){
		this.tickTime =tickTime;
	}
	public void  setDuration(int duration){
		this.duration =duration;
	}

	 /**
     * {@link MessageBusImpl} Singleton Holder.
     */

	private static class SingletonHolder {
        private static TimeService instance = new TimeService();
    }

	/**
     * Retrieves the single instance of this class.
     */


	public static TimeService getInstance() {
		return SingletonHolder.instance;
	}

	public void reduceDuration(){
		this.duration-=1;
	}

	//TODO termination tick
	@Override
	protected void initialize() {
		subscribeBroadcast(KillEmAllBroadcast.class, m -> {
            terminate();
            
        }); 
		Timer	 timer = new Timer("Time Service");
		TimerTask t = new TimerTask() {
			public void run(){
				//System.out.println("AFter");
				if(duration> 1){
					sendBroadcast(broadcast);
					reduceDuration();
					//System.out.println(duration);
				}
				
				else{
					System.out.println("Bye");this.cancel();
					
					sendBroadcast(new KillEmAllBroadcast());
					terminate();
					timer.cancel();
					//Thread.interrupted();

					//
										
				}
			}
		};
		
		timer.scheduleAtFixedRate(t, 0, (long)tickTime);
		
		
		
	}
	public int calculateTimeLeft() {
		//System.out.println("time left "+ (duration));
		return duration;
	}

}
