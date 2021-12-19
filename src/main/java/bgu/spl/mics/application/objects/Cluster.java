package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.Vector;
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

	// private ConcurrentHashMap<Integer,GPU> GPUs2;

	private Vector<GPU> GPUs;
	
	private LinkedBlockingQueue<CPU> CPUs;
	private LinkedList<String> trainedModelNames;
	private AtomicInteger numberOfDatabatchsProcessedByCpus = new AtomicInteger(0);
	private AtomicInteger cpuTimeUsed = new AtomicInteger(0);
	private AtomicInteger gpuTimeUsed = new AtomicInteger(0);


	private Vector <Integer> GpuProcessTime;
	private Vector <Integer> CpuProcessTime;

	public void addModelName(String modelName){
		trainedModelNames.add(modelName);
	}

	public void printStatistics(){
		System.out.println("GPU Time "+gpuTimeUsed.get());
		System.out.println("CPU Time "+cpuTimeUsed.get());
		System.out.println("Number Of Databatchs Processed By Cpus "+numberOfDatabatchsProcessedByCpus.get());
		for (int i = 0; i < GpuProcessTime.size(); i++) {
			System.out.println("GPU "+i+" processed time "+GpuProcessTime.get(i));
		}
		for (int i = 0; i < CpuProcessTime.size(); i++) {
			System.out.println("CPU "+i+" processed time "+CpuProcessTime.get(i));
		}

	}
	public int getCpuTimeUsed(){
		return cpuTimeUsed.get();
	}
	public int getGpuTimeUsed(){
		return gpuTimeUsed.get();
	}
	public int getNumberOfDatabatchsProcessedByCpus(){
		return numberOfDatabatchsProcessedByCpus.get();
	}
	public LinkedList<String> getTrainedModelNames(){
		return trainedModelNames;
	}
	  
	public void addTrainedModelName(String modelName){
		trainedModelNames.add(modelName);
	}
	public void addCpuTime(int cpuId){
		cpuTimeUsed.incrementAndGet(); 
		int n = CpuProcessTime.get(cpuId);
		CpuProcessTime.set(cpuId, new Integer(n+1));
	}
	public void addGpuTime(int gpuId){
		gpuTimeUsed.incrementAndGet();
		int n = GpuProcessTime.get(gpuId);
		GpuProcessTime.set(gpuId, new Integer(n+1));
	}
	public void advanceNumberOfDatabatchsProcessedByCpus(){
		numberOfDatabatchsProcessedByCpus.incrementAndGet();
	}



	 /**
     * {@link Cluster} Singleton Holder.
     */

	private static class SingletonHolder {
        private static Cluster instance = new Cluster();
    }

	public void addGpu(GPU gpu){
		gpu.setGpuId(GPUs.size());
		GPUs.add(gpu);
		// gpu.setGpuId(GPUs2.size());
		// GPUs2.put(gpu.getGpuID(), gpu);
		GpuProcessTime.add(0);
	}	
	private int id =0;
	public void addCpu(CPU cpu){
		cpu.setCpuId(id);
		CPUs.add(cpu);
		id+=1;
		CpuProcessTime.add(0);
	}
	public void setCluster(){
		int minCpu =1000;
		for (CPU cpu : CPUs) {
			minCpu = Math.min(minCpu, cpu.getCpuCores());
		}
		int CpusSize = CPUs.size();
		LinkedList<CPU> toAdd = new LinkedList<CPU>();

		for (CPU cpu : CPUs) {
			int CpusToAdd = cpu.getCpuCores()/minCpu;
			for (int i = 1; i < CpusToAdd; i++) {
				toAdd.add(cpu);
			}
		}
		for (CPU cpu : toAdd) {
			CPUs.add(cpu);
		}
	}

	/**
     * {@link Cluster}  private Constructor.
     */
	
	private Cluster(){
		GPUs = new Vector<GPU>();
		//  GPUs2 = new ConcurrentHashMap<Integer,GPU>();
		CPUs = new LinkedBlockingQueue<CPU>();
		GpuProcessTime = new Vector<Integer>();
		CpuProcessTime = new Vector<Integer>();
		trainedModelNames = new LinkedList<String>();
	}
	
	/**
     * Retrieves the single instance of {@link Cluster}.
     */
	public static Cluster getInstance() {
        return SingletonHolder.instance;
    }


	public  void sendDataBatchtoGPU(DataBatch dataBatch){
		GPUs.get(dataBatch.getGpuId()).addDataBatchToVRAM(dataBatch);
		/* GPU gpu = ;
		gpu.getVRAM().add(dataBatch); */
		// GPUs2.get(dataBatch.getGpuId()).addDataBatchToVRAM(dataBatch); 
	}

	public  void sendDataBatchtoCPU(DataBatch dataBatch){
		// process data batchs in round-robin manner

		CPU cpu = null;
		try {
			cpu = CPUs.take();
			cpu.addDataBatchToCpu(dataBatch);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CPUs.add(cpu);
	}

	

}		
	
		
		


