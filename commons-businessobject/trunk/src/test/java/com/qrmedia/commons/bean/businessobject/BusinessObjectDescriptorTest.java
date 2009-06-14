/*
 * @(#)BusinessObjectDescriptorTest.java     8 Jul 2008
 */
package com.qrmedia.commons.bean.businessobject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import com.qrmedia.commons.bean.businessobject.annotation.BusinessField;
import com.qrmedia.commons.bean.businessobject.annotation.BusinessObject;

/**
 * Unit tests for the {@link BusinessObjectDescriptor}.
 * 
 * @author anph
 * @since 8 Jul 2008
 *
 */
public class BusinessObjectDescriptorTest {
    
    @Test(expected = IllegalArgumentException.class)
    public void forBusinessObject_nonBusinessObjectClass() {
        BusinessObjectDescriptor.forBusinessObjectClass(Object.class);
    }
    
    @BusinessObject(equivalentClasses = { Object.class })
    private static class StubInvalidEquivalentClassBusinessObject {}
    
    @Test(expected = IllegalArgumentException.class)
    public void forBusinessObject_nonBusinessObjectEquivalentClass() {
        BusinessObjectDescriptor.forBusinessObjectClass(StubInvalidEquivalentClassBusinessObject.class);
    }
    
    @Test
    public void forBusinessObject() {
        BusinessObjectDescriptor descriptor = 
            BusinessObjectDescriptor.forBusinessObjectClass(StubBusinessObject.class);
        
        assertEquals(StubBusinessObject.class, descriptor.getBusinessObjectClass());
        assertFalse(descriptor.ignoreClass());
        assertTrue(CollectionUtils.isEqualCollection(
                Arrays.<Class<?>>asList(StubBusinessObject.class), descriptor.getEquivalentClasses()));
        assertTrue(CollectionUtils.isEqualCollection(
                Arrays.asList("property1", "property2", "property3", "property4"), 
                descriptor.getBusinessFields().keySet()));
        assertTrue(CollectionUtils.isEqualCollection(
                Arrays.asList("property1", "property3"), 
                descriptor.getBusinessFieldsRelevantForEquals().keySet()));
        assertTrue(CollectionUtils.isEqualCollection(
                Arrays.asList("property1", "property3"), 
                descriptor.getBusinessFieldsRelevantForHashCode().keySet()));
        assertTrue(CollectionUtils.isEqualCollection(
                Arrays.asList("property1", "property2"), 
                descriptor.getBusinessFieldsRelevantForToString().keySet()));        
    }

    @BusinessObject
    private static class StubParentBusinessObject {
        @BusinessField
        private int property1;

        public int getProperty1() {
            return property1;
        }

        public void setProperty1(int property1) {
            this.property1 = property1;
        }
        
    }
    
    @BusinessObject
    private static class StubChildBusinessObject extends StubParentBusinessObject {
        @BusinessField
        private int property2;

        public int getProperty2() {
            return property2;
        }

        public void setProperty2(int property2) {
            this.property2 = property2;
        }
        
    }
    
    private static class StubGrandchildBusinessObject extends StubChildBusinessObject {
        @BusinessField
        private int property3;

        public int getProperty3() {
            return property3;
        }

        public void setProperty3(int property3) {
            this.property3 = property3;
        }
        
    }    
    
    /**
     * Tests whether only fields <em>up to and including the first {@link BusinessObject}
     * superclass</em> are included. 
     */
    @Test
    public void forBusinessObject_multipleBusinessObjectSuperclasses() {
        BusinessObjectDescriptor descriptor = 
            BusinessObjectDescriptor.forBusinessObjectClass(StubGrandchildBusinessObject.class);
        
        assertEquals(StubGrandchildBusinessObject.class, descriptor.getBusinessObjectClass());
        assertTrue(CollectionUtils.isEqualCollection(
                Arrays.asList("property2", "property3"), descriptor.getBusinessFields().keySet()));
    }
    
    @BusinessObject
    private static class StubClassIgnoringBusinessObject {}
    
    @Test
    public void ignoreClass_ignore() {
        assertTrue(BusinessObjectDescriptor.forBusinessObjectClass(StubClassIgnoringBusinessObject.class)
                   .ignoreClass());
    }
    
    @Test
    public void ignoreClass() {
        assertFalse(BusinessObjectDescriptor.forBusinessObjectClass(StubBusinessObject.class)
                    .ignoreClass());        
    }
    
    @BusinessObject(equivalentClasses = { StubBusinessObject.class })
    private static class StubClassRestrictingBusinessObject {}
    
    /**
     * Checks that the class containing the {@link BusinessObject @BusinessObject} annotation
     * is automatically included the collection of equivalent classes, provided these are specified.
     */
    @Test
    public void getEquivalentClasses_declaringClass() {
        assertTrue(CollectionUtils.isEqualCollection(
                Arrays.<Class<?>>asList(StubClassRestrictingBusinessObject.class, StubBusinessObject.class), 
                BusinessObjectDescriptor.forBusinessObjectClass(StubClassRestrictingBusinessObject.class)
                .getEquivalentClasses()));
    }
    
}
