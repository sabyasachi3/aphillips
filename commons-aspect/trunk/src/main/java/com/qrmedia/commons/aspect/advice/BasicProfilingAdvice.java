/*
 * @(#)BasicProfilingAdvice.java     10 Jul 2008
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import com.qrmedia.commons.log.LoggingUtils;

/**
 * Logs method call information and execution time if the log level is DEBUG or lower.
 * <p>
 * <strong>Assumes a valid pointcut definition at 
 * {@code com.qrmedia.commons.aspect.pointcut.Pointcuts.profiledOperation()}.</strong> In 
 * other words, a class of type {@code com.qrmedia.commons.aspect.pointcut.Pointcuts} must 
 * be on the classpath and its method {@code profiledOperation} a valid pointcut definition.

 * @author anph
 * @since 10 Jul 2008
 *
 */
@Aspect
/*
 * Defaults to lowest priority, which means executed last in the chain. This is what is
 * desired since the profiling should execute as closely to the actual target code as
 * possible.
 */
@Order
public class BasicProfilingAdvice extends AbstractLoggingAdvice {
    private static final int MAX_ARGS_STRING_LENGTH = 256;
    
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat();
    
    @Around("com.qrmedia.commons.aspect.pointcut.Pointcuts.profiledOperation()")
    public Object profileMethodExecution(ProceedingJoinPoint proceedingJoinPoint) 
            throws Throwable {

        // if the result will not be output anyway, don't bother profiling 
        if (!log.isDebugEnabled()) {
            return proceedingJoinPoint.proceed();
        }
        
        long startTime = System.currentTimeMillis();
        Object advisedMethodResult = proceedingJoinPoint.proceed();
        logMethodExecutionProfile(proceedingJoinPoint, System.currentTimeMillis() - startTime);
        
        return advisedMethodResult;
    }
    
    private void logMethodExecutionProfile(JoinPoint joinPoint, long executionTime) {
        StringBuilder logMessage = new StringBuilder();
        String nowString = DATE_FORMATTER.format(new Date());
        String methodName = toShortJoinPointString(joinPoint);
        
        // format: "<readable message>",<timestamp>,<method name>,"<arguments>",<execution time>
        logMessage.append("\"Execution of '").append(methodName).append("' at ").append(nowString)
        .append(" with arguments ");
        
        // note: an empty args array is also returned for methods that *don't take arguments* 
        String argsString = LoggingUtils.arrayToString(joinPoint.getArgs(), true, 
                                                       MAX_ARGS_STRING_LENGTH);
        
        logMessage.append(argsString).append(" completed in ").append(executionTime).append("ms\",")
        .append(nowString).append(",").append(methodName).append(",\"").append(argsString)
        .append("\",").append(executionTime);
        
        log.debug(logMessage.toString());
    }
    
}
