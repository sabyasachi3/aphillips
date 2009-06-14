/*
 * @(#)AbstractLoggingAdviceTest.java     21 May 2009
 */
package com.qrmedia.commons.aspect.advice;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertTrue;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;

import com.qrmedia.commons.aspect.advice.AbstractLoggingAdvice;

/**
 * Unit tests for the {@link AbstractLoggingAdvice}.
 * 
 * @author aphillips
 * @since 21 May 2009
 *
 */
public class AbstractLoggingAdviceTest {
    private final JoinPoint joinPoint = createMock(JoinPoint.class);
    private final Signature signature = createMock(Signature.class);
    
    @Before
    public void prepareFixture() {
        
        // not all calls to the join point or signature are being checked
        joinPoint.getSignature();
        expectLastCall().andStubReturn(signature);
        
        signature.getName();
        expectLastCall().andReturn("advisedMethod");
    }
    
    @Test
    public void toShortJoinPointString_useTargetClassName() {
        Object target = new Object();
        joinPoint.getTarget();
        expectLastCall().andReturn(target);
        replay(joinPoint, signature);
        
        assertTrue(AbstractLoggingAdvice.toShortJoinPointString(joinPoint, true)
                   .contains(target.getClass().getSimpleName()));
        
        verify(joinPoint, signature);
    }
    
    @Test
    public void toShortJoinPointString_useDeclaringClassName() {
        Class<Integer> declaringType = Integer.class;
        signature.getDeclaringType();
        expectLastCall().andReturn(declaringType);
        replay(joinPoint, signature);
        
        assertTrue(AbstractLoggingAdvice.toShortJoinPointString(joinPoint, false)
                   .contains(declaringType.getSimpleName()));
        
        verify(joinPoint, signature);
    }
    
}
