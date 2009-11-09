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

import java.rmi.AccessException;

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

    @Test(expected = AccessException.class)
    public void getValue_nonexistentStaticProperty() throws AccessException {
        ReflectionUtils.getValue(StubObject.class, "nonexistent");
    }
    
    @Test
    public void getValue_accessibleStaticProperty() throws AccessException {
        int value = 7;
        StubObject.accessibleStaticProperty = value;
        
        assertEquals(value, ReflectionUtils.getValue(StubObject.class, "accessibleStaticProperty"));
    }
    
    @Test
    public void getValue_privateStaticProperty() throws AccessException {
        int value = 7;
        StubObject.privateStaticProperty = value;
        
        assertEquals(value, ReflectionUtils.getValue(StubObject.class, "privateStaticProperty"));
    }
    
    @Test
    public void getValue_immutableStaticProperty() throws AccessException {
        assertEquals(7,  ReflectionUtils.getValue(StubObject.class, "immutableStaticProperty"));        
    }

    @Test
    public void getValue_staticChildProperty() throws AccessException {
        String value = "007";
        StubObject.staticChildStubObject.property = value;
        
        assertEquals(value, ReflectionUtils.getValue(StubObject.class, 
                                                     "staticChildStubObject.property"));
    }
    
    @Test(expected = AccessException.class)
    public void getValue_nonexistentProperty() throws AccessException {
        ReflectionUtils.getValue(new StubObject(), "nonexistent");
    }
    
    @Test
    public void getValue_accessibleProperty() throws AccessException {
        StubObject stubObject = new StubObject();
        String value = "007";
        stubObject.accessibleProperty = value;
        
        assertEquals(value, ReflectionUtils.getValue(stubObject, "accessibleProperty"));
    }
    
    @Test
    public void getValue_privateProperty() throws AccessException {
        StubObject stubObject = new StubObject();
        String value = "007";
        stubObject.privateProperty = value;
        
        assertEquals(value, ReflectionUtils.getValue(stubObject, "privateProperty"));
    }
    
    @Test
    public void getValue_immutableProperty() throws AccessException {
        assertEquals("007",  ReflectionUtils.getValue(new StubObject(), "immutableProperty"));        
    }

    @Test
    public void getValue_childProperty() throws AccessException {
        StubObject stubObject = new StubObject();
        String value = "007";
        stubObject.childStubObject.property = value;
        
        assertEquals(value, ReflectionUtils.getValue(stubObject, "childStubObject.property"));
    }

    @Test(expected = AccessException.class)
    public void setValue_nonexistentStaticProperty() throws AccessException {
        ReflectionUtils.setValue(StubObject.class, "nonexistent", null);
    }
    
    @Test(expected = AccessException.class)
    public void setValue_invalidStaticPropertyValue() throws AccessException  {
        ReflectionUtils.setValue(StubObject.class, "accessibleStaticProperty", "007");
    }    
    
    @Test
    public void setValue_accessibleStaticProperty() throws AccessException {
        int value = 8;
        
        ReflectionUtils.setValue(StubObject.class, "accessibleStaticProperty", value);
        assertEquals(value, StubObject.accessibleStaticProperty);
    }
    
    @Test
    public void setValue_privateStaticProperty() throws AccessException {
        int value = 8;
        
        ReflectionUtils.setValue(StubObject.class, "privateStaticProperty", value);
        assertEquals(value, StubObject.privateStaticProperty);
    }
    
    @Test
    public void setValue_immutableStaticProperty()  {
        try {
            ReflectionUtils.setValue(StubObject.class, "immutableStaticProperty", null);
            fail("Expected an AccessException to be thrown");
        } catch (AccessException exception) { 
            // expected
        }
        
        // verify that nothing happened
        assertEquals(7, StubObject.immutableStaticProperty);
    }
    
    @Test
    public void setValue_staticChildProperty() throws AccessException {
        String value = "008";
        
        ReflectionUtils.setValue(StubObject.class, "staticChildStubObject.property", value);
        assertEquals(value, StubObject.staticChildStubObject.property);
    } 
    
    @Test(expected = AccessException.class)
    public void setValue_nonexistentProperty() throws AccessException {
        ReflectionUtils.setValue(new StubObject(), "nonexistent", null);
    }

    @Test(expected = AccessException.class)
    public void setValue_invalidPropertyValue() throws AccessException {
        ReflectionUtils.setValue(new StubObject(), "accessibleProperty", 7);
    }    

    @Test
    public void setValue_accessibleProperty() throws AccessException {
        StubObject stubObject = new StubObject();
        String value = "008";
        
        ReflectionUtils.setValue(stubObject, "accessibleProperty", value);
        assertEquals(value, stubObject.accessibleProperty);
    }
    
    @Test
    public void setValue_privateProperty() throws AccessException {
        StubObject stubObject = new StubObject();
        String value = "008";
        
        ReflectionUtils.setValue(stubObject, "privateProperty", value);
        assertEquals(value, stubObject.privateProperty);
    }
    
    @Test
    public void setValue_immutableProperty() throws AccessException {
        StubObject stubObject = new StubObject();
        
        // doesn't throw an exception internally, hence returns true...but has no effect
        ReflectionUtils.setValue(stubObject, "immutableProperty", null);
        
        // verify that nothing happened
        assertEquals("007", stubObject.immutableProperty);
    }
    
    @Test
    public void setValue_childProperty() throws AccessException {
        StubObject stubObject = new StubObject();
        String value = "008";
        
        ReflectionUtils.setValue(stubObject, "childStubObject.property", value);
        assertEquals(value, stubObject.childStubObject.property);
    } 
    
    @Test
    public void setValueQuietly_nonexistentStaticProperty() {
        assertFalse(ReflectionUtils.setValueQuietly(StubObject.class, "nonexistent", null));
    }
    
    @Test
    public void setValueQuietly_invalidStaticPropertyValue() {
        assertFalse(ReflectionUtils.setValueQuietly(StubObject.class, "accessibleStaticProperty", "007"));
    }    
    
    @Test
    public void setValueQuietly_accessibleStaticProperty() {
        int value = 8;
        
        assertTrue(ReflectionUtils.setValueQuietly(StubObject.class, "accessibleStaticProperty", value));
        assertEquals(value, StubObject.accessibleStaticProperty);
    }
    
    @Test
    public void setValueQuietly_privateStaticProperty() {
        int value = 8;
        
        assertTrue(ReflectionUtils.setValueQuietly(StubObject.class, "privateStaticProperty", value));
        assertEquals(value, StubObject.privateStaticProperty);
    }
    
    @Test
    public void setValueQuietly_immutableStaticProperty() {
        assertFalse(ReflectionUtils.setValueQuietly(StubObject.class, "immutableStaticProperty", null));
        
        // verify that nothing happened
        assertEquals(7, StubObject.immutableStaticProperty);
    }
    
    @Test
    public void setValueQuietly_staticChildProperty() {
        String value = "008";
        
        assertTrue(ReflectionUtils.setValueQuietly(
                StubObject.class, "staticChildStubObject.property", value));
        assertEquals(value, StubObject.staticChildStubObject.property);
    } 
    
    @Test
    public void setValueQuietly_nonexistentProperty() {
        assertFalse(ReflectionUtils.setValueQuietly(new StubObject(), "nonexistent", null));
    }

    @Test
    public void setValueQuietly_invalidPropertyValue() {
        assertFalse(ReflectionUtils.setValueQuietly(new StubObject(), "accessibleProperty", 7));
    }    

    @Test
    public void setValueQuietly_accessibleProperty() {
        StubObject stubObject = new StubObject();
        String value = "008";
        
        assertTrue(ReflectionUtils.setValueQuietly(stubObject, "accessibleProperty", value));
        assertEquals(value, stubObject.accessibleProperty);
    }
    
    @Test
    public void setValueQuietly_privateProperty() {
        StubObject stubObject = new StubObject();
        String value = "008";
        
        assertTrue(ReflectionUtils.setValueQuietly(stubObject, "privateProperty", value));
        assertEquals(value, stubObject.privateProperty);
    }
    
    @Test
    public void setValueQuietly_immutableProperty() {
        StubObject stubObject = new StubObject();
        
        // doesn't throw an exception internally, hence returns true...but has no effect
        assertTrue(ReflectionUtils.setValueQuietly(stubObject, "immutableProperty", null));
        
        // verify that nothing happened
        assertEquals("007", stubObject.immutableProperty);
    }
    
    @Test
    public void setValueQuietly_childProperty() {
        StubObject stubObject = new StubObject();
        String value = "008";
        
        assertTrue(ReflectionUtils.setValueQuietly(stubObject, "childStubObject.property", value));
        assertEquals(value, stubObject.childStubObject.property);
    }
    
}