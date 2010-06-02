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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for the {@link ClassUtils#getActualTypeArguments(Class, Class)} method.
 * 
 * @author aphillips
 * @since 6 Mar 2009
 * @see ClassUtilsTest
 *
 */
@RunWith(value = Parameterized.class)
public class ClassUtilsTypeArgumentsTest {
    private final Class<?> typedSuperclass;
    private final Class<?> typedClass;
    private final boolean validArguments;
    private final List<Class<?>> expectedTypeArguments;
    
    private static class TypedClass<U, V, W> {}
    
    private static class TypedSubclass<S, T> extends TypedClass<Integer, S, T> {}
    
    private static class TypedSubclass2<M> extends TypedSubclass<M, Long> {}
    
    // use the same type parameter letter as in the parent class
    private static class TypedSubclass3<T> extends TypedSubclass<T, Long> {}
    
    private static class TypedSubclass4 extends TypedSubclass2<String> {}
    
    // a subclass whose parent *isn't* a generic type
    private static class TypedSubclass5 extends TypedSubclass4 {}
    
    // a subclass whose parent *is* a generic type but arguments are not declared
    @SuppressWarnings("rawtypes")
    private static class TypedSubclass6 extends TypedSubclass3 {}
    
    private static interface TypedInterface<N, O> {}
    
    private static interface TypedSubinterface<P> extends TypedInterface<P, Short> {}
    
    private static class TypedClass2 implements TypedSubinterface<Byte> {}
    
    private static interface TypedSubinterface2 extends TypedSubinterface<Byte> {}
    
    private static class TypedClass3 implements TypedSubinterface2 {}
    
    @SuppressWarnings("rawtypes")
    private static class TypedClass4 implements TypedSubinterface {}
    
    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> data = new ArrayList<Object[]>();
        
        // invalid arguments
        data.add(new Object[] { new TypedClass<Integer, Short, Byte>().getClass(), null, false, null });
        data.add(new Object[] { null, TypedClass.class, false, null });        
        
        // the "subclass" is not actually a subclass of the superclass
        data.add(new Object[] { TypedClass.class, TypedClass2.class, true, null });
        
        // some classes for which not all type information can be determined
        data.add(new Object[] { new TypedClass<Integer, Short, Byte>().getClass(), TypedClass.class, 
                                true, Arrays.asList(null, null, null) });
        data.add(new Object[] { new TypedSubclass<Short, Byte>().getClass(), TypedClass.class, true,
                                Arrays.<Object>asList(Integer.class, null, null) });        
        data.add(new Object[] { new TypedSubclass2<Byte>().getClass(), TypedClass.class, true,
                                Arrays.<Object>asList(Integer.class, null, Long.class) });
        data.add(new Object[] { new TypedSubclass3<Byte>().getClass(), TypedClass.class, true,
                                Arrays.<Object>asList(Integer.class, null, Long.class) });        
        data.add(new Object[] { new TypedSubclass2<Byte>().getClass(), TypedSubclass2.class, true,
                                Arrays.<Class<?>>asList((Class<?>) null) });        
        data.add(new Object[] { new TypedSubclass3<Byte>().getClass(), TypedSubclass3.class, true,
                                Arrays.<Class<?>>asList((Class<?>) null) }); 
        data.add(new Object[] { TypedSubclass6.class, TypedSubclass.class, true, 
                                Arrays.<Object>asList(null, Long.class) });  
        data.add(new Object[] { TypedClass4.class, TypedInterface.class, true, 
                                Arrays.<Object>asList(null, Short.class) });           
        
        // classes for which all information should be retrievable
        data.add(new Object[] { TypedSubclass4.class, TypedClass.class, true,
                                Arrays.<Object>asList(Integer.class, String.class, Long.class) });        
        data.add(new Object[] { TypedSubclass4.class, TypedSubclass.class, true,
                                Arrays.<Object>asList(String.class, Long.class) });
        data.add(new Object[] { TypedSubclass4.class, TypedSubclass2.class, true, 
                                Arrays.<Object>asList(String.class) });
        data.add(new Object[] { TypedSubclass4.class, TypedSubclass4.class, true, 
                                new ArrayList<Object>() });
        data.add(new Object[] { TypedSubclass5.class, TypedSubclass2.class, true, 
                                Arrays.<Object>asList(String.class) });
        
        // interfaces
        data.add(new Object[] { TypedClass2.class, TypedInterface.class, true, 
                                Arrays.<Object>asList(Byte.class, Short.class) });            
        data.add(new Object[] { TypedClass2.class, TypedSubinterface.class, true, 
                                Arrays.<Object>asList(Byte.class) });     
        data.add(new Object[] { TypedClass3.class, TypedSubinterface.class, true, 
                                Arrays.<Object>asList(Byte.class) });           
        
        return data;
    }

    // called for each parameter set in the test data
    public ClassUtilsTypeArgumentsTest(Class<?> typedClass, Class<?> typedSuperclass, 
            boolean validArguments, List<Class<?>> expectedTypeArguments) {
        this.typedClass = typedClass;
        this.typedSuperclass = typedSuperclass;
        this.validArguments = validArguments;
        this.expectedTypeArguments = expectedTypeArguments;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void getActualTypeArguments_invalid() {
        
        if (!validArguments) {
            
            try {
                // the cast to the generic-less (Class) is required to avoid a compiler error                
                ClassUtils.getActualTypeArguments(typedClass, (Class) typedSuperclass);
                fail();
            } catch (IllegalArgumentException exception) {
                // expected
            }
            
        }
        
    }   
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void getActualTypeArguments() {
        
        if (validArguments) {
            // the cast to the generic-less (Class) is required to avoid a compiler error 
            assertEquals(expectedTypeArguments, 
                         ClassUtils.getActualTypeArguments(typedClass, (Class) typedSuperclass));
        }
        
    }
    
}
