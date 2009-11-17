/*
 * @(#)ReflectionUtilsTest.java     6 May 2009
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
package com.qrmedia.commons.reflect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Unit tests for the {@link ReflectionUtils}.
 * 
 * @author aphillips
 * @since 6 May 2009
 *
 */
public class ReflectionUtilsTest {

    private static class StubObject {
        public static int accessibleStaticProperty;
        private static int privateStaticProperty;
        private static final int immutableStaticProperty = 7;
        private static ChildStubObject staticChildStubObject = new ChildStubObject();
        
        public String accessibleProperty;
        private String privateProperty;
        private final String immutableProperty = "007";
        private ChildStubObject childStubObject = new ChildStubObject();
    }
    
    private static class ChildStubObject {
        private String property;
    }

    @Test(expected = IllegalAccessException.class)
    public void getValue_nonexistentStaticProperty() throws IllegalAccessException {
        ReflectionUtils.getValue(StubObject.class, "nonexistent");
    }
    
    @Test
    public void getValue_accessibleStaticProperty() throws IllegalAccessException {
        int value = 7;
        StubObject.accessibleStaticProperty = value;
        
        assertEquals(value, ReflectionUtils.getValue(StubObject.class, "accessibleStaticProperty"));
    }
    
    @Test
    public void getValue_privateStaticProperty() throws IllegalAccessException {
        int value = 7;
        StubObject.privateStaticProperty = value;
        
        assertEquals(value, ReflectionUtils.getValue(StubObject.class, "privateStaticProperty"));
    }
    
    @Test
    public void getValue_immutableStaticProperty() throws IllegalAccessException {
        assertEquals(7,  ReflectionUtils.getValue(StubObject.class, "immutableStaticProperty"));        
    }

    @Test
    public void getValue_staticChildProperty() throws IllegalAccessException {
        String value = "007";
        StubObject.staticChildStubObject.property = value;
        
        assertEquals(value, ReflectionUtils.getValue(StubObject.class, 
                                                     "staticChildStubObject.property"));
    }
    
    @Test(expected = IllegalAccessException.class)
    public void getValue_nonexistentProperty() throws IllegalAccessException {
        ReflectionUtils.getValue(new StubObject(), "nonexistent");
    }
    
    @Test
    public void getValue_accessibleProperty() throws IllegalAccessException {
        StubObject stubObject = new StubObject();
        String value = "007";
        stubObject.accessibleProperty = value;
        
        assertEquals(value, ReflectionUtils.getValue(stubObject, "accessibleProperty"));
    }
    
    @Test
    public void getValue_privateProperty() throws IllegalAccessException {
        StubObject stubObject = new StubObject();
        String value = "007";
        stubObject.privateProperty = value;
        
        assertEquals(value, ReflectionUtils.getValue(stubObject, "privateProperty"));
    }
    
    @Test
    public void getValue_immutableProperty() throws IllegalAccessException {
        assertEquals("007",  ReflectionUtils.getValue(new StubObject(), "immutableProperty"));        
    }

    @Test
    public void getValue_childProperty() throws IllegalAccessException {
        StubObject stubObject = new StubObject();
        String value = "007";
        stubObject.childStubObject.property = value;
        
        assertEquals(value, ReflectionUtils.getValue(stubObject, "childStubObject.property"));
    }

    @Test(expected = IllegalAccessException.class)
    public void setValue_nonexistentStaticProperty() throws IllegalAccessException {
        ReflectionUtils.setValue(StubObject.class, "nonexistent", null);
    }
    
    @Test(expected = IllegalAccessException.class)
    public void setValue_invalidStaticPropertyValue() throws IllegalAccessException  {
        ReflectionUtils.setValue(StubObject.class, "accessibleStaticProperty", "007");
    }    
    
    @Test
    public void setValue_accessibleStaticProperty() throws IllegalAccessException {
        int value = 8;
        
        ReflectionUtils.setValue(StubObject.class, "accessibleStaticProperty", value);
        assertEquals(value, StubObject.accessibleStaticProperty);
    }
    
    @Test
    public void setValue_privateStaticProperty() throws IllegalAccessException {
        int value = 8;
        
        ReflectionUtils.setValue(StubObject.class, "privateStaticProperty", value);
        assertEquals(value, StubObject.privateStaticProperty);
    }
    
    @Test
    public void setValue_immutableStaticProperty()  {
        try {
            ReflectionUtils.setValue(StubObject.class, "immutableStaticProperty", null);
            fail("Expected an IllegalAccessException to be thrown");
        } catch (IllegalAccessException exception) { 
            // expected
        }
        
        // verify that nothing happened
        assertEquals(7, StubObject.immutableStaticProperty);
    }
    
    @Test
    public void setValue_staticChildProperty() throws IllegalAccessException {
        String value = "008";
        
        ReflectionUtils.setValue(StubObject.class, "staticChildStubObject.property", value);
        assertEquals(value, StubObject.staticChildStubObject.property);
    } 
    
    @Test(expected = IllegalAccessException.class)
    public void setValue_nonexistentProperty() throws IllegalAccessException {
        ReflectionUtils.setValue(new StubObject(), "nonexistent", null);
    }

    @Test(expected = IllegalAccessException.class)
    public void setValue_invalidPropertyValue() throws IllegalAccessException {
        ReflectionUtils.setValue(new StubObject(), "accessibleProperty", 7);
    }    

    @Test
    public void setValue_accessibleProperty() throws IllegalAccessException {
        StubObject stubObject = new StubObject();
        String value = "008";
        
        ReflectionUtils.setValue(stubObject, "accessibleProperty", value);
        assertEquals(value, stubObject.accessibleProperty);
    }
    
    @Test
    public void setValue_privateProperty() throws IllegalAccessException {
        StubObject stubObject = new StubObject();
        String value = "008";
        
        ReflectionUtils.setValue(stubObject, "privateProperty", value);
        assertEquals(value, stubObject.privateProperty);
    }
    
    @Test
    public void setValue_immutableProperty() throws IllegalAccessException {
        StubObject stubObject = new StubObject();
        
        // doesn't throw an exception internally, hence returns true...but has no effect
        ReflectionUtils.setValue(stubObject, "immutableProperty", null);
        
        // verify that nothing happened
        assertEquals("007", stubObject.immutableProperty);
    }
    
    @Test
    public void setValue_childProperty() throws IllegalAccessException {
        StubObject stubObject = new StubObject();
        String value = "008";
        
        ReflectionUtils.setValue(stubObject, "childStubObject.property", value);
        assertEquals(value, stubObject.childStubObject.property);
    } 
    
    @Test
    public void trySetValue_nonexistentStaticProperty() {
        assertFalse(ReflectionUtils.trySetValue(StubObject.class, "nonexistent", null));
    }
    
    @Test
    public void trySetValue_invalidStaticPropertyValue() {
        assertFalse(ReflectionUtils.trySetValue(StubObject.class, "accessibleStaticProperty", "007"));
    }    
    
    @Test
    public void trySetValue_accessibleStaticProperty() {
        int value = 8;
        
        assertTrue(ReflectionUtils.trySetValue(StubObject.class, "accessibleStaticProperty", value));
        assertEquals(value, StubObject.accessibleStaticProperty);
    }
    
    @Test
    public void trySetValue_privateStaticProperty() {
        int value = 8;
        
        assertTrue(ReflectionUtils.trySetValue(StubObject.class, "privateStaticProperty", value));
        assertEquals(value, StubObject.privateStaticProperty);
    }
    
    @Test
    public void trySetValue_immutableStaticProperty() {
        assertFalse(ReflectionUtils.trySetValue(StubObject.class, "immutableStaticProperty", null));
        
        // verify that nothing happened
        assertEquals(7, StubObject.immutableStaticProperty);
    }
    
    @Test
    public void trySetValue_staticChildProperty() {
        String value = "008";
        
        assertTrue(ReflectionUtils.trySetValue(
                StubObject.class, "staticChildStubObject.property", value));
        assertEquals(value, StubObject.staticChildStubObject.property);
    } 
    
    @Test
    public void trySetValue_nonexistentProperty() {
        assertFalse(ReflectionUtils.trySetValue(new StubObject(), "nonexistent", null));
    }

    @Test
    public void trySetValue_invalidPropertyValue() {
        assertFalse(ReflectionUtils.trySetValue(new StubObject(), "accessibleProperty", 7));
    }    

    @Test
    public void trySetValue_accessibleProperty() {
        StubObject stubObject = new StubObject();
        String value = "008";
        
        assertTrue(ReflectionUtils.trySetValue(stubObject, "accessibleProperty", value));
        assertEquals(value, stubObject.accessibleProperty);
    }
    
    @Test
    public void trySetValue_privateProperty() {
        StubObject stubObject = new StubObject();
        String value = "008";
        
        assertTrue(ReflectionUtils.trySetValue(stubObject, "privateProperty", value));
        assertEquals(value, stubObject.privateProperty);
    }
    
    @Test
    public void trySetValue_immutableProperty() {
        StubObject stubObject = new StubObject();
        
        // doesn't throw an exception internally, hence returns true...but has no effect
        assertTrue(ReflectionUtils.trySetValue(stubObject, "immutableProperty", null));
        
        // verify that nothing happened
        assertEquals("007", stubObject.immutableProperty);
    }
    
    @Test
    public void trySetValue_childProperty() {
        StubObject stubObject = new StubObject();
        String value = "008";
        
        assertTrue(ReflectionUtils.trySetValue(stubObject, "childStubObject.property", value));
        assertEquals(value, stubObject.childStubObject.property);
    }
    
}