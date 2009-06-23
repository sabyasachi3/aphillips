/*
 * @(#)AbstractMtcTest.java     26 May 2009
 */
package com.xebia.aphillips.multithreadedtc;

import org.junit.Test;

import edu.umd.cs.mtc.MultithreadedTest;
import edu.umd.cs.mtc.TestFramework;

/**
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
