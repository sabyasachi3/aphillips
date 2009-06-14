/*
 * @(#)ClassUtilsAllAnnotatedDeclaredFieldsTest.java     11 Jun 2009
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

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;


/**
 * Unit tests for the {@link ClassUtils#getAllAnnotatedDeclaredFields(Class, Class)} method.
 * 
 * @author aphillips
 * @since 11 Jun 2009
 * @see ClassUtilsTest
 *
 */
public class ClassUtilsAllAnnotatedDeclaredFieldsTest {

    @Test(expected = IllegalArgumentException.class)
    public void getAllAnnotatedDeclaredFields_nullClass() {
        ClassUtils.getAllAnnotatedDeclaredFields(null, PostConstruct.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getAllAnnotatedDeclaredFields_nullAnnotationType() {
        ClassUtils.getAllAnnotatedDeclaredFields(Parent.class, null);
    }    
    
    @SuppressWarnings("unused")
    private static class Parent {
        protected static int nonAnnotatedProtectedStaticField;
        @Resource
        protected static int annotatedProtectedStaticField;
        private static int nonAnnotatedPrivateStaticField;
        @Resource
        private static int annotatedPrivateStaticField;
        
        protected int nonAnnotatedProtectedField;
        @Resource
        protected int annotatedProtectedField;
        private int nonAnnotatedPrivateField;
        @Resource
        private int annotatedPrivateField;
    }
    
    @SuppressWarnings("unused")
    private static class Child extends Parent {
        protected static int nonAnnotatedProtectedStaticField;
        @Resource
        protected static int annotatedProtectedStaticField;
        private static int nonAnnotatedPrivateStaticField;
        @Resource
        private static int annotatedPrivateStaticField;
        
        protected int nonAnnotatedProtectedField;
        @Resource
        protected int annotatedProtectedField;
        private int nonAnnotatedPrivateField;
        @Resource
        private int annotatedPrivateField;
    }
    
    @Test
    public void getAllAnnotatedDeclaredFields_absentAnnotationType() {
        assertTrue("Expected empty result", 
                   ClassUtils.getAllAnnotatedDeclaredFields(Child.class, PostConstruct.class).isEmpty());
    }

    @Test
    public void getAllAnnotatedDeclaredFields() throws Exception {
        Collection<Field> expectedFields = Arrays.asList(
                Parent.class.getDeclaredField("annotatedProtectedStaticField"),
                Parent.class.getDeclaredField("annotatedPrivateStaticField"),
                Parent.class.getDeclaredField("annotatedProtectedField"),
                Parent.class.getDeclaredField("annotatedPrivateField"),
                Child.class.getDeclaredField("annotatedProtectedStaticField"),
                Child.class.getDeclaredField("annotatedPrivateStaticField"),
                Child.class.getDeclaredField("annotatedProtectedField"),
                Child.class.getDeclaredField("annotatedPrivateField"));
        assertTrue("Expected " + expectedFields, CollectionUtils.isEqualCollection(expectedFields, 
                ClassUtils.getAllAnnotatedDeclaredFields(Child.class, Resource.class)));
    }
    
}
