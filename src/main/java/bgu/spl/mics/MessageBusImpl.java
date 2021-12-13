package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.application.objects.Model;
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
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) throws IllegalStateException {
		if(!isMicroServiceRegistred(m))
			throw new IllegalStateException();
		if(!bMap.containsKey(type)){
			bMap.put(type, new LinkedList<MicroService>());
		}
		bMap.get(type).add(m);			
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		eventFutureMap.get(e).resolve(result);
	
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		if(bMap.containsKey(b.getClass())){
			for (MicroService m : bMap.get(b.getClass())) {
				sMap.get(m).add(b);
			}
		}
	}

	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		if(eMap.get(e.getClass()).isEmpty())
			return null;
		// add event and corresponding future to mbus map
		Future<T> future = new Future<T>();
		eventFutureMap.put(e, future);
		//round-robin 
		MicroService m = eMap.get(e.getClass()).removeFirst();
		eMap.get(e.getClass()).addLast(m);
		// add event to microservice's messega-queue
		sMap.get(m).add(e);
		
		
		return eventFutureMap.get(e);
	}

	@Override
	public void register(MicroService m) {
		if(isMicroServiceRegistred(m)) throw new IllegalStateException();
		sMap.put(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		if(!isMicroServiceRegistred(m)) throw new IllegalStateException();
		sMap.remove(m);
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
