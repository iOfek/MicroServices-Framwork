package bgu.spl.mics.application.objects;

import javax.xml.bind.PrintConversionEvent;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    /**
     * Enum representing the status of the Model.
     */
    enum Status {PreTrained, Training, Trained, Tested}

    /**
     * Enum representing the results of the Model.
     */
    enum Result {None, Good, Bad}

    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Result result;

    /**
     * {@link Model} Cosntructor 
     */

    public Model(String name, Data data, Student student, Status status, Result result){
        this.name = name;
        this.data = data;
        this.student = student;
        this.status = status;
        this.result = result;
    }


    /**
     * @return {@link Model} object's name 
     */

    public String getName(){
        return name;
    }

    /**
     * @return {@link Model} object's {@link Data}
     */

    public Data getData(){
        return data;
    }

    /**
     * @return {@link Model} object's {@link Student} 
     */

    public Student getStudent(){
        return student;
    }

    /**
     * @return {@link Model} object's {@link Status} 
     */

    public Status getStatus(){
        return status;
    }

    /**
     * @return {@link Model} object's {@link Result} 
     */

    public Result getResult(){
        return result;
    }

    /** 
     * Sets {@link Model}'s {@link Status}
     * @param status {@link Status}
     * @post this.getStatus() == {@code status}
     */

    public void setStatus(Status status){
        this.status = status;
    }

    /** 
     * Sets {@link Model}'s {@link Result}
     * @param result {@link Result}
     * @post this.getResult() == {@code result}
     */

    public void setResult(Result result){
        this.result = result;
    }
}