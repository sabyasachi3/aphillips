/*
 * @(#)MTCCompareAndSet.java     26 May 2009
 */
package com.xebia.aphillips.multithreadedtc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * MultithreadedTC timing demo.
 * 
 * @author aphillips
 * @since 26 May 2009
 * 
 */
public class MtcTimedOffer extends AbstractMtcTest {
    ArrayBlockingQueue<Object> q;

    @Override
    public void initialize() {
        q = new ArrayBlockingQueue<Object>(2);
    }

    public void thread1() {
        try {
            q.put(new Object());
            q.put(new Object());

            freezeClock();
            assertFalse("Expected timeout", q.offer(new Object(), 25, TimeUnit.MILLISECONDS));
            unfreezeClock();

            q.offer(new Object(), 2500, TimeUnit.MILLISECONDS);
            fail("should throw exception");
        } catch (InterruptedException success) {
            assertTick(1);
        }
    }

    public void thread2() {
        waitForTick(1);
        getThread(1).interrupt();
    }
}
