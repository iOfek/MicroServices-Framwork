package bgu.spl.mics.application.services;

import java.util.concurrent.LinkedBlockingQueue;
import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.KillEmAllBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu;
    private LinkedBlockingQueue<Event> eventQueue;


    public GPUService(String name,GPU gpu) {
        super(name);
        this.gpu = gpu;
        eventQueue = new LinkedBlockingQueue<Event>();
    }


    
    @Override
    protected void initialize() { 
        //KillEmAll makes sure all services are done
        subscribeBroadcast(KillEmAllBroadcast.class, m -> {
            terminate();
            while(!eventQueue.isEmpty())
                complete(eventQueue.poll(), null);
            gpu.reset();
        });  
        
        //Tick Broadcast lambda
        subscribeBroadcast(TickBroadcast.class, m->{
            
            Event event =null;
            if(gpu.getModel()== null){
                for (Event test : eventQueue) {
                    if(test.getClass()== TestModelEvent.class){
                        Model model =gpu.testModelEvent(((TestModelEvent)test).getModel());
                        complete(test,model);
                        eventQueue.remove(test);
                    }
                }                
                if(!eventQueue.isEmpty()){
                    event = eventQueue.peek();
                    if(event.getClass() == TrainModelEvent.class){
                        gpu.setModel(((TrainModelEvent)event).getModel());
                        gpu.divideDataToDataBatches();
                        //System.out.println(gpu.getGpuID()+"started training "+ gpu.getModel().getName());
                    }
                }
            }
            else if(gpu.getModel().getStatus() == Model.Status.Trained){
                complete(eventQueue.poll(), gpu.getModel());
                gpu.reset();
            }
            gpu.advanceTick();
        });

        //Subscribe Train Model event lambda
        subscribeEvent(TrainModelEvent.class, m->{
            eventQueue.add(m);
        });
        
        //Subscribe Test Model event lambda
        subscribeEvent(TestModelEvent.class, m->{
            eventQueue.add(m);
            if(gpu.getModel()== null){
                for (Event test : eventQueue) {
                    if(test.getClass()== TestModelEvent.class){
                        Model model =gpu.testModelEvent(((TestModelEvent)test).getModel());
                        complete(test,model);
                    }
                } 
            }
        });
    }
}
