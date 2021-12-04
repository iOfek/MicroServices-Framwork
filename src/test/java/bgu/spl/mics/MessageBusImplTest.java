package bgu.spl.mics;

import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import bgu.spl.mics.example.messages.*;
import bgu.spl.mics.example.services.*;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class MessageBusImplTest {
    private static ExampleEvent event;
    private static ExampleBroadcast broadcast;


    private static Message m;
    private static Future<String> futureEvent;
    private static MessageBusImpl msb;
    private static ExampleMessageSenderService eService;
    private static ExampleBroadcastListenerService bService;

    @Before
    public void setUp() throws Exception {
        event = new ExampleEvent("ofek");
        broadcast = new ExampleBroadcast("nadav");
        m = null;
        msb = MessageBusImpl.getInstance();
        String [] eargs = {"event"};
        String [] bargs = {"2"};

        eService = new ExampleMessageSenderService("ofekService", eargs);
        bService = new ExampleBroadcastListenerService("nadavService", bargs);
    }

    @Test
    public void testSubscribeEvent() {
        assertThrows(IllegalStateException.class, () ->msb.subscribeEvent(event.getClass(), eService));
        msb.register(eService);
        msb.subscribeEvent(event.getClass(), eService);
        futureEvent = msb.sendEvent(event);
        try{
            m = msb.awaitMessage(eService);
        }catch(Exception e){}
        assertEquals(event, m); 
    }
@Test
    public void testSubscribeBroadcast() {
        assertThrows(IllegalStateException.class, () ->msb.subscribeBroadcast(broadcast.getClass(), eService));
        msb.register(eService);
        msb.subscribeBroadcast(broadcast.getClass(), eService);
        msb.sendBroadcast(broadcast);
        try{
            m = msb.awaitMessage(eService);
        }catch(Exception e){}
        assertEquals(event, m); 
    }
    @Test
    public void testAwaitMessage() {
        assertThrows(IllegalStateException.class, () ->msb.awaitMessage( eService));
        msb.register(eService);
        msb.subscribeEvent(event.getClass(), eService);
        futureEvent = msb.sendEvent(event);
        try{
            m = msb.awaitMessage(eService);
        }catch(Exception e){}
       assertEquals(event, m);
       
        m= null;
        Thread t1 = new Thread(() ->{
        try{
            m = msb.awaitMessage(eService);
        }catch(Exception e){assertEquals(InterruptedException.class, e);}
        });
        t1.start();
        try {
            Thread.sleep(100);
        } catch (Exception e) {}
        assertEquals(Thread.State.WAITING,t1.getState());
        futureEvent = msb.sendEvent(event);
    }

    @Test
    public void testComplete() {
        msb.register(eService);
        msb.subscribeEvent(event.getClass(), eService);
        futureEvent = msb.sendEvent(event);
        String result = "before";
        assertNotEquals(result, futureEvent.get(1, TimeUnit.SECONDS));
        msb.complete(event, "after");
        assertEquals("after", futureEvent.get(1, TimeUnit.SECONDS));
    }
   @Test
    public void testSendBroadcast() {
        msb.register(eService);
        msb.register(bService);
        msb.subscribeBroadcast(broadcast.getClass(), eService);
        msb.subscribeBroadcast(broadcast.getClass(), bService);
        msb.sendBroadcast(broadcast);
        try{
            m = msb.awaitMessage(eService);
        }catch(Exception e){}
        assertEquals(broadcast, m);
        m= null;
        try{
            m = msb.awaitMessage(bService);
        }catch(Exception e){}
        assertEquals(broadcast, m);
    }

    @Test
    public void testSendEvent() {
        msb.register(eService);
        msb.register(bService);
        msb.subscribeEvent(event.getClass(), eService);
        msb.subscribeEvent(event.getClass(), bService);
        futureEvent = msb.sendEvent(event);
        try{
            m = msb.awaitMessage(eService);
        }catch(Exception e){}
        assertEquals(event, m); 
        m= null;
        Thread t1 = new Thread(() ->{
            try{
                m = msb.awaitMessage(bService);
            }catch(Exception e){}
        });
        t1.start();
        
        try {
            Thread.sleep(100);
        } catch (Exception e){}
        notify();
        assertNull(m);
    }


    @Test
    public void testIsMicroServiceRegistred() {
        assertFalse(msb.isMicroServiceRegistred(eService));
        msb.register(eService);
        assertTrue(msb.isMicroServiceRegistred(eService));
    }

    @Test
    public void testRegister() {
        msb.register(eService);
        assertTrue(msb.isMicroServiceRegistred(eService));
        assertThrows(IllegalStateException.class, () ->msb.register(eService));
    }
   
    @Test
    public void testUnregister() {
        assertThrows(IllegalStateException.class, () ->msb.unregister(eService));
        msb.register(eService);
        assertFalse(msb.isMicroServiceRegistred(eService));
    }

    @Test
    public void testGetInstance() {

    }

    

    



   
}
