package bgu.spl.mics.application.objects;

import javax.swing.text.html.StyleSheet;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data;
    private int start_index;
    private int gpuId;
    private int timeTrained;
    public int getTimeTrained(){
        return timeTrained;
    }
    public void setTimeTrained(int timeTrained){
        this.timeTrained =timeTrained;
    }
    /**
     * {@link DataBatch} Cosntructor 
     */
    
    public DataBatch(Data data, int start_index){
        this.data = data;
        this.start_index = start_index;
    }

    public int getGpuId(){
        return gpuId;
    }

    public void setGpuId(int gpuId){
        this.gpuId = gpuId; 
    }
    /**
     * @return the {@link Data}, the {@link DataBatch } object belongs to
     */

    public Data getData(){
        return data;
    }

    /**
     * @return {@link DataBatch } object's {@code data} 
     */

    public int getStart_index(){
        return start_index;
    }
}
