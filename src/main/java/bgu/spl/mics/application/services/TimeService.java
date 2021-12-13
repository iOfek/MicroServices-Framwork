package bgu.spl.mics.application.services;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

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

	

	private TimeService( int tickTime, int duration) {
		super("Time Service");
		this.tickTime = tickTime;
		this.duration = duration;
		broadcast = new TickBroadcast();
	}

	 /**
     * {@link MessageBusImpl} Singleton Holder.
     */

	private static class SingletonHolder {
        private static TimeService instance = new TimeService(1,5000);
    }

	/**
     * Retrieves the single instance of this class.
     */


	public static TimeService getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	protected void initialize() {
		Timer timer = new Timer("Time Service");
		TimerTask t = new TimerTask() {
			public void run(){
				if(duration> 0){
					sendBroadcast(broadcast);
					duration-=1;
				}
					
				else{
					terminate();
					this.cancel();
				}
			}
		};
		timer.scheduleAtFixedRate(t, 1, (long)tickTime);
			
	}

}
