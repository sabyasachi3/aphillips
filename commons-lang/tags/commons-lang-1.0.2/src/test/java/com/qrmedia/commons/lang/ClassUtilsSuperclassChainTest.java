/*
 * @(#)ClassUtilsSuperclassChainTest.java     6 Mar 2009
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
@SuppressWarnings({ "rawtypes", "unchecked" })
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
        data.add(new Object[] { Object.class, String.class, true, Collections.EMPTY_SET });
        data.add(new Object[] { Interface1.class, Object.class, true, Collections.EMPTY_SET });
        data.add(new Object[] { Object.class, Interface1.class, true, Collections.EMPTY_SET });
        data.add(new Object[] { Interface1.class, Cloneable.class, true, Collections.EMPTY_SET });
        
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
            if (expectedSuperclassChains.isEmpty()) {
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
