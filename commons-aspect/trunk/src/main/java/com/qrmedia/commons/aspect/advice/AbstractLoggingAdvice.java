/*
 * @(#)AbstractLoggingAdvice.java     18 Feb 2009
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

/**
 *
 * @author anph
 * @since 18 Feb 2009
 *
 */
public abstract class AbstractLoggingAdvice {
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * Returns a string representation of the target join point, of the form
     * <code>DefiningClass.method</code>.
     * 
     * @param joinPoint the join point to be represented as a string
     * @return  a string representation of the join point
     */
    protected static String toShortJoinPointString(JoinPoint joinPoint) {
        return toShortJoinPointString(joinPoint, false);
    }
    
    /**
     * Returns a string representation of the target join point, of the form
     * <code>DefiningClass.targetMethod</code> or <code>TargetClass.targetMethod</code>.
     * 
     * @param joinPoint the join point to be represented as a string
     * @param useTargetClassName <code>true</code> if the class name of the <u>target</u>
     *                           object should be used; otherwise, the class name of the
     *                           class defining the join point will be used 
     * @return  a string representation of the join point
     */
    protected static String toShortJoinPointString(JoinPoint joinPoint, boolean useTargetClassName) {
        Signature signature = joinPoint.getSignature();
        return (useTargetClassName ? joinPoint.getTarget().getClass().getSimpleName()
                                   : signature.getDeclaringType().getSimpleName()) 
               + "." + signature.getName();
    }    
    
}
