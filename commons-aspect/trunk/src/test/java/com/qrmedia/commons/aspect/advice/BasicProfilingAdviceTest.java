/*
 * @(#)BasicProfilingAdviceTest.java     21 May 2009
 */
package com.qrmedia.commons.aspect.advice;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.verify;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.easymock.Capture;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import com.qrmedia.commons.reflect.ReflectionUtils;

/**
 * Unit tests for the {@link BasicProfilingAdvice}.
 * 
 * @author aphillips
 * @since 21 May 2009
 *
 */
public class BasicProfilingAdviceTest {
    private final BasicProfilingAdvice advice = new BasicProfilingAdvice();
    
    private final Log log = createMock(Log.class);
    private final ProceedingJoinPoint joinPoint = createMock(ProceedingJoinPoint.class);
    
    @Before
    public void prepareFixture() {
        // inject the mocked Log into the class
        ReflectionUtils.setValue(advice, "log", log);
        
        // not all calls to the join point are checked
        Signature signature = createMock(Signature.class);
        signature.getDeclaringType();
        expectLastCall().andStubReturn(Object.class);
        signature.getName();
        expectLastCall().andStubReturn("advisedMethod");
        replay(signature);
        
        joinPoint.getSignature();
        expectLastCall().andStubReturn(signature);  
        joinPoint.getArgs();
        expectLastCall().andStubReturn(new Object[0]);
    }
    
    /** 
     * If debug logging is not enabled, the join point should simply be invoked. 
     */
    @Test
    public void profileMethodExecution_noDebugLogging() throws Throwable {
        log.isDebugEnabled();
        expectLastCall().andReturn(false);
        
        Object result = new Object();
        joinPoint.proceed();
        expectLastCall().andReturn(result);
        replay(log, joinPoint);
        
        assertSame(result, advice.profileMethodExecution(joinPoint));
        
        verify(log, joinPoint);
    }

    @Test
    public void profileMethodExecution() throws Throwable {
        log.isDebugEnabled();
        expectLastCall().andReturn(true);
        
        // pause for 100ms when the join point is invoked
        final Object result = new Object();
        final long simulatedRunTime = 100;
        joinPoint.proceed();
        expectLastCall().andAnswer(new IAnswer<Object>() {
    
                @Override
                public Object answer() throws Throwable {
                    Thread.sleep(simulatedRunTime);
                    return result;
                }
                
            });
        
        // a log entry should be written with profiling details
        Capture<String> logMessageCapture = new Capture<String>();
        log.debug(capture(logMessageCapture));
        replay(log, joinPoint);
        
        assertSame(result, advice.profileMethodExecution(joinPoint));
        
        verify(log, joinPoint);
        
        final Matcher expectedMessagePatternMatcher = 
            Pattern.compile("^\"[^\"]*\",(?:[^,]*,){2}\"[^\"]*\",(\\d+)$")
            .matcher(logMessageCapture.getValue());
        assertTrue(expectedMessagePatternMatcher.matches());
        
        /*
         * Verify that the reported runtime is within 10% of the simulated runtime. This
         * makes the test a bit brittle, of course, but such a long delay should happen very
         * rarely.
         */
        long reportedRunTime = Long.parseLong(expectedMessagePatternMatcher.group(1));
        assertTrue(Math.abs((simulatedRunTime - reportedRunTime) / simulatedRunTime) 
                   < (simulatedRunTime / 10));
    }

}
