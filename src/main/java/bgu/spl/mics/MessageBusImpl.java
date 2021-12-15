package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.application.objects.Model;
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
	private Object eventLock = new Object();
	private Object broadcastLock = new Object();
	


	

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
	public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) throws IllegalStateException {
			if(!isMicroServiceRegistred(m))
				throw new IllegalStateException();
			if(!eMap.containsKey(type)){
				eMap.put(type, new LinkedList<MicroService>());
			}
			eMap.get(type).add(m);
		
	}

	@Override
	public  synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) throws IllegalStateException {
			if(!isMicroServiceRegistred(m))
				throw new IllegalStateException();
			if(!bMap.containsKey(type)){
				bMap.put(type, new LinkedList<MicroService>());
			}
			bMap.get(type).add(m);	
		
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if(!eventFutureMap.containsKey(e))
			System.out.println("mo such event");
		else	
			eventFutureMap.get(e).resolve(result);
	
	}

	@Override
	public  synchronized void sendBroadcast(Broadcast b) {
		if(b==null){
			System.out.println("terminate all");
			for (MicroService m : sMap.keySet()) {
				if(m.getClass()!= TimeService.class){
					System.out.println("terminating "+m.getName());
					m.terminate();
				}
			}
			
		}
		else{
			synchronized(bMap){
				if(bMap.containsKey(b.getClass())){
					for (MicroService m : bMap.get(b.getClass())) {
						synchronized(sMap){
							synchronized(m){
								sMap.get(m).add(b);
							}
						}	
					}					
				}
			}
		}
		
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
			synchronized(eMap){
			if(eMap.get(e.getClass()).isEmpty())
				return null;}
			// add event and corresponding future to mbus map
			Future<T> future = new Future<T>();
			synchronized(eventFutureMap){
				eventFutureMap.put(e, future);
			}
			//round-robin 
			MicroService m;
			synchronized(eMap){
				m = eMap.get(e.getClass()).removeFirst();
				synchronized(m){
					eMap.get(e.getClass()).addLast(m);
				}
			}
			// add event to microservice's messega-queue
			synchronized(sMap){
				sMap.get(m).add(e);
			}
			return eventFutureMap.get(e);
		
	}

	@Override
	public  void register(MicroService m) {
		
		if(isMicroServiceRegistred(m))
			throw new IllegalStateException();

		sMap.put(m, new LinkedBlockingQueue<Message>());
		
	}
		

	@Override
	public synchronized  void unregister(MicroService m) {
		
		if(!isMicroServiceRegistred(m)) 
			throw new IllegalStateException();
		synchronized(sMap){
			synchronized(m){
				sMap.remove(m);
			}
		}	
		synchronized(bMap){
			synchronized(m){
			for (Class b:bMap.keySet() ) {
				bMap.get(b).remove(m);
			}
		}
		}			
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException,IllegalStateException  {
		
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
		return sMap.containsKey(m);
	}



	public HashMap<Event, Future> getEventFutureMap() {
		return eventFutureMap;
	}


	public HashMap<MicroService, LinkedBlockingQueue<Message>> getsMap() {
		return sMap;
	}

	

	public HashMap<Class<? extends Event>, LinkedList<MicroService>> geteMap() {
		return eMap;
	}

	

	public HashMap<Class<? extends Broadcast>, LinkedList<MicroService>> getbMap() {
		return bMap;
	}


}
