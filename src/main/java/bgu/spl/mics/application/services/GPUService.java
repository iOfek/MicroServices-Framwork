package bgu.spl.mics.application.services;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jws.WebParam.Mode;
import javax.naming.spi.DirStateFactory.Result;
import javax.net.ssl.SSLEngineResult.Status;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.KillEmAllBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Data.Type;

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
        subscribeBroadcast(KillEmAllBroadcast.class, m -> {
            terminate();
            while(!eventQueue.isEmpty())
                complete(eventQueue.poll(), null);
            gpu.reset();
            
        });  
        
        subscribeBroadcast(TickBroadcast.class, m->{
           
                
            Event event =null;
            if(gpu.getModel()== null){
                for (Event test : eventQueue) {
                    if(test.getClass()== TestModelEvent.class){
                        Model model =gpu.testModelEvent(((TestModelEvent)test).getModel());
                        complete(eventQueue.poll(),model);
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
                
                //System.out.println(gpu.getGpuID()+"completed trining "+ gpu.getModel().getName());
                gpu.reset();
            }
            //System.out.println(gpu.getTickTime());
            gpu.advanceTick();
            
            
        });

        subscribeEvent(TrainModelEvent.class, m->{
            eventQueue.add(m);
        });
        
        subscribeEvent(TestModelEvent.class, m->{
            eventQueue.add(m);
            /* if(gpu.getModel()== null){
                for (Event test : eventQueue) {
                    if(test.getClass()== TestModelEvent.class){
                        Model model =gpu.testModelEvent(((TestModelEvent)test).getModel());
                        complete(eventQueue.poll(),model);
                    }
                } 
            } */
        });
    }
}
