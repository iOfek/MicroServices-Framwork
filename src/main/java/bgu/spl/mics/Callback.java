package bgu.spl.mics;

import bgu.spl.mics.application.messages.TestModelEvent;

/**
 * a callback is a function designed to be called when a message is received.
 */
public interface Callback<T> {

    public void call(T c);
    

}
