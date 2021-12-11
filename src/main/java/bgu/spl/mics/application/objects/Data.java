package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;

    /**
     * {@link Data} Cosntructor 
     */
    
    public Data(Type type, int processed, int size){
        this.type = type;
        this.processed = processed;
        this.size = size;
    }


    /**
     * @return {@link Data } object's {@link Type} 
     */

    public Type getType(){
        return type;
    }
    
    /**
     * @return {@link Data } object's number of samples which GPU has proccessed for training
     */

    public int getProcessed(){
        return processed;
    }
    /**
     * @return {@link Data } object's number of samples which GPU has proccessed for training
     */

    public void updateProcessed(){
        processed+=1;
    }

    /**
     * @return {@link Data } object's number of samples
     */

    public int getSize(){
        return size;
    }
}
