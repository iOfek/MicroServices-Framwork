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
	/* private LinkedBlockingQueue<DataBatch> in, out; */
	private LinkedBlockingQueue<String> trainedModelNames;
	private AtomicInteger numberOfDatabatchsProcessedByCpus = new AtomicInteger(0);
	private AtomicInteger cpuTimeUsed = new AtomicInteger(0);
	private AtomicInteger gpuTimeUsed = new AtomicInteger(0);
	private Vector <Integer> processTime;
	/* private boolean terminated =false;		 */	
	public void printStatistics(){
		//System.out.println("time "+gpuTimeUsed.get());
/* 		for (int i = 0; i < processTime.size(); i++) {
			System.out.println("GPU "+i+" processed time "+processTime.get(i));
		} */
	}
	  
	public void addTrainedModelName(String modelName){
		trainedModelNames.add(modelName);
	}
	public void addCpuTime(int cpuTime){
		cpuTimeUsed.addAndGet(cpuTime);
	}
	public void addGpuTime(int gpuId, int gpuTime){
		gpuTimeUsed.addAndGet(gpuTime);
		int n = processTime.get(gpuId);
		processTime.set(gpuId, new Integer(n+1));
	}
	public void advanceNumberOfDatabatchsProcessedByCpus(){
		numberOfDatabatchsProcessedByCpus.incrementAndGet();
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
		processTime.add(0);
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
		processTime = new Vector<Integer>();
/* 		in = new LinkedBlockingQueue<DataBatch>();
		out = new LinkedBlockingQueue<DataBatch>(); */
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
	/* public LinkedBlockingQueue<DataBatch> getInQueue(){
		return in;
	} */

	/**
     * @return the GPUQueue}.
     */
	/* public LinkedBlockingQueue<DataBatch> getOutQueue(){
		return out;
	} */



	public void sendDataBatchtoGPU(DataBatch dataBatch){
		GPU gpu = GPUs.get(dataBatch.getGpuId());
		gpu.getVRAM().add(dataBatch);
	}

	public synchronized void sendDataBatchtoCPU(DataBatch dataBatch){
		// process data batchs in round-robin manner
		CPUs.getFirst().getDataBatchCollection().add(dataBatch);
		CPU cpu = CPUs.removeFirst();
		CPUs.addLast(cpu);
	}
}		
	
		
		


