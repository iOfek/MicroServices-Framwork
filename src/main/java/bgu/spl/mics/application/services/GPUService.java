package bgu.spl.mics.application.services;

import java.util.concurrent.ArrayBlockingQueue;

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
    


    public GPUService(String name,GPU gpu) {
        super(name);
        this.gpu = gpu;
        
    }
    public void trainModelEvent(TrainModelEvent event){
        System.out.println("trained");
        System.out.println("Recieved "+event.getModel().getName());
        gpu.setModel(event.getModel());
        DataBatch[] dataBatchs = gpu.divideDataToDataBatches();
        //first send data batchs to cpu
        for (DataBatch dataBatch : dataBatchs) {
            gpu.sendUnproccessedDataBatchToCluster(dataBatch);
        }
        //then train the proccessed data
        ArrayBlockingQueue<DataBatch> VRAM = gpu.getVRAM();
        gpu.trainDataBatch(VRAM.poll());
        
        //complete
        complete(event, gpu.getModel());
        

    }
    public void testModelEvent(Model m){
        gpu.testModelEvent();
    }
    
    @Override
    protected void initialize() {  
       
       

    }
}
