package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster implements Runnable{

	private Vector<GPU> GPUs;
	private LinkedList<CPU> CPUs;
	private LinkedBlockingQueue<DataBatch> in, out;
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
		GPUs.add(gpu);
	}
	public void addCpu(CPU cpu){
		CPUs.addLast(cpu);
	}

	/**
     * {@link Cluster}  private Constructor.
     */
	
	private Cluster(){
		GPUs = new Vector<>();
		CPUs = new LinkedList<>();
		in = new LinkedBlockingQueue<DataBatch>();
		out = new LinkedBlockingQueue<DataBatch>();
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


		
	@Override
	public void run() {
		System.out.println("Cluster");
			Thread t1 = new Thread(()->{
				while(true){
					DataBatch dataBatch =null;
					while(!in.isEmpty()){
						//System.out.println("taking");
						dataBatch  = in.poll();	
						CPUs.getFirst().getDataBatchCollection().add(dataBatch);
						
						//round robin
						CPU cpu = CPUs.removeFirst();
						/* try {
							cpu.proccessDataBatch();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} */
						CPUs.addLast(cpu);
					}
				}
				
			});
	
			Thread t2 = new Thread(()->{
				while(true){
					
					DataBatch dataBatch =null;
					while(!out.isEmpty()){
						//System.out.println("Sending");
						dataBatch = out.poll();
						GPUs.get(dataBatch.getGpuId()).getVRAM().add(dataBatch);
					}
				}
			});
			t1.start();
			t2.start();
		}

		
	

}
