package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private volatile CPU cpu;

    public CPUService(String name,CPU cpu) {
        super(name);
        this.cpu =cpu;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, call->{
            
                cpu.advanceTick();
                //System.out.println("CPU time "+cpu.getTickTime());
            
            
        });
        //TODO update cluster statistics while training
        Thread t = new Thread(()->{
            while(true){
                try {
                    cpu.proccessDataBatch();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
