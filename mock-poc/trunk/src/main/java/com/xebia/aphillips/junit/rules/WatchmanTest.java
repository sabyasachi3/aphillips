package com.xebia.aphillips.junit.rules;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;

/**
 * TestWatchman is a base class for Rules that take note of the testing action, 
 * without modifying it. For example, this class will keep a log of each passing 
 * and failing test.
 */
public class WatchmanTest {
    private final StringBuilder watchedLog = new StringBuilder();

    @Rule
    public MethodRule watchman = new TestWatchman() {
        @Override
        public void failed(Throwable e, FrameworkMethod method) {
            watchedLog.append(method.getName()).append(" returned ")
            .append(e.getClass().getSimpleName()).append(": ")
            .append(e.getMessage());
        }

        @Override
        public void succeeded(FrameworkMethod method) {
            watchedLog.append(method.getName());
        }
    };

    @Test
    public void fails() {
        fail("Failed!");
    }

    @Test
    public void succeeds() {
        assertTrue(true);
    }
    
    @After
    public void printLog() {
        System.out.println(watchedLog.toString());
    }
}
