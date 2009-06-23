/*
 * @(#)MTCCompareAndSet.java     26 May 2009
 */
package com.xebia.aphillips.multithreadedtc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * MultithreadedTC CAS demo.
 * 
 * @author aphillips
 * @since 26 May 2009
 * 
 */
public class MtcCompareAndSet extends AbstractMtcTest {
    private AtomicInteger ai;

    @Override
    public void initialize() {
        ai = new AtomicInteger(1);
    }

    public void thread1() {
        while (!ai.compareAndSet(2, 3))
            Thread.yield();
    }

    public void thread2() {
        assertTrue(ai.compareAndSet(1, 2));
    }

    @Override
    public void finish() {
        assertEquals(3, ai.get());
    }
    
}
