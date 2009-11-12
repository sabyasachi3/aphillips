/*
 * @(#)ClassUtilsTypeArgumentsTest.java     6 Mar 2009
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
    
    private static interface TypedInterface<N, O> {}
    
    private static interface TypedSubinterface<P> extends TypedInterface<P, Short> {}
    
    private static class TypedClass2 implements TypedSubinterface<Byte> {}
    
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
    
    @SuppressWarnings("unchecked")
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
    
    @SuppressWarnings("unchecked")
    @Test
    public void getActualTypeArguments() {
        
        if (validArguments) {
            // the cast to the generic-less (Class) is required to avoid a compiler error 
            assertEquals(expectedTypeArguments, 
                         ClassUtils.getActualTypeArguments(typedClass, (Class) typedSuperclass));
        }
        
    }
    
}
