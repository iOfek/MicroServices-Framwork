package bgu.spl.mics.application.messages;

import javax.swing.plaf.basic.BasicTreeUI.TreeCancelEditingAction;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

/**
 * Sent by the Student, this event is at the core of the system. It will
 * be processed by one of the GPU microservices. During its process, it will utilize both
 * the CPUS and the relevant GPU.
 * @param <T>
 */

public class TrainModelEvent implements Event<Model>{
    public TrainModelEvent(){}
}
