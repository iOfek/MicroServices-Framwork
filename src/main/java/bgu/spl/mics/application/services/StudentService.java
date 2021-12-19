package bgu.spl.mics.application.services;


import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.KillEmAllBroadcast;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.StudentStartBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.objects.Model.Result;
import bgu.spl.mics.application.objects.Model.Status;

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
            ConfrenceInformation c = m.getConfrence();
            student.addPapersRead(c.getModels().size());
        }); 

        subscribeBroadcast(KillEmAllBroadcast.class, m -> {
            student.setPapersRead();

            terminate();
            
        }); 

        subscribeBroadcast(StudentStartBroadcast.class, m->{
            for (Model model : student.getModels()) {
                Model modelCopy = model;
                Future<Model> future= sendEvent(new TrainModelEvent(model));
                model.setStatus(Status.Training);
                
                modelCopy = future.get();
            
                if(modelCopy == null)
                    break;

                model.setStatus(Status.Trained);
                Cluster.getInstance().addTrainedModelName(model.getName());
                future= sendEvent(new TestModelEvent(model));
                modelCopy = future.get();

                if(modelCopy == null)
                    break;

                model = modelCopy;
                
                if(model.getResult() == Result.Good){
                    sendEvent(new PublishResultsEvent(model));
                }   
            }
        });

    }
}
