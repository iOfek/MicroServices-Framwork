package bgu.spl.mics.application.messages;
/* 
import java.util.LinkedList;

import javax.jws.WebParam.Mode; */

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
/* import bgu.spl.mics.application.objects.Model; */

/**
 * Sent by the conference at a set time (according to
 * time ticks, see below), will broadcast all the aggregated results to all the students.
 * After this event is sent, the conference unregisters from the system.
 */

public class PublishConferenceBroadcast implements Broadcast{
    private ConfrenceInformation confrence;

    public PublishConferenceBroadcast(ConfrenceInformation confrence) {
        this.confrence = confrence;
    }
    
    public ConfrenceInformation getConfrence(){
        return confrence;
    }
    
}
