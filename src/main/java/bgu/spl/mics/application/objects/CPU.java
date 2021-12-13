package bgu.spl.mics.application.objects;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    
    private int cores;
    private LinkedBlockingQueue<DataBatch> dataBatchCollection;
  	private Cluster cluster;
	private AtomicInteger t = new AtomicInteger(1);

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
	 * @throws InterruptedException
	 * @post getDataBatchCollection.contains({@code dataBatch}) == false
	 * @post cluster.getOutQueue().contains({@code dataBatch}) == true
	 * @post getTickTime() = pre.getTickTime() + CPUProcessingTimeInTicks(dataBatch) 
	 */
	public void proccessDataBatch() throws InterruptedException{
		DataBatch dataBatch =  dataBatchCollection.take();
		System.out.println("CPU processing DB");
		synchronized(this){
			int currTime = getTickTime();
			while(getTickTime()< currTime + CPUProcessingTimeInTicks(dataBatch)){

			}
		}
		cluster.getOutQueue().add(dataBatch);
	}

	
	/**
	 * @return {@link CPU} tick time 
	 */
	public int getTickTime(){
		return t.get();
	}
	/**
	 * update CPU's tick Time
	 * @param ticksToAdd ticks to add to CPU clock after opereation
	 * @post getTickTime() = pre.getTickTime() + {@code ticksToAdd}
	 */
	public void advanceTick(){
		t.incrementAndGet();
	}

	/**
	 * @return {@link CPU}'s dataBatchCollection
	 */
	public LinkedBlockingQueue<DataBatch> getDataBatchCollection() {
		return dataBatchCollection;
	}
	

}
