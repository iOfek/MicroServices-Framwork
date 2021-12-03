package bgu.spl.mics;

import org.junit.Before;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class FutureTest {
    private static Future<Integer> future;
    private static Future<Integer> test1;
    private static Future<Integer> test2;
    @Before
    public void setUp() throws Exception {
        future = new Future<Integer>();
        test1 = new Future<Integer>();
        test2 = new Future<Integer>();
    }

    @Test
    public void get() {
        Thread t1 = new Thread(()-> System.out.println(test1.get()));
        t1.start();
        try{
            t1.join(10);
        }catch(Exception e){}
        assertEquals(Thread.State.WAITING,t1.getState());
        test1.resolve(2);
        assertEquals(Thread.State.BLOCKED,t1.getState());
        assertEquals(new Integer(2), test1.get());
    }

    @Test
    public void resolve(){
        test2.resolve(2);
        assertEquals(new Integer(2), test2.get());
    }

    @Test
    public void isDone() {
        assertFalse(future.isDone()); 
        future.resolve(new Integer(2));
        assertTrue(future.isDone()); 
    }

    @Test
    public void testGet() {
    }
}