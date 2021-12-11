package bgu.spl.mics.application.objects;

import java.util.concurrent.ArrayBlockingQueue;
import bgu.spl.mics.application.objects.Model.Result;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
	private Model model;
    private Cluster cluster;
	private int dataBatchTrainingTime;
	private ArrayBlockingQueue<DataBatch> VRAM;
	private int tickTime;
	
	

    /**
	 * {@link GPU} Constructor
	 */
	public GPU(Type type, Model model) {
		this.type  = type;
		this.model = model;
        this.cluster = Cluster.getInstance();
		switch (this.type) {
			case RTX3090:
				dataBatchTrainingTime = 1;
				setVRAMCapacity(32);
			case RTX2080:
				dataBatchTrainingTime = 2;
				setVRAMCapacity(16);
			case GTX1080:
				dataBatchTrainingTime = 4;
				setVRAMCapacity(8);
		}
		this.tickTime =0;//TODO use time service 
	}
	
	/**
	 * Intialisze VRAM with capacity({@code VRAMCapacity})
	 * @param VRAMCapacity VRAM Capacity
	 */
	public void setVRAMCapacity(int VRAMCapacity) {
		VRAM = new ArrayBlockingQueue<DataBatch>(VRAMCapacity);
	}

	//public 

	/** 
	 * divdes {@link GPU}'s model.getData() into batches of 1000 samples({@link Data Batch} objects) and stores them in disk
	 * @return  DataBatch[] of the the devided {@link GPU}'s model.getData()
	 */
	public DataBatch[] divideDataToDataBatches(){
		int samplesize = 1000;
		Data data = model.getData();
		int numOfDataBatches = data.getSize()/samplesize;
		DataBatch[]GPUDataBatches = new DataBatch[numOfDataBatches];
		for (int i = 0; i < GPUDataBatches.length; i++) {
			GPUDataBatches[i] = new DataBatch(data, i*1000);
		}
		return GPUDataBatches;
	}

	/** Communicates with {@link Cluster} and decides how many {@link DataBatch}es to send (if any)
	 * @param  data - the unprocessed {@link Data} 
	 * @return how many {@link DataBatch}es to send (if any) >= 0
	 */
	public int numOfBatchesToSend(){
		return 0;
	}

	/**
	 * send unproccessed {@code dataBatch} To Cluster
	 * @throws IllegalStateException if VRAM.remainingCapacity()<=1
	 * @pre sends batch only if GPU has room for it when it returns<p> VRAM.remainingCapacity()>1
	 * @param  dataBatch - the unprocessed {@link DataBatch}
	 * @inv VRAM.remainingCapacity()>=0
	 * @post cluster.getInQueue().contains({@code dataBatch}) == true
	 */
	
	public void sendUnproccessedDataBatchToCluster(DataBatch dataBatch) throws IllegalStateException{
		if(VRAM.remainingCapacity()<=1){
			throw new IllegalStateException();
		}			
		cluster.getInQueue().add(dataBatch);
		
	}


	/**
	 * 
	 * @param  dataBatch - the {@link DataBatch} to be trained;
	 * @inv  VRAM.remainingCapacity()>=0
	 * @post getTickTime() = pre.getTickTime() + getDataBatchTrainingTime()
	 * @post VRAM.size() = pre.VRAM.size()-1 && VRAM.contains({@code dataBatch}) == false
	 * @post model.getData().getProccessed() == pre.model.getData().getProccessed()+1
	 */	
	public void trainDataBatch(DataBatch dataBatch){
		updateTickTime(dataBatchTrainingTime);
		VRAM.remove(dataBatch);
		model.getData().updateProcessed();
	}
	
	/**
	 * @return {@link GPU} tick time 
	 */
	public int getTickTime(){
		return tickTime;
	}

	/**
	 * update GPU's tick Time
	 * @param ticksToAdd ticks to add to GPU clock after opereation
	 * @post getTickTime() = pre.getTickTime() + {@code ticksToAdd}
	 */
	public void updateTickTime(int ticksToAdd){
		tickTime += ticksToAdd;
	}


	/**
	 * @return model.getResult() == (Good|| Bad)
	 */	
	public Model.Result testModelEvent(){
		return Result.Good;
	}


	/**
	 * @return {@link GPU}'s VRAM 
	 */
	public ArrayBlockingQueue<DataBatch> getVRAM(){
		return VRAM;
	}
	
	/**
	 * @return {@link GPU}'s {@link DataBatch} training time
	 */

	public int getDataBatchTrainingTime(){
		return dataBatchTrainingTime;
	}
	public Model getModel(){
		return model;
	}
	public void setModel(Model model){
		this.model =model;
	}

}
