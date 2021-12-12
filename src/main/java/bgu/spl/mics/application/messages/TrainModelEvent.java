package bgu.spl.mics.application.messages;

import javax.jws.WebParam.Mode;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.swing.plaf.basic.BasicTreeUI.TreeCancelEditingAction;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

/**
 * Sent by the Student, this event is at the core of the system. It will
 * be processed by one of the GPU microservices. During its process, it will utilize both
 * the CPUS and the relevant GPU.
 * @param <T>
 */

public class TrainModelEvent implements Event<Model>{

    private Model model;

    

    public TrainModelEvent(Model model){
        this.model = model;
    }
    
    public Model getModel(){
        return model;
    }
    
    public void setModel(Model model) {
        this.model = model;
    }
    public TrainModelEvent gEvent(){
        return this;
    }
}
