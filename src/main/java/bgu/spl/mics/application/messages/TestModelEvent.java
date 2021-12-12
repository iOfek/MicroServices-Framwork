package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
/** 
 * Sent by the student, handled by the GPU microservice, this type of
 * event is processed instantly, and will return results on the model, will return ‘Good’
 * results with a probability of 0.1 for MSc student, and 0.2 for PhD student. (yes this is
 * random), when the GPU finish handling the event it will update the object, and set
 * the future via the MessageBus, so the Student can see the change.
 * @param <T>
 */

public class TestModelEvent implements Event<Model>{
    
    private Model model;

    

    public TestModelEvent(Model model){
        this.model = model;
    }
    
    public Model getModel(){
        return model;
    }
    
    public void setModel(Model model) {
        this.model = model;
    }
    public TestModelEvent gEvent(){
        return this;
    }
}
