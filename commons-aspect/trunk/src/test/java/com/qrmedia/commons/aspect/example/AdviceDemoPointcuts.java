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
package com.qrmedia.commons.aspect.example;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


/**
 * Demonstration pointcuts on the {@link AdvisedObject} object.
 * 
 * @author anph
 * @since 10 Jul 2008
 * 
 */
@Aspect
public final class AdviceDemoPointcuts {

    /**
     * The execution of a no-argument method of the {@link AdvisedObject} object.
     */
    @Pointcut("execution(* com.qrmedia.commons.aspect.example.AdvisedObject.*())")
    public void noArgumentMethods() { }

    /**
     * The execution of a method of the {@link AdvisedObject} object with arguments.
     */
    @Pointcut("!noArgumentMethods()")
    public void argumentMethods() { }
}