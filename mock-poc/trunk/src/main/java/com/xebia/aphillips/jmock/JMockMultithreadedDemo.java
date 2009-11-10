/*
 * @(#)MultithreadedTest.java     26 May 2009
 */
package com.xebia.aphillips.jmock;

import java.util.concurrent.Executor;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.DeterministicExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Demo multithreaded testing with JMock. See the 
 * <a href="http://jmock.org/">JMock documentation</a> for more information.
 * 
 * @author aphillips
 * @since 26 May 2009
 *
 */
@RunWith(JMock.class)
public class JMockMultithreadedDemo {
    private final Mockery context = new JUnit4Mockery();
    
    interface Alarm {
        void ring();
    }
    
    class Guard {
        protected final Alarm alarm;
        
        Guard( Alarm alarm ) {
            this.alarm = alarm;
        }
        
        void notice(Object burglar) {
            startRingingTheAlarm();
        }
        
        protected void startRingingTheAlarm() {
            Runnable ringAlarmTask = new Runnable() {
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        alarm.ring();
                    }
                }
            };
            
            Thread ringAlarmThread = new Thread(ringAlarmTask);
            ringAlarmThread.start();
        }
    }
    
    @Test
    public void ringsTheAlarmOnceWhenNoticesABurglar() throws InterruptedException {
        final Alarm alarm = context.mock(Alarm.class);
        Guard guard = new Guard(alarm);
        
        context.checking(new Expectations() {{
            oneOf (alarm).ring();
        }});
        
        guard.notice(new Object());
        
        // give the frantically ringing alarm thread a bit of time before validating
        Thread.sleep(200);
    }
    
    class ExecutorGuard extends Guard {
        private final Executor executor;
        
        ExecutorGuard(Alarm alarm, Executor executor) {
            super(alarm);
            this.executor = executor;
        }

        @Override
        protected void startRingingTheAlarm() {
            Runnable ringAlarmTask = new Runnable() {
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        alarm.ring();
                    }
                }
            };
            executor.execute(ringAlarmTask);
        }
       
    }
    
    @Test
    public void ringsTheAlarmOnceWhenNoticesABurglar2() {
        final Alarm alarm = context.mock(Alarm.class);
        DeterministicExecutor executor = new DeterministicExecutor();
        Guard guard = new ExecutorGuard(alarm, executor);
        
        guard.notice(new Object());
        
        context.checking(new Expectations() {{
            oneOf (alarm).ring();
        }});
        
        executor.runUntilIdle();    
    }
}
