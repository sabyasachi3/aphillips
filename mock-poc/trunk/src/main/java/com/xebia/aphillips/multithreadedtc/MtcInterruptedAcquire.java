/*
 * @(#)MtcInterruptedAcquire.java     26 May 2009
 */
package com.xebia.aphillips.multithreadedtc;

import java.util.concurrent.Semaphore;

/**
 * MultithreadedTC interruption demo.
 * 
 * @author aphillips
 * @since 26 May 2009
 *
 */
public class MtcInterruptedAcquire extends AbstractMtcTest {
    Semaphore s;
    
    @Override
    public void initialize() {
        s = new Semaphore(0);
    }

    public void thread1() {
        try {
            s.acquire();
            fail("should throw exception");
        } catch(InterruptedException success){ 
            assertTick(1); 
        }
    }

    public void thread2() {
        waitForTick(1);
        getThread(1).interrupt();
    }
    
}
