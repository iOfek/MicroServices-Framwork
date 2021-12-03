package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    
    private int cores;
    private Vector<DataBatch> dataBatchCollection;
    private Cluster cluster;

	/**
	 * This should be the the only public constructor in this class.
	 */
	public CPU(int cores,Vector<DataBatch> dataBatchCollection, Cluster cluster) {
		this.cores = cores;
		this.dataBatchCollection = dataBatchCollection;
		this.cluster = cluster;
	}

	/**
	 * @param  dataBatch - the {@link DataBatch} processed
	 * @return {@link CPU} processing time in ticks for {@code dataBach}
	 */
	private long CPUProcessingTimeInTicks(DataBatch dataBatch){
		int time=-1;
		switch (dataBatch.getData().getType()) {
			case Images:
				time = (32 / cores) * 4;
			case Text:
				time = (32 / cores) * 2;
			case Tabular:
				time = (32 / cores) * 1;
		}
		return time;
	}

	/**
	 * @param  dataBatch - the {@link DataBatch} to be processed
	 * @post  @postdataBatchCollection.size() == @predataBatchCollection.size() + 1
	 */
	public void addDataBatchToCPU(DataBatch dataBatch){
		dataBatchCollection.add(dataBatch);
	}

	/**
	 * @param  dataBatch - the {@link DataBatch} to be processed
	 * @post send the proccessed {@code dataBatch} to {@link Cluster}
	 */
	public void proccessDataBatch(DataBatch dataBatch){
		long batchStartTime = System.currentTimeMillis() ;
		long timeToProccessBatch = CPUProcessingTimeInTicks(dataBatch);
		if((System.currentTimeMillis() -batchStartTime)> timeToProccessBatch ){
			sendProccessedDataToCluster(dataBatch);
		}
	}

	/**
	 * @param  proccessedDataBatch - the proccessed {@link DataBatch}
	 * @post sends {@code proccessedDataBatch} to {@link Cluster}
	 */
	private void sendProccessedDataToCluster(DataBatch proccessedDataBatch){
		
	}
}
