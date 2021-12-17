package bgu.spl.mics.application.services;

import java.util.concurrent.TimeUnit;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.KillEmAllBroadcast;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.objects.Model.Result;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    private TimeService t=TimeService.getInstance();


    public StudentService(String name,Student student) {
        super(name);
        this.student = student;
    }
    

    @Override
    protected void initialize() {
        
        subscribeBroadcast(PublishConferenceBroadcast.class,m->{
           int n =  m.getPublications()- student.getPublications();
           student.addPapersRead(n);
        }); 

        subscribeBroadcast(KillEmAllBroadcast.class, m -> {
            terminate();
            
        }); 

        for (Model model : student.getModels()) {
    
            
            Future<Model> future= sendEvent(new TrainModelEvent(model));
            
            model = future.get();
         

            
            if(model == null)
                break;
           
            System.out.println(model.getName() + " finished training! ");
            
            
            System.out.println("Testing "+model.getName());
            future= sendEvent(new TestModelEvent(model));
            
            model = future.get();
            if(model == null)
                break;
            System.out.println(model.getName() +" result "+model.getResult());
            //TODO PublishResultsEvent in student service
            //if model is good?
            if(model.getResult() == Result.Good){
                System.out.println("Publishing "+model.getName() +" result");
                sendEvent(new PublishResultsEvent(model));
                student.addPublication();
            }   
        }
        //System.out.println("S");
        //terminate();

    }
}
