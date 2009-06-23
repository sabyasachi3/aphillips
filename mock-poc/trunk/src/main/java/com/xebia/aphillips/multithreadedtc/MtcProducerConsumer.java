/*
 * @(#)MultithreadedTCDemo.java     26 May 2009
 */
package com.xebia.aphillips.multithreadedtc;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * MultithreadedTC demo.
 * 
 * @author aphillips
 * @since 26 May 2009
 *
 */
public class MtcProducerConsumer extends AbstractMtcTest {
    private ArrayBlockingQueue<Integer> buf;
    
    @Override 
    public void initialize() {
        buf = new ArrayBlockingQueue<Integer>(1);
    }

    public void thread1() throws InterruptedException {
        buf.put(42);
        buf.put(17);
        assertTick(1);
    }

    public void thread2() throws InterruptedException {
        waitForTick(1);
        assertEquals(Integer.valueOf(42), buf.take());
        assertEquals(Integer.valueOf(17), buf.take());
    }

    @Override 
    public void finish() {
        assertTrue(buf.isEmpty());
    }
    
}
