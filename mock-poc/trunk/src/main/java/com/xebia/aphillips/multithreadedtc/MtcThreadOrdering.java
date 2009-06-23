/*
 * @(#)MtcInterruptedAcquire.java     26 May 2009
 */
package com.xebia.aphillips.multithreadedtc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * MultithreadedTC ordering demo.
 * 
 * @author aphillips
 * @since 26 May 2009
 * 
 */
public class MtcThreadOrdering extends AbstractMtcTest {
    AtomicInteger ai;

    @Override
    public void initialize() {
        ai = new AtomicInteger(0);
    }

    public void thread1() throws Exception {
        assertTrue(ai.compareAndSet(0, 1)); // S1
        waitForTick(3);
        assertEquals(ai.get(), 3); // S4
    }

    public void thread2() {
        waitForTick(1);
        assertTrue(ai.compareAndSet(1, 2)); // S2
        waitForTick(3);
        assertEquals(3, ai.get()); // S4
    }

    public void thread3() {
        waitForTick(2);
        assertTrue(ai.compareAndSet(2, 3)); // S3
    }
}
