package bgu.spl.mics.application.objects;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TrainModelEvent;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private int cores;
    private Cluster cluster;
	private int dataBatchTrainingTime;
	private int vramCapacity;
	private int numOfStoredBatches;

    /**
	 * This should be the the only public constructor in this class.
	 */
	public GPU(Type type, int cores, Cluster cluster) {
		this.type  = type;
        this.cores = cores;
        this.cluster = Cluster.getInstance();
		setSpecificGPULimitations();
		this.numOfStoredBatches = 0;
	}

	/**
	 * Set specific GPU limitations({@code dataBatchTrainingTime},{@code vram}} by {@link Type}
	 */
	private void setSpecificGPULimitations(){
		switch (this.type) {
				case RTX3090:
					dataBatchTrainingTime = 1;
					vramCapacity = 32;
				case RTX2080:
					dataBatchTrainingTime = 2;
					vramCapacity = 16;
				case GTX1080:
					dataBatchTrainingTime = 4;
					vramCapacity = 8;
		}
	}

	/**
	 * @param  data - the unprocessed {@link Data} 
	 * @inv numOfStoredBatches <= vram
	 * @post send unproccessed {@code data} To {@link Cluster} in smaller {@link DataBatch}'s.
	 */
	public void sendUnproccessedDataToCluster(Data data){
		
	}

	/**
	 * @param  data - the unprocessed {@link Data} 
	 * @post divdes {@code data} into batches of 1000 samples({@link Data Batch} objects) and stores them in disk
	 */
	private void divideDataToDataBatches(Data data){
		int samplesize = 1000;
		
	}

	/**
	 * send unproccessed {@code dataBatch} To Cluster
	 * @pre numOfStoredBatches <= vram-1
	 * @param  dataBatch - the unprocessed {@link DataBatch}
	 * @inv numOfStoredBatches <= vram
	 * @post @postnumOfStoredBatches == @prenumOfStoredBatches -1 && @postnumOfStoredBatches <= vram-1
	 * @post sends batch only if GPU has room for it when it returns(e.g. after the CPU processed)
	 */
	
	private void sendUnproccessedDataBatchToCluster(DataBatch dataBatch){

	}


	/**
	 * @pre dataBatch != null
	 * @param  dataBatch - the {@link DataBatch} to be trained;
	 * @inv  numOfStoredBatches <= vram
	 * @post send trained dataBatch {@code dataBatch} To Cluster
	 */	
	public void trainDataBatch(DataBatch dataBatch){

	}

	/**
	 * @param  trainedDataBatch - the trained {@link DataBatch} 
	 * @post send unproccessed {@code dataBatch} To Cluster
	 * @post set the {@link Future} of {@link TrainModelEvent} e as complete
	 * @post model.getResult() == Trained
	 */	
	 public void sendTrainedBatch(DataBatch trainedDataBatch){
		//MessageBusImpl.getInstance().complete(TrainModelEvent e, trainedDataBatch);
	}
	/**
	 * @param  trainedDataBatch - the trained {@link DataBatch} 
	 * @post set the {@link Future} of {@link TestModelEvent} e as complete via {@link MessageBusImpl}
	 * @return 'Good’ results with a probability of 0.1 for MSc student, and 0.2 for PhD student. 
	 * @post model.getResult() == (None || Good|| Bad)
	 */	
	public void GPUTestModelEvent(){

	}

	/**
	 * @post update GPU's tick Time
	 */
	public void updateTick(){

	}
	//מי ששולח את הטיקים עבור הCPU  זה הCPUSERVICE
}
