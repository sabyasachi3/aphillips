/*
 * @(#)ClassUtilsTest.java     9 Feb 2009
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

/**
 * Unit tests for the {@link ClassUtils}.
 * 
 * @author anph
 * @since 9 Feb 2009
 * @see ClassUtilsTypeArgumentsTest
 *
 */
public class ClassUtilsTest {

    @SuppressWarnings("unused")
    private static class Parent {
        public static int publicStaticParentProperty;
        protected static int protectedStaticParentProperty;
        static int packageStaticParentProperty;
        private static int privateStaticParentProperty;
        
        public int publicParentProperty;
        protected int protectedParentProperty;
        int packageParentProperty;
        private int privateParentProperty;
    }
    
    @SuppressWarnings("unused")
    private static class Child extends Parent {
        public static int publicStaticChildProperty;
        protected static int protectedStaticChildProperty;
        static int packageStaticChildProperty;
        private static int privateStaticChildProperty;
        
        public int publicChildProperty;
        protected int protectedChildProperty;
        int packageChildProperty;
        private int privateChildProperty;
    }
    
    @Test
    public void getAllDeclaredFields() {
        List<Field> expectedFields = new ArrayList<Field>();
        expectedFields.addAll(Arrays.asList(Parent.class.getDeclaredFields()));
        expectedFields.addAll(Arrays.asList(Child.class.getDeclaredFields()));
        
        assertTrue(CollectionUtils.isEqualCollection(
                expectedFields, 
                ClassUtils.getAllDeclaredFields(Child.class)));
    }

    @SuppressWarnings("unused")
    private static class Grandchild extends Child {
        public static int publicStaticGrandchildProperty;
        protected static int protectedStaticGrandchildProperty;
        static int packageStaticGrandchildProperty;
        private static int privateStaticGrandchildProperty;
        
        public int publicGrandchildProperty;
        protected int protectedGrandchildProperty;
        int packageGrandchildProperty;
        private int privateGrandchildProperty;        
    }
    
    /**
     * Tests whether the given superclass limit is observed.
     */
    @Test
    public void getAllDeclaredFields_givenSuperclass() {
        List<Field> expectedFields = new ArrayList<Field>();
        expectedFields.addAll(Arrays.asList(Child.class.getDeclaredFields()));
        expectedFields.addAll(Arrays.asList(Grandchild.class.getDeclaredFields()));
        
        assertTrue(CollectionUtils.isEqualCollection(
                expectedFields, 
                ClassUtils.getAllDeclaredFields(Grandchild.class, Child.class)));
    }

    
    @Test(expected = IllegalArgumentException.class)
    public void isAnyAssignableFrom_nullSuperclasses() {
        ClassUtils.isAnyAssignableFrom(null, Object.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isAnyAssignableFrom_nullClass() {
        ClassUtils.isAnyAssignableFrom(new ArrayList<Class<?>>(), null);
    }
    
    @Test
    public void isAnyAssignableFrom_unassignable() {
        assertFalse(ClassUtils.isAnyAssignableFrom(Arrays.<Class<?>>asList(String.class, List.class), 
                                                   Long.class));
    }
    
    @Test
    public void isAnyAssignableFrom() {
        assertTrue(ClassUtils.isAnyAssignableFrom(Arrays.<Class<?>>asList(String.class, Number.class), 
                                                  Long.class));        
    }    
    
    @Test(expected = IllegalArgumentException.class)
    public void isAnyInstance_nullSuperclasses() {
        ClassUtils.isInstance(null, new Object());
    }

    @Test
    public void isAnyInstance_nullInstance() {
        assertFalse("Expected null not to be an instance of String or List",
                    ClassUtils.isInstance(Arrays.<Class<?>>asList(String.class, List.class), 
                    null));
    }
    
    @Test
    public void isAnyInstance_unassignable() {
        assertFalse("Expected 7L not to be an instance of String or List",
                ClassUtils.isInstance(Arrays.<Class<?>>asList(String.class, List.class), 
                Long.valueOf(7)));
    }
    
    @Test
    public void isAnyInstance() {
        assertTrue("Expected 7L to be an instance of String or Number",
                ClassUtils.isInstance(Arrays.<Class<?>>asList(String.class, Number.class), 
                Long.valueOf(7)));
    }
    
}
