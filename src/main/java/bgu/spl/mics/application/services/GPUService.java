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

    private  GPU gpu;
    private LinkedBlockingQueue<TrainModelEvent> trainQueue;
    private LinkedBlockingQueue<TestModelEvent> testQueue;
    private Object lock = new Object();


    public GPUService(String name,GPU gpu) {
        super(name);
        this.gpu = gpu;
        trainQueue = new LinkedBlockingQueue<TrainModelEvent>();
        testQueue = new LinkedBlockingQueue<TestModelEvent>();         
    }

    public void trainModelEvent(TrainModelEvent event){
        //System.out.println( "GPU recieved "+ event.getModel().getName());
        gpu.setModel(event.getModel());
        
        DataBatch[] dataBatchs = gpu.divideDataToDataBatches();
        //first send data batchs to cpu
        int index =0;
        
        for (int i = 0; i < dataBatchs.length;) {
            
            int n = gpu.numOfBatchesToSend();
            //System.out.println("Sending " +n+ " batchs");
            for (int j = 0; j < n && j+i < dataBatchs.length; j++) {
                gpu.sendUnproccessedDataBatchToCluster(dataBatchs[i+j]);
            }

            synchronized (this){
                while(gpu.getVRAM().size()< n){
                    //System.out.println("VRAM size: "+gpu.getVRAM().size());
                }
            }
            

            int timeAfterTraining = gpu.getTickTime()+gpu.trainingTime();
            
            //System.out.println("Before: "+gpu.getTickTime());
            synchronized (lock){
                //then train the proccessed data
                while(gpu.getTickTime() < timeAfterTraining){ 
                    //System.out.println("GPU Clock"+gpu.getTickTime());
                }
                gpu.getVRAM().clear(); 
                
            }
            //System.out.println("After: "+gpu.getTickTime());
            i+=n;
            if(i+n > dataBatchs.length)
                break;
            //System.out.println("sent to cluster " +i +" Databatchs" );
        } 
       
        //TODO update cluster statistics while training
       
        
        //System.out.println("complete");
        
        //complete
        complete(event, gpu.getModel());
        gpu.setModel(null);
        

    }
    public void testModelEvent(TestModelEvent event){
        
        //System.out.println("testing model:   "+event.getModel().getName());
        gpu.testModelEvent(event.getModel());
        //System.out.println(event.getModel().getResult());
        //complete
        complete(event, event.getModel());
    
    }
    
    @Override
    protected void initialize() {  
        subscribeBroadcast(TickBroadcast.class, call->{
            
                gpu.advanceTick();
                //System.out.println("GPU timeeeeeeeee "+gpu.getTickTime());
           
            
        });

        subscribeEvent(TrainModelEvent.class, m->{
            if(gpu.getModel()==null){
                if(trainQueue.isEmpty()){
                    Thread eht = new Thread(()->{trainModelEvent(m);}); 
                    eht.start();
                }
                else{
                    trainQueue.add(m);
                
                    Thread eht = new Thread(()->{
                        while(!trainQueue.isEmpty())
                            trainModelEvent(trainQueue.poll());
                    }); 
                    eht.start();
                }
            }
            else
                trainQueue.add(m);
        });
        subscribeEvent(TestModelEvent.class, m->{
            if(gpu.getModel()==null){
                if(testQueue.isEmpty()){
                    Thread eht = new Thread(()->{testModelEvent(m);}); 
                    eht.start();
                }
                else{
                    testQueue.add(m);
                
                    Thread eht = new Thread(()->{
                        while(!testQueue.isEmpty())
                            testModelEvent(testQueue.poll());
                    }); 
                    eht.start();
                }
            }
            else
                testQueue.add(m);
        });    

    }
}
