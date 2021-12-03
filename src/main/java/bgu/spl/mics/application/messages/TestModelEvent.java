package bgu.spl.mics.application.messages;

/** 
 * Sent by the student, handled by the GPU microservice, this type of
 * event is processed instantly, and will return results on the model, will return ‘Good’
 * results with a probability of 0.1 for MSc student, and 0.2 for PhD student. (yes this is
 * random), when the GPU finish handling the event it will update the object, and set
 * the future via the MessageBus, so the Student can see the change.
 */

public class TestModelEvent {
    
}