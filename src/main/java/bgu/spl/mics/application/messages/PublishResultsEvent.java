package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

/**
 * Sent by the student, handled by the Conference micro
 * service, the conference simply aggregates the model names of successful models
 * from the students (until it publishes everything). 
 */

public class PublishResultsEvent implements Event<ConfrenceInformation>{
    private Model model;

    public PublishResultsEvent(Model model) {
        this.model = model;
    }
    
     public Model getModel() {
        return model;
    }

}
