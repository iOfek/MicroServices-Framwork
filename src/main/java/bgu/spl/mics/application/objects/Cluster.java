package bgu.spl.mics.application.objects;

import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private Vector<GPU> GPUs;
	private Vector<CPU> CPUs;
	private LinkedBlockingDeque<DataBatch> in, out;
	// Consider Using SynchronousQueue which has at most one item
	// it is used a way way to communicate between threads (e.g CPU and GPU)

	/* 	
	 * Statistics: You are free to choose how to implement this - It needs to hold the
	 * following information: Names of all the models trained, Total number of data
	 * batches processed by the CPUs, Number of CPU time units used, number of
	 * GPU time units used. 
	 */

	 /**
     * {@link Cluster} Singleton Holder.
     */

	private static class SingletonHolder {
        private static Cluster instance = new Cluster();
    }

	/**
     * {@link Cluster} Constructor.
     */

	private Cluster() {
		in = new LinkedBlockingDeque<DataBatch>();
		out = new LinkedBlockingDeque<DataBatch>();
	 }
	/**
     * Retrieves the single instance of {@link Cluster}.
     */
	public static Cluster getInstance() {
        return SingletonHolder.instance;
    }


	
	/**
     * @return the CPUQueue}.
     */
	public LinkedBlockingDeque<DataBatch> getInQueue(){
		return in;
	}

	/**
     * @return the GPUQueue}.
     */
	public LinkedBlockingDeque<DataBatch> getOutQueue(){
		return out;
	}

	/**
     * @return the most availible {@link CPU} from {@link Cluster}.
     */
	public void recieveDataBatchFromGPU(DataBatch dataBatch){
		
	}

	/**
     * @return the most availible {@link CPU} from {@link Cluster}.
     */
	public CPU getMostAvailibleCPU(){
		return CPUs.lastElement();
	}

	/**
     * @return the most availible {@link GPU} from {@link Cluster}.
     */
	public GPU getMostAvailibleGPU(){
		return GPUs.lastElement();
	}

}
