/*
 * @(#)Pointcuts.java     10 Jul 2008
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
package com.qrmedia.commons.aspect.pointcut;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.qrmedia.commons.aspect.advice.BasicProfilingAdvice;
import com.qrmedia.commons.aspect.advice.MethodExecutionTracingAdvice;

/**
 * Defines AspectJ pointcuts for Commons/Aspect advice. See {@link com.qrmedia.commons.aspect}
 * for recommendations.
 * 
 * @author anph
 * @since 10 Jul 2008
 * 
 */
@Aspect
public final class Pointcuts {

    /**
     * The execution of a method that should be profiled by the {@link BasicProfilingAdvice}.
     */
    @Pointcut("com.qrmedia.commons.aspect.example.AdviceDemoPointcuts.noArgumentMethods()")
    public void profiledOperation() { }

    /**
     * The execution of a method that should be traced by the {@link MethodExecutionTracingAdvice}.
     */
    @Pointcut("com.qrmedia.commons.aspect.example.AdviceDemoPointcuts.argumentMethods()")
    public void tracedOperation() { }
}