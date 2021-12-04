package bgu.spl.mics.application.objects;

import java.util.Vector;

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

	private static Cluster instance = null;
	/* 	
	 * Statistics: You are free to choose how to implement this - It needs to hold the
	 * following information: Names of all the models trained, Total number of data
	 * batches processed by the CPUs, Number of CPU time units used, number of
	 * GPU time units used. 
	 */

	/**
     * Retrieves the single instance of {@link Cluster}.
     */
	public static Cluster getInstance() {
		if(instance == null) {
			instance = new Cluster();
		 }
		 return instance;
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
