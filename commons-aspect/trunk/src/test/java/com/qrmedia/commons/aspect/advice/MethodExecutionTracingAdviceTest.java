/*
 * @(#)MethodExecutionTracingAdviceTest.java     21 May 2009
 */
package com.qrmedia.commons.aspect.advice;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.verify;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;

import com.qrmedia.commons.aspect.advice.MethodExecutionTracingAdvice;
import com.qrmedia.commons.reflect.ReflectionUtils;

/**
 * Unit tests for the {@link MethodExecutionTracingAdvice}.
 * 
 * @author aphillips
 * @since 21 May 2009
 *
 */
public class MethodExecutionTracingAdviceTest {
    private final MethodExecutionTracingAdvice advice = new MethodExecutionTracingAdvice();
    
    private final Log log = createMock(Log.class);
    private final ProceedingJoinPoint joinPoint = createMock(ProceedingJoinPoint.class);
    
    @Before
    public void prepareFixture() throws IllegalAccessException {
        // inject the mocked Log into the class
        ReflectionUtils.setValue(advice, "log", log);
    }

    /** 
     * If trace logging is not enabled, the join point should simply be invoked. 
     */
    @Test
    public void traceMethodExecution_noTraceLogging() throws Throwable {
        log.isTraceEnabled();
        expectLastCall().andReturn(false);
        
        Object result = new Object();
        joinPoint.proceed();
        expectLastCall().andReturn(result);
        replay(log, joinPoint);
        
        assertSame(result, advice.traceMethodExecution(joinPoint));
        
        verify(log, joinPoint);
    }
    
    /**
     * Verifies that the targeted method, arguments and return value are
     * present in the log messages.
     */
    @Test
    public void traceMethodExecution() throws Throwable {
        log.isTraceEnabled();
        expectLastCall().andReturn(true);
        
        // two messages are constructed, so two calls to the mocks are made
        Integer target = 7;
        joinPoint.getTarget();
        expectLastCall().andReturn(target).times(2);

        Signature signature = createMock(Signature.class);
        String advisedMethodName = "advisedMethod";
        signature.getName();
        expectLastCall().andReturn(advisedMethodName).times(2);

        /*
         * Only called when constructing the exit message. " void " in the signature's 
         * LongString would be a method without a result.
         */
        signature.toLongString();
        expectLastCall().andReturn("private long " + advisedMethodName);
        
        // called twice during the construction of the exit message
        joinPoint.getSignature();
        expectLastCall().andReturn(signature).times(3);
        
        /*
         * Using arguments whose toString won't contain a package name, as that would be
         * harder to test (logMessage.contains(args.toString()) wouldn't work because the 
         * package names are stripped).
         */
        Object[] args = new Integer[] { 0, 0, 7 };
        joinPoint.getArgs();
        expectLastCall().andReturn(args).times(2);
        
        Capture<String> entryMessageCapture = new Capture<String>();
        log.trace(capture(entryMessageCapture));
        expectLastCall();
        
        // again, for simplicity's sake choose a result with a package-free toString
        Object result = '7';
        joinPoint.proceed();
        expectLastCall().andReturn(result);
        
        Capture<String> exitMessageCapture = new Capture<String>();
        log.trace(capture(exitMessageCapture));
        expectLastCall();        
        replay(log, joinPoint, signature);
        
        assertSame(result, advice.traceMethodExecution(joinPoint));
        
        verify(log, joinPoint, signature);
        
        /*
         * Ensure the logged values contain key information: the signature of the
         * join point, arguments passed to the method and the returned value.
         */
        String entryMessage = entryMessageCapture.getValue();
        assertTrue(entryMessage.contains(target.getClass().getSimpleName()));
        assertTrue(entryMessage.contains(advisedMethodName));
        assertTrue(entryMessage.contains(Arrays.toString(args)));
        
        String exitMessage = exitMessageCapture.getValue();
        assertTrue(exitMessage.contains(target.getClass().getSimpleName()));
        assertTrue(exitMessage.contains(advisedMethodName));
        assertTrue(exitMessage.contains(Arrays.toString(args)));
        assertTrue(exitMessage.contains(result.toString()));
    }    
    
}
