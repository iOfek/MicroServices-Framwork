package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    
    private int cores;
    private Vector<DataBatch> dataBatch;
    private Cluster cluster;

    /**
	 * This should be the the only public constructor in this class.
	 */
	public CPU(int cores,Vector<DataBatch> dataBatch, Cluster cluster) {
        this.cores = cores;
        this.dataBatch = dataBatch;
        this.cluster = cluster;
	}
}
