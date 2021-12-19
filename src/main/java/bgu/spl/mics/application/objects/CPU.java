package bgu.spl.mics.application.objects;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    
    private int cores;
    private LinkedBlockingQueue<DataBatch> dataBatchCollection;
  	private Cluster cluster;
	private DataBatch currentDataBatch;

	private int tickTime =0;
	private int cpuId;
	private int timeToSend =-1;
	private int processTime =0;
	
	/**
	 * This should be the the only public constructor in this class.
	 */
	public CPU(int cores) {
		this.cores = cores;
		this.dataBatchCollection = new LinkedBlockingQueue<DataBatch>();
		this.cluster = Cluster.getInstance();
		
	}


	/**
	 * @param  dataBatch - the {@link DataBatch} processed
	 * @return {@link CPU} processing time in ticks for {@code dataBach}
	 */
	public int CPUProcessingTimeInTicks(DataBatch dataBatch){
		int time=-1;
		switch (dataBatch.getData().getType()) {
			case Images:
				time = (32 / cores) * 4;
				break;
			case Text:
				time = (32 / cores) * 2;
				break;
			case Tabular:
				time = (32 / cores) * 1;
				break;
		}
		return time;
	}

	/**
	 * 
	 * Proccesses {@code dataBatch} and sends back to {@link Cluster} 
	 * @param  dataBatch - the {@link DataBatch} to be processed
	 * @post getDataBatchCollection.contains({@code dataBatch}) == false
	 * @post cluster.getOutQueue().contains({@code dataBatch}) == true
	 * @post getTickTime() = pre.getTickTime() + CPUProcessingTimeInTicks(dataBatch) 
	 */
	public  void proccessDataBatch() {
		if(currentDataBatch == null){
			if(!dataBatchCollection.isEmpty()){
				currentDataBatch =  dataBatchCollection.poll();
				timeToSend = CPUProcessingTimeInTicks(currentDataBatch);
			}
		}
		
	
	}

	
	/**
	 * @return {@link CPU} tick time 
	 */
	public int getTickTime(){
		return tickTime;
	}

	/**
	 * update CPU's tick Time and process databatch if needed
	 * @param ticksToAdd ticks to add to CPU clock after opereation
	 * @throws InterruptedException
	 * @post getTickTime() = pre.getTickTime() + {@code ticksToAdd}
	 */
	public void advanceTick() throws InterruptedException{

		proccessDataBatch();
		if(currentDataBatch!= null){
			cluster.addCpuTime(cpuId);
			processTime+=1;
			if(processTime == timeToSend){
				cluster.sendDataBatchtoGPU(currentDataBatch);
				
				cluster.advanceNumberOfDatabatchsProcessedByCpus();
			
				currentDataBatch=null;
				processTime =0;
			}
		}
		
		tickTime+=1;
	}

	/**
	 * @param dataBatch the databatch to be added 
	 * add {@link DataBatch} {@code dataBatch} to {@link CPU}'s dataBatchCollection
	 */
	
	public void addDataBatchToCpu(DataBatch dataBatch){
		dataBatchCollection.add(dataBatch);
	} 
	public void setCpuId(int cpuId){
		this.cpuId = cpuId;
	}
	public int getCpuId(){
		return cpuId;
	}
	public int getCpuCores(){
		return cores;
	}

}
