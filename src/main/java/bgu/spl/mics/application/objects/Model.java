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

}
