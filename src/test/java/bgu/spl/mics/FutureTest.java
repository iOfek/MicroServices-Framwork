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
        assertEquals(Thread.State.BLOCKED,t1.getState());
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

        Thread t2 = new Thread(() ->{
            try{
                n = future.get(10,TimeUnit.SECONDS);
            }catch(Exception e){ }
        });
        t2.start();
        assertNull(n);
        try {
            Thread.sleep(200);
        } catch (Exception e) { }
        future.resolve(2);
        assertEquals(new Integer(2),future.get());
        
    }
}