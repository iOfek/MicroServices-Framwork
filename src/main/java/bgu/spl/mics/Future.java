package bgu.spl.mics;

/* import java.nio.channels.InterruptedByTimeoutException;
import java.util.concurrent.Executor; */
import java.util.concurrent.TimeUnit;

/* import javax.xml.bind.PrintConversionEvent;
import javax.xml.namespace.QName; */

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	
	private T result;
	boolean isDone;
	private Object lock = new Object();
	
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		isDone = false;
		result = null;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
	 * 
     * @return the result of type T if this.isDone()==true, if not wait until it is available.      
     */
	public T get() {
		if(this.isDone())
			return this.result;
		synchronized(lock){
			while(!this.isDone()){
				try{
					lock.wait();
				}catch(InterruptedException e){}
			}
		}
		return this.result;			
	}

	
	/**
     * Resolves the result of this Future object.
	 * 
	 * @param result != null
	 * @pre isDone() == false
	 * @post isDone() == true && this.get() == result
     */
	public void resolve (T result) {
			synchronized(lock){
				this.isDone =true;
				this.result = result;
				lock.notifyAll();
			}		
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return this.isDone;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
	 * @post  (@post CPUtime - @pre CPUtime) <= timeout * unit
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {
		if(!this.isDone()){
		synchronized(lock){
			long t=unit.toMillis(timeout);
				try {
					Thread.sleep(t);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return this.result;	
	}
}
