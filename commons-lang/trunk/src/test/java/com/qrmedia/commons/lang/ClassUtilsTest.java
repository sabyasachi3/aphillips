/*
 * @(#)ClassUtilsTest.java     9 Feb 2009
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
    
}
