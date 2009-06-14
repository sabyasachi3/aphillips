/*
 * @(#)BusinessObjectContextTest.java     9 Jul 2008
 */
package com.qrmedia.commons.bean.businessobject;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.qrmedia.commons.reflect.ReflectionUtils;

/**
 * Unit tests for the {@link BusinessObjectContext}.
 * 
 * @author anph
 * @since 9 Jul 2008
 *
 */
public class BusinessObjectContextTest {
    private static Map<Class<?>, BusinessObjectDescriptor> contextBusinessObjectDescriptors;
    
    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void prepareFixture() throws Exception {
        contextBusinessObjectDescriptors = 
            (Map<Class<?>, BusinessObjectDescriptor>) ReflectionUtils.getValue(
                    BusinessObjectContext.class, "BUSINESS_OBJECT_DESCRIPTORS");
    }
    
    @Test
    public void getDescriptor_notPresent() {
        Class<StubBusinessObject> stubBusinessObjectClass = StubBusinessObject.class;
        BusinessObjectDescriptor descriptor = 
            BusinessObjectContext.getDescriptor(stubBusinessObjectClass);
        
        assertNotNull(descriptor);
        assertSame(descriptor, contextBusinessObjectDescriptors.get(stubBusinessObjectClass));
    }
    
    @Test
    public void getDescriptor_present() {
        Class<StubBusinessObject> stubBusinessObjectClass = StubBusinessObject.class;
        BusinessObjectDescriptor descriptor = 
            BusinessObjectDescriptor.forBusinessObjectClass(stubBusinessObjectClass);
        contextBusinessObjectDescriptors.put(stubBusinessObjectClass, descriptor);
        
        assertSame(descriptor, BusinessObjectContext.getDescriptor(stubBusinessObjectClass));
    }
}
