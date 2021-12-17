package bgu.spl.mics;

import java.sql.Time;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.omg.PortableServer.THREAD_POLICY_ID;

import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.application.messages.KillEmAllBroadcast;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.application.services.TimeService;
import bgu.spl.mics.example.messages.ExampleBroadcast;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.

 * @param <T> */
public class MessageBusImpl implements MessageBus {


	private HashMap<MicroService,LinkedBlockingQueue<Message>> sMap;
	private HashMap<Class<? extends Event>,LinkedList<MicroService>> eMap;
	private HashMap<Class<? extends Broadcast>,LinkedList<MicroService>> bMap;
	private HashMap<Event,Future> eventFutureMap;
	private Object sMapLock = new Object();
	private Object eMapLock = new Object();
	private Object bMapLock = new Object();
	private Object eventFutureMapLock = new Object();

	


	

	/**
     * {@link MessageBusImpl} Constructor.
     */

	private MessageBusImpl() {
	   sMap = new HashMap<MicroService,LinkedBlockingQueue<Message>>();
	   eMap = new HashMap<Class<? extends Event>,LinkedList<MicroService>>();
	   bMap = new HashMap<Class<? extends Broadcast>,LinkedList<MicroService>>();
	   eventFutureMap = new HashMap<Event,Future>();
	}
	 /**
     * {@link MessageBusImpl} Singleton Holder.
     */

	private static class SingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

	
	/**
     * Retrieves the single instance of this class.
     */

	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	//TODO add locks instead of full synchronization
	

	@Override
	public  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) throws IllegalStateException {
			if(!isMicroServiceRegistred(m))
				throw new IllegalStateException();
				synchronized(eMapLock){
					if(!eMap.containsKey(type)){
						eMap.put(type, new LinkedList<MicroService>());
					}
					eMap.get(type).add(m);
				}
				
			
	}

	@Override
	public   void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) throws IllegalStateException {
			if(!isMicroServiceRegistred(m))
				throw new IllegalStateException();
				synchronized(bMapLock){
					if(!bMap.containsKey(type)){
						bMap.put(type, new LinkedList<MicroService>());
					}
					bMap.get(type).add(m);
				}
			
	
	}

	@Override
	public  <T> void complete(Event<T> e, T result) {
		synchronized(eventFutureMap){
		if(!eventFutureMap.containsKey(e))
			System.out.println("mo such event");
		else	
			eventFutureMap.get(e).resolve(result);
		}
	}

	@Override
	public  void sendBroadcast(Broadcast b) {
		synchronized(sMapLock){
		if(b.getClass() == KillEmAllBroadcast.class){
			
					for (MicroService m : sMap.keySet()) {
					

						sMap.get(m).clear();
						sMap.get(m).add(b);
						
						
							
					
				}
				
				
		}
			
		
		else{
			synchronized(bMapLock){
			if(bMap.containsKey(b.getClass())){
				for (MicroService m : bMap.get(b.getClass())) {
					synchronized(sMapLock){
						sMap.get(m).add(b);
					}
												

				} 
			}
		}
					
		}}
			
		
					
					
	}

	@Override
	public  <T> Future<T> sendEvent(Event<T> e) {
			synchronized(eMapLock){
				if(eMap.get(e.getClass()).isEmpty())
				return null;
			}
			// add event and corresponding future to mbus map
			Future<T> future = new Future<T>();
			synchronized(eventFutureMapLock){
				eventFutureMap.put(e, future);

			}

			//round-robin 
			MicroService m;
			synchronized(eMapLock){
				m = eMap.get(e.getClass()).removeFirst();
				eMap.get(e.getClass()).addLast(m);
			}
				
			

			// add event to microservice's messega-queue
			synchronized(sMapLock){
				sMap.get(m).add(e);

			}			

			synchronized(eventFutureMapLock){
				return eventFutureMap.get(e);
			}
			
		
			
	}

	@Override
	public  void register(MicroService m) {
		
		if(isMicroServiceRegistred(m))
			throw new IllegalStateException();
		synchronized(sMapLock){
		sMap.put(m, new LinkedBlockingQueue<Message>());
		}

		
	}
		

	@Override
	public synchronized void unregister(MicroService m) {
		if(!isMicroServiceRegistred(m)) 
			throw new IllegalStateException();
		//remove microservice's messageQueue 
		synchronized(sMapLock){
			sMap.remove(m);
		}

		// unsunscribe to subscribed Events
		synchronized(eMapLock){
			for (Class e:eMap.keySet() ) {
					eMap.get(e).remove(m);
			}
		}
		// unsunscribe to subscribed Broadcasts
		synchronized(bMapLock){
			for (Class b:bMap.keySet() ) {
				bMap.get(b).remove(m);
			}
		}

					
	}

	@Override
	public  Message awaitMessage(MicroService m) throws InterruptedException,IllegalStateException  {
		
		if(!isMicroServiceRegistred(m))
			throw new IllegalStateException();
		
		Message message= null;
		try {
			message =sMap.get(m).take();
			
		} catch (InterruptedException e) {
			throw new InterruptedException();
		}
		return message;
	}

	@Override
	public boolean isMicroServiceRegistred(MicroService m) {
		synchronized(sMapLock){
					return sMap.containsKey(m);

		}
	}



	@Override
	public <T> boolean isThereAMicroserviceSubscribedToEventType(Class<? extends Event> class1) {
		synchronized(eMapLock){
		if(eMap.containsKey(class1) && eMap.get(class1).size()>0)
			return true;
		}
		return false;
	}



}
