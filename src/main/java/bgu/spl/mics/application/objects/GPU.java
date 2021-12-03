package bgu.spl.mics.application.objects;

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

    /**
	 * This should be the the only public constructor in this class.
	 */
	public GPU(Type type, int cores, Cluster cluster) {
		this.type  = type;
        this.cores = cores;
        this.cluster = cluster;
	}

}
