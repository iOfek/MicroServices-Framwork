package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * Sent by the conference at a set time (according to
 * time ticks, see below), will broadcast all the aggregated results to all the students.
 * After this event is sent, the conference unregisters from the system.
 */

public class PublishConferenceBroadcast implements Broadcast{
    
}
