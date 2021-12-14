package bgu.spl.mics.application.objects;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private Vector<GPU> GPUs;
	
	private LinkedList<CPU> CPUs;
	private LinkedBlockingQueue<DataBatch> in, out;
	private LinkedBlockingQueue<String> trainedModelNames;
	private AtomicInteger numberOfDataBatchesTrained = new AtomicInteger(0);
	private AtomicInteger cpuTimeUsed = new AtomicInteger(0);
	private AtomicInteger gpuTimeUsed = new AtomicInteger(0);
	private boolean terminated =false;			

	  
	public void addTrainedModelName(String modelName){
		trainedModelNames.add(modelName);
	}
	public void addCpuTime(int cpuTime){
		cpuTimeUsed.addAndGet(cpuTime);
	}
	public void addGpuTime(int gpuTime){
		gpuTimeUsed.addAndGet(gpuTime);
	}

	// Consider Using SynchronousQueue which has at most one item
	// it is used a way way to communicate between threads (e.g CPU and GPU)
	//TODO statistics
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

	public void addGpu(GPU gpu){
		gpu.setGpuId(GPUs.size());
		GPUs.add(gpu);
	}
	public void addCpu(CPU cpu){
		CPUs.addLast(cpu);
	}

	/**
     * {@link Cluster}  private Constructor.
     */
	
	private Cluster(){
		GPUs = new Vector<GPU>();
		CPUs = new LinkedList<CPU>();
		in = new LinkedBlockingQueue<DataBatch>();
		out = new LinkedBlockingQueue<DataBatch>();
		trainedModelNames = new LinkedBlockingQueue<String>();
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
	public LinkedBlockingQueue<DataBatch> getInQueue(){
		return in;
	}

	/**
     * @return the GPUQueue}.
     */
	public LinkedBlockingQueue<DataBatch> getOutQueue(){
		return out;
	}

	public void terminate(){
		terminated= true;
	}

	public void sendDataBatchtoGPU(DataBatch dataBatch){
		GPUs.get(dataBatch.getGpuId()).getVRAM().add(dataBatch);
	}

	public synchronized void sendDataBatchtoCPU(DataBatch dataBatch){
		// process data batchs in round-robin manner
		CPUs.getFirst().getDataBatchCollection().add(dataBatch);
		CPU cpu;
		
			cpu = CPUs.removeFirst();
		
		CPUs.addLast(cpu);
	}
}		
	
		
		


