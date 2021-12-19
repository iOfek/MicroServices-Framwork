package bgu.spl.mics;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;



/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.

 * @param <T> */
public class MessageBusImpl implements MessageBus {


	private HashMap<MicroService,LinkedBlockingQueue<Message>> sMap;
	private HashMap<Class<? extends Event>,LinkedBlockingQueue<MicroService>> eMap;
	private HashMap<Class<? extends Broadcast>,LinkedBlockingQueue<MicroService>> bMap;
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
	   eMap = new HashMap<Class<? extends Event>,LinkedBlockingQueue<MicroService>>();
	   bMap = new HashMap<Class<? extends Broadcast>,LinkedBlockingQueue<MicroService>>();
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

	

	@Override
	public  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) throws IllegalStateException {
			if(!isMicroServiceRegistred(m))
				throw new IllegalStateException();
				synchronized(eMapLock){
					if(!eMap.containsKey(type)){
						eMap.put(type, new LinkedBlockingQueue<MicroService>());
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
						bMap.put(type, new LinkedBlockingQueue<MicroService>());
					}
					bMap.get(type).add(m);
				}
			
	
	}

	@Override
	public  <T> void complete(Event<T> e, T result) {
		if(!eventFutureMap.containsKey(e))
			System.out.println("mo such event");
		else	
			eventFutureMap.get(e).resolve(result);
	}

	@Override
	public  void sendBroadcast(Broadcast b) {
			synchronized(bMapLock){
			if(bMap.containsKey(b.getClass())){
				for (MicroService m : bMap.get(b.getClass())) {
					synchronized(sMapLock){
						sMap.get(m).add(b);
					}
				} 
			}	
		} 
	}

	@Override
	public  <T> Future<T> sendEvent(Event<T> e) {
			Future<T> future = new Future<T>();
			synchronized(eventFutureMapLock){
				eventFutureMap.put(e, future);
			}

			//round-robin 
			MicroService m=null;
			synchronized(eMapLock){
				try {
					m = eMap.get(e.getClass()).take();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				eMap.get(e.getClass()).add(m);
			}
			// add event to microservice's messega-queue
			synchronized(m){
				sMap.get(m).add(e);

			}	
			return future;
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
	public  void unregister(MicroService m) {
		if(!isMicroServiceRegistred(m)) 
			throw new IllegalStateException();
		

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
		}//remove microservice's messageQueue 
		synchronized(sMapLock){
			
			sMap.remove(m);
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
		if(eMap.containsKey(class1) && eMap.get(class1).size()>0)
			return true;
		return false;
	}
	// Added for testing purposes only
	public void clear(){
		sMap.clear();
		eMap.clear();
		bMap.clear();
		eventFutureMap.clear();
	} 



}
