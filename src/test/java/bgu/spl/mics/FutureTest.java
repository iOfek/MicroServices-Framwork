package bgu.spl.mics;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

public class FutureTest {

    private static Future<Integer> future;
    private static Integer n;

    @Before
    public void setUp() throws Exception {
        future = new Future<Integer>();
    }

    @Test
    public void get() {
        Thread t1 = new Thread(()-> System.out.println(future.get()));
        t1.start();
        try{
            t1.join(100);
        }catch(Exception e){}
        assertEquals(Thread.State.WAITING,t1.getState());
        future.resolve(2);
        try{
            t1.join();
        }catch(Exception e){}
        assertEquals(Thread.State.TERMINATED,t1.getState());
        assertEquals(new Integer(2), future.get());
    }

    @Test
    public void resolve(){
        future.resolve(2);
        assertEquals(new Integer(2), future.get());
    }

    @Test
    public void isDone() {
        assertFalse(future.isDone()); 
        future.resolve(new Integer(2));
        assertTrue(future.isDone()); 
    }

    @Test
    public void testGet() {     
        Thread t1 = new Thread(() ->{
            try{
                n = future.get(4,TimeUnit.SECONDS);
            }catch(Exception e){ }
        });
        t1.start();
        try{
            t1.join();
        }catch(Exception e){}
        assertNull(n);
        future.resolve(new Integer(2));
        n = future.get(4,TimeUnit.SECONDS);
        assertEquals(n, new Integer(2));
    }
}