package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private PublishConferenceBroadcast broadcast;
    private ConfrenceInformation conference;

    public ConferenceService(String name,ConfrenceInformation conference) {
        super(name);
        this.conference = conference;
        broadcast = new PublishConferenceBroadcast();
    }
    
    
    @Override
    protected void initialize() {
        subscribeEvent(PublishResultsEvent.class, m->{
            broadcast.addModel(m.getModel());
        });
        subscribeBroadcast(TickBroadcast.class, m->{
            conference.advanceTick();
            if(conference.getTickTime() >= conference.getDate()){
                sendBroadcast(broadcast);
                terminate();
            }             
        });
        
    }
}
