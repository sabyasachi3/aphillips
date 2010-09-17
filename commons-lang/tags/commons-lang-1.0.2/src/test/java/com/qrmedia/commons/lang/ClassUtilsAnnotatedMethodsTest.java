/*
 * @(#)ClassUtilsTypeArgumentsTest.java     6 Mar 2009
 * 
 * Copyright © 2009 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package com.qrmedia.commons.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for the {@link ClassUtils#getAnnotatedMethods(Class, Class)} method.
 * 
 * @author aphillips
 * @since 6 Mar 2009
 * @see ClassUtilsTest
 *
 */
@RunWith(value = Parameterized.class)
public class ClassUtilsAnnotatedMethodsTest {
    private final Class<?> clazz;
    private final Class<? extends Annotation> annotationType;
    private final boolean validArguments;
    private final Set<Method> expectedMethods;
    
    @SuppressWarnings("unused")
    private static class StubObject {
        public static void unannotatedStaticMethod() {}
        
        @Resource
        public static void resourceStaticMethod() {}
        
        public void unannotatedMethod() {}
        
        // @Generated does not have runtime retention!
        @Generated("")
        public void generatedMethod() {}
        
        @Resource
        public void resourceMethod() {}
    }
    
    @Parameters
    public static Collection<Object[]> data() throws Exception {
        List<Object[]> data = new ArrayList<Object[]>();
        
        // invalid arguments
        data.add(new Object[] { null, null, false, null });
        
        // annotations for which no methods are found
        data.add(new Object[] { StubObject.class, null, true, new HashSet<Method>() });
        data.add(new Object[] { StubObject.class, Generated.class, true, new HashSet<Method>() });
        data.add(new Object[] { StubObject.class, Target.class, true, new HashSet<Method>() });
        
        data.add(new Object[] { StubObject.class, Resource.class, true,
                                asSet(getMethod("resourceStaticMethod"), getMethod("resourceMethod")) });        
        return data;
    }
    
    private static Method getMethod(String methodName) throws Exception {
        return StubObject.class.getMethod(methodName);
    }
    
    // can't use SetUtils from qrmedia's commons-collections...*sigh*...
    private static <T> Set<T> asSet(T... objs) {
        return new HashSet<T>(Arrays.asList(objs));
    }

    // called for each parameter set in the test data
    public ClassUtilsAnnotatedMethodsTest(Class<?> clazz,
            Class<? extends Annotation> annotationType, boolean validArguments,
            Set<Method> expectedMethods) {
        this.clazz = clazz;
        this.annotationType = annotationType;
        this.validArguments = validArguments;
        this.expectedMethods = expectedMethods;
    }

    @Test
    public void getAnnotatedMethods_invalid() {
        
        if (!validArguments) {
            
            try {
                ClassUtils.getAnnotatedMethods(clazz, annotationType);
                fail();
            } catch (IllegalArgumentException exception) {
                // expected
            }
            
        }
        
    }   
    
    @Test
    public void getAnnotatedMethods() {
        
        if (validArguments) {
            assertEquals(expectedMethods,  ClassUtils.getAnnotatedMethods(clazz, annotationType));
        }
        
    }
    
}
