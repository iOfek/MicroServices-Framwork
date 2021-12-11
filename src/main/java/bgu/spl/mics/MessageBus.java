package bgu.spl.mics;

import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * The message-bus is a shared object used for communication between
 * micro-services.
 * It should be implemented as a thread-safe singleton.
 * The message-bus implementation must be thread-safe as
 * it is shared between all the micro-services in the system.
 * You must not alter any of the given methods of this interface. 
 * You cannot add methods to this interface.
 */
public interface MessageBus {

    /**
     * Subscribes {@code m} to receive {@link Event}s of type {@code type}.
     * <p>
     * @param <T>  The type of the result expected by the completed event.
     * @param type The {@link Event} type to subscribe to,
     * @param m    The subscribing micro-service.
     * @pre isMicroServiceRegistred({@code m}) == true
     * @post let {@code e} be the next {@link Event} of the same {@code type}sent, and assume {@code m} to be the next  MicroService to recieve an {@link Event} of this type, 
     * then awaitMessage(m) == {@code e}
     * @throws IllegalStateException  if isMicroServiceRegistred({@code m}) == false
    
     */
    <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) throws IllegalStateException;

    /**
     * Subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
     * <p>
     * @param type 	The {@Broadcast} type to subscribe to.
     * @param m    	The subscribing micro-service.
     * @throws IllegalStateException  if isMicroServiceRegistred({@code m}) == false
     * @pre isMicroServiceRegistred({@code m}) == true
     * @post let {@code b} be the next {@link Broadcast} of the same {@code type} sent, then awaitMessage(m) == {@code b}
     */
    void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) throws IllegalStateException;

    /**
     * Notifies the MessageBus that the event {@code e} is completed and its
     * result was {@code result}.
     * When this method is called, the message-bus will resolve the {@link Future}
     * object associated with {@link Event} {@code e}.
     * <p>
     * @param <T>    The type of the result expected by the completed event.
     * @param e      The completed event.
     * @param result The resolved result of the completed event.
     * @post Let {@code f} be the {@link Future} object associated with {@link Event} {@code e}<p>
     * f.isDone() == true && f.get() == result
     */
    <T> void complete(Event<T> e, T result) throws IllegalStateException;

    /**
     * Adds the {@link Broadcast} {@code b} to the message queues of all the
     * micro-services subscribed to {@code b.getClass()}.
     * <p>
     * @param b 	The message to added to the queues.
     * @post Let {@code m} be a {@link MicroService}<p>
     *  if pre(isMicroServiceRegistred({@code m}) == true && subscribeBroadcast({@code b}}.getClass(),{@code m}) )
     * <p>then, awaitMessage({@code m}) == b
     */
    void sendBroadcast(Broadcast b);

    /**
     * Adds the {@link Event} {@code e} to the message queue of one of the
     * micro-services subscribed to {@code e.getClass()} in a round-robin
     * fashion. This method should be non-blocking.
     * <p>
     * @param <T>    	The type of the result expected by the event and its corresponding future object.
     * @param e     	The event to add to the queue.
     * @return {@link Future<T>} object to be resolved once the processing is complete
     * @post Let {@code m} be a {@link MicroService}<p>
     *  if pre(isMicroServiceRegistred({@code m}) == true && subscribeEvent({@code e}.getClass(),{@code m}) )
     *  && {@code m} is the next in the round-robin order <p>then, awaitMessage({@code m}) == e
     */
    <T> Future<T> sendEvent(Event<T> e);

    /**
     * Allocates a message-queue for the {@link MicroService} {@code m}.
     * <p>
     * @param m the micro-service to create a queue for.
     * @throws IllegalStateException if isMicroServiceRegistred({@code m}) == true 
     * @pre isMicroServiceRegistred({@code m}) == false 
     * @post isMicroServiceRegistred({@code m}) == true
     * 
     */
    void register(MicroService m) throws IllegalStateException;

    /**
     * Removes the message queue allocated to {@code m} via the call to
     * {@link #register(bgu.spl.mics.MicroService)} and cleans all references
     * related to {@code m} in this message-bus. If {@code m} was not
     * registered, nothing should happen.
     * <p>
     * @param m the micro-service to unregister.
     * @throws IllegalStateException if isMicroServiceRegistred({@code m}) == false 
     * @pre isMicroServiceRegistred({@code m}) == true 
     * @post isMicroServiceRegistred({@code m}) == false
     */
    void unregister(MicroService m) throws IllegalStateException;

    /**
     * Using this method, a <b>registered</b> micro-service can take message
     * from its allocated queue.
     * This method is blocking meaning that if no messages
     * are available in the micro-service queue it
     * should wait until a message becomes available.
     * The method should throw the {@link IllegalStateException} in the case
     * where {@code m} was never registered.
     * <p>
     * @param m The micro-service requesting to take a message from its message
     *          queue.
     * @return The next message in the {@code m}'s queue (blocking).
     * @throws IllegalStateException if(isMicroServiceRegistred({@code m}) == false )
     * @throws InterruptedException if interrupted while waiting for a message
     *                              to became available.
     * @post getMessages.size()  ==  (@pregetMessages.size() -1)
     */
    Message awaitMessage(MicroService m) throws InterruptedException, IllegalStateException;

    /** 
     * @return true if {@link MicroService} {@code m} is registerd, false otherwise
     */
    boolean isMicroServiceRegistred(MicroService m);

    
}
