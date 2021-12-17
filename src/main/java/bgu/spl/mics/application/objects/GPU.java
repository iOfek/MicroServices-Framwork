package bgu.spl.mics.application.objects;

import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.application.objects.Model.Result;
import bgu.spl.mics.application.objects.Model.Status;

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
	private  ArrayBlockingQueue<DataBatch> VRAM;
	private AtomicInteger t = new AtomicInteger(1);
	private int gpuId; 
	private DataBatch[] dataBatchs ;
	private int trained =0;
	private int totalBatchsSent=0;
	private int numOfBatchsCurrentlyBeingProcessed=0;
	private int processTime=0;
	
	public int getGpuID(){
		return gpuId;
	}
	public void setGpuId(int gpuId){
		this.gpuId = gpuId;
	}


    /**
	 * {@link GPU} Constructor
	 */
	public GPU(Type type) {
		this.type  = type;
		this.model = null;
        this.cluster = Cluster.getInstance();
		switch (this.type) {
			case RTX3090:
				dataBatchTrainingTime = 1;
				VRAM = new ArrayBlockingQueue<DataBatch>(32);
				break;
			case RTX2080:
				dataBatchTrainingTime = 2;
				VRAM = new ArrayBlockingQueue<DataBatch>(16);
				break;
			case GTX1080:
				dataBatchTrainingTime = 4;
				VRAM = new ArrayBlockingQueue<DataBatch>(8);
				break;
		}
	}
	

	/** 
	 * divdes {@link GPU}'s model.getData() into batches of 1000 samples({@link Data Batch} objects) and stores them in disk
	 * @return  DataBatch[] of the the devided {@link GPU}'s model.getData()
	 */
	public void divideDataToDataBatches(){
		int samplesize = 1000;
		Data data = model.getData();
		int numOfDataBatches = data.getSize()/samplesize;
		dataBatchs = new DataBatch[numOfDataBatches];
		for (int i = 0; i < numOfDataBatches; i++) {
			DataBatch dataBatch= new DataBatch(data, i*1000); 
			dataBatch.setGpuId(gpuId);
			dataBatchs[i] = dataBatch;
		}
	}

	
	/**
	 * send unproccessed {@code dataBatch} To Cluster
	 * @throws IllegalStateException if VRAM.remainingCapacity()<=1
	 * @pre sends batch only if GPU has room for it when it returns<p> VRAM.remainingCapacity()>1
	 * @param  dataBatch - the unprocessed {@link DataBatch}
	 * @inv VRAM.remainingCapacity()>=0
	 * @post cluster.getInQueue().contains({@code dataBatch}) == true
	 */
	
	private void sendUnproccessedDataBatchToCluster(DataBatch dataBatch) throws IllegalStateException{
		if(VRAM.remainingCapacity()<=1){
			throw new IllegalStateException();
		}
		totalBatchsSent+=1;		
		numOfBatchsCurrentlyBeingProcessed+=1;	
		cluster.sendDataBatchtoCPU(dataBatch);
		
	}
	
	/**
	 * @return {@link GPU} tick time 
	 */
	public int getTickTime(){
		//return tickTime;
		return t.get();
	}
/** Communicates with {@link Cluster} and decides how many {@link DataBatch}es to send (if any)
	 * @param  data - the unprocessed {@link Data} 
	 * @return how many {@link DataBatch}es to send (if any) >= 0
	 */
	public int numOfBatchesToSend(){
		int initialCapacity = VRAM.size()+VRAM.remainingCapacity();
		//System.out.println("sending "+(initialCapacity -numOfBatchsCurrentlyBeingProcessed-VRAM.size()));
		int x = initialCapacity/2 -numOfBatchsCurrentlyBeingProcessed-VRAM.size() ;
		//int min = Math.min(4, x);
		return x;
	}
	/**
	 * update GPU's tick Time
	 * @param ticksToAdd ticks to add to GPU clock after opereation
	 * @post getTickTime() = pre.getTickTime() + {@code ticksToAdd}
	 */
	public void reset(){
		model =null;
		numOfBatchsCurrentlyBeingProcessed=0;
		totalBatchsSent =0;
		trained =0;
	}

	
	public void advanceTick(){
		//tickTime += ticksToAdd;
		if(model!= null){
			int n = numOfBatchesToSend();
			int copyTotalBatchsSent = totalBatchsSent;
			for (int i = copyTotalBatchsSent; i < (n + copyTotalBatchsSent) &&  i< dataBatchs.length; i++) {
				//System.out.println("GPU "+gpuId+" send db number "+i);
				sendUnproccessedDataBatchToCluster(dataBatchs[i]);
			}
			 /* while(VRAM.size()==0){
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} */ 
			
			trained +=trainModel();
			//System.out.println("trained "+trained);
			if(trained>= dataBatchs.length-1){
				//System.out.println("total size"+model.getDataSize());
				model.setStatus(Status.Trained);
			}
		}
		t.incrementAndGet();
		
	}
	public int trainModel(){
		int trained =0;
		int currTime = getTickTime();
		if(!VRAM.isEmpty()){
			processTime+=1;
			if(processTime == dataBatchTrainingTime){
				VRAM.remove();
				processTime =0;
				trained+=1;
				model.getData().updateProcessed();
				numOfBatchsCurrentlyBeingProcessed-=1;
				cluster.addGpuTime(gpuId,dataBatchTrainingTime);
				cluster.printStatistics();
			}
		}
		return trained;
	}

	/**
	 * @return model.getResult() == (Good|| Bad)
	 */	
	public Model testModelEvent(Model model){
		model.setResult(Result.Good);
		return model;//TODO add real random
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
	public Cluster getCluster(){
		return cluster;
	}


}
