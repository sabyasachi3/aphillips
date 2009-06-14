/*
 * @(#)ClassUtilsSuperclassChainTest.java     6 Mar 2009
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for the {@link ClassUtils#getSuperclassChains(Class, Class)} and 
 * {@link ClassUtils#getSuperclassChain(Class, Class) getSuperclassChain(Class, Class)} methods.
 * 
 * @author aphillips
 * @since 6 Mar 2009
 * @see ClassUtilsTest
 *
 */
@RunWith(value = Parameterized.class)
@SuppressWarnings("unchecked")
public class ClassUtilsSuperclassChainTest {
    private final Class clazz;
    private final Class superclass;
    private final boolean validArguments;
    private final Set<List<Class<?>>> expectedSuperclassChains;

    private static interface Interface1 {}
    
    private static interface Interface2a extends Interface1 {}
    private static interface Interface2b extends Interface1 {}
    
    private static interface Interface3 extends Interface2a, Interface2b {}
    
    private static interface Interface4a extends Interface3, Cloneable {}
    private static interface Interface4b extends Interface3 {}
    
    private static interface Interface5 extends Interface4a, Interface4b {}
    
    private static class Class1 implements Interface4a, Cloneable {}
    
    private static class Class2 extends Class1 implements Interface5, Serializable {
        private static final long serialVersionUID = 1L;
    }
    
    private static class Class3 extends Class2 {
        private static final long serialVersionUID = 1L;
    }

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> data = new ArrayList<Object[]>();
        
        // invalid arguments
        data.add(new Object[] { null, null, false, null });
        data.add(new Object[] { null, Interface1.class, false, null });
        data.add(new Object[] { Class1.class, null, false, null });
        
        // nonexistent chains (class/interface -> class/interface)
        data.add(new Object[] { Object.class, String.class, true, null });
        data.add(new Object[] { Interface1.class, Object.class, true, null });
        data.add(new Object[] { Object.class, Interface1.class, true, null });
        data.add(new Object[] { Interface1.class, Cloneable.class, true, null });
        
        // class -> class chains
        data.add(new Object[] { Object.class, Object.class, true, 
                asSet(Arrays.asList(Object.class)) });
        data.add(new Object[] { Class3.class, Class2.class, true, 
                asSet(Arrays.asList(Class3.class, Class2.class)) });        
        data.add(new Object[] { Class3.class, Object.class, true, 
                asSet(Arrays.asList(Class3.class, Class2.class, Class1.class, Object.class)) });
        
        // class -> interface chains
        data.add(new Object[] { Class2.class, Interface5.class, true, 
                asSet(Arrays.asList(Class2.class, Interface5.class)) });
        data.add(new Object[] { Class3.class, Interface4a.class, true, asSet(
                Arrays.asList(Class3.class, Class2.class, Interface5.class, Interface4a.class),
                Arrays.asList(Class3.class, Class2.class, Class1.class, Interface4a.class)) });        
        data.add(new Object[] { Class2.class, Interface3.class, true, asSet(
                Arrays.asList(Class2.class, Interface5.class, Interface4a.class, Interface3.class),
                Arrays.asList(Class2.class, Interface5.class, Interface4b.class, Interface3.class),
                Arrays.asList(Class2.class, Class1.class, Interface4a.class, Interface3.class)) });        
        
        // interface -> interface chains
        data.add(new Object[] { Interface4a.class, Cloneable.class, true, 
                asSet(Arrays.asList(Interface4a.class, Cloneable.class)) });        
        data.add(new Object[] { Interface5.class, Interface1.class, true, asSet(
                Arrays.asList(Interface5.class, Interface4a.class, Interface3.class, 
                              Interface2a.class, Interface1.class),
                Arrays.asList(Interface5.class, Interface4a.class, Interface3.class, 
                              Interface2b.class, Interface1.class),                              
                Arrays.asList(Interface5.class, Interface4b.class, Interface3.class, 
                              Interface2a.class, Interface1.class),
                Arrays.asList(Interface5.class, Interface4b.class, Interface3.class, 
                              Interface2b.class, Interface1.class)) });
        
        return data;
    }

    // can't use SetUtils.asSet as that would cause a circular dependency
    private static <U> Set<U> asSet(U... objects) {
        return new HashSet<U>(Arrays.asList(objects));
    } 

    // called for each parameter set in the test data
    public ClassUtilsSuperclassChainTest(Class<?> clazz, Class<?> superclass, boolean validArguments,
            Set<List<Class<?>>> expectedSuperclassChains) {
        this.clazz = clazz;
        this.superclass = superclass;
        this.validArguments = validArguments;
        this.expectedSuperclassChains = expectedSuperclassChains;
    }
    
    @Test
    public void getSuperclassChain_invalid() {
        
        if (!validArguments) {
            
            try {
                ClassUtils.getSuperclassChain(clazz, superclass);
                fail();
            } catch (IllegalArgumentException exception) {
                
                // expected
            }
            
        }
        
    }   
    
    @Test
    public void getSuperclassChains_invalid() {
        
        if (!validArguments) {
            
            try {
                ClassUtils.getSuperclassChains(clazz, superclass);
                fail();
            } catch (IllegalArgumentException exception) {
                
                // expected
            }
            
        }
        
    }   
    
    @Test
    public void getSuperclassChain() {
        
        if (validArguments) {
            List<Class<?>> superclassChain = ClassUtils.getSuperclassChain(clazz, superclass);
            
            // the expected result is null if there is no chain between the arguments
            if (expectedSuperclassChains == null) {
                assertNull(superclassChain);
            } else {
                assertTrue(expectedSuperclassChains.contains(superclassChain));
            }
            
        }
        
    }    
    
    @Test
    public void getSuperclassChains() {
        
        if (validArguments) {
            assertEquals(expectedSuperclassChains, ClassUtils.getSuperclassChains(clazz, superclass));
        }
        
    }
    
}
