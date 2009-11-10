/*
 * @(#)AbstractMtcTest.java     26 May 2009
 */
package com.xebia.aphillips.multithreadedtc;

import org.junit.Test;

import edu.umd.cs.mtc.MultithreadedTest;
import edu.umd.cs.mtc.TestFramework;

/**
 * See the <a href="http://www.cs.umd.edu/projects/PL/multithreadedtc/overview.html">
 * Multithreaded TC documentation</a>. Source is available at <a href="http://code.google.com/p/multithreadedtc/">
 * Google Code</a>.
 *  
 * @author aphillips
 * @since 26 May 2009
 *
 */
public class AbstractMtcTest extends MultithreadedTest {

    @Test
    public void testMultithreadedCode() throws Throwable {
        TestFramework.runOnce( getClass().getConstructor(new Class[0]).newInstance(new Object[0]) );
    }  
    
}
