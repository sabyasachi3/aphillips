/*
 * @(#)MethodExecutionTracingAdvice.java     10 Jul 2008
 * 
 * Copyright © 2009 Andrew Phillips.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.qrmedia.commons.aspect.advice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.qrmedia.commons.log.LoggingUtils;

/**
 * Logs method entrances and exits if the log level is TRACE or less.
 * <p>
 * <strong>Assumes a valid pointcut definition at 
 * {@code com.qrmedia.commons.aspect.pointcut.Pointcuts.tracedOperation()}.</strong> In 
 * other words, a class of type {@code com.qrmedia.commons.aspect.pointcut.Pointcuts} must 
 * be on the classpath and its method {@code tracedOperation} a valid pointcut definition.
 *  
 * @author anph
 * @since 10 Jul 2008
 *
 */
@Aspect
public class MethodExecutionTracingAdvice extends AbstractLoggingAdvice {
    
    @Around("com.qrmedia.commons.aspect.pointcut.Pointcuts.tracedOperation()")
    public Object traceMethodExecution(ProceedingJoinPoint proceedingJoinPoint) 
            throws Throwable {
        
        // if the result will not be output anyway, don't bother profiling 
        if (!log.isTraceEnabled()) {
            return proceedingJoinPoint.proceed();
        }
        
        logMethodEntry(proceedingJoinPoint);
        Object advisedMethodResult = proceedingJoinPoint.proceed();
        logMethodExit(proceedingJoinPoint, advisedMethodResult);
        
        return advisedMethodResult;
    }
    
    private void logMethodEntry(JoinPoint joinPoint) {
        logMethodExecution("Enter", joinPoint, null, false);
    }

    private void logMethodExit(JoinPoint joinPoint, Object result) {
        logMethodExecution("Exit", joinPoint, result, true);
    }

    private void logMethodExecution(String action, JoinPoint joinPoint, Object result, 
                                    boolean displayResult) {
        StringBuilder logMessage = new StringBuilder();
        
        // format: "<action>ing '<method name>' with argument(s) [<arguments>]"
        logMessage.append(action).append("ing '").append(toShortJoinPointString(joinPoint, true))
        .append("' with argument(s) ");
        
        String argsString = LoggingUtils.arrayToString(joinPoint.getArgs(), true);
        
        logMessage.append(argsString).append(".");
        
        if (displayResult && returnsResult(joinPoint)) {
            logMessage.append(" Result: ").append((result != null) 
                    ? LoggingUtils.toPackageNameFreeString(result.toString())
                    : null);
        }
        
        log.trace(logMessage.toString());
    }
    
    // assume that any method with " void " in its signature does not return a value
    private static boolean returnsResult(JoinPoint joinPoint) {
        return !joinPoint.getSignature().toLongString().contains(" void ");
    }

}
