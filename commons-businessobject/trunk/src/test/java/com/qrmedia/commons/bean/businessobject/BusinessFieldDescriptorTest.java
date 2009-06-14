/*
 * @(#)BusinessFieldDescriptorTest.java     8 Jul 2008
 */
package com.qrmedia.commons.bean.businessobject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for the {@link BusinessFieldDescriptor}.
 * 
 * @author anph
 * @since 8 Jul 2008
 *
 */
@RunWith(value = Parameterized.class)
public class BusinessFieldDescriptorTest {
    private Field businessField;
    private boolean expectedRelevantForEquals;
    private boolean expectedRelevantForHashCode;
    private boolean expectedRelevantForToString;
    
    @Parameters
    public static Collection<Object[]> data() throws Exception {
        List<Object[]> data = new ArrayList<Object[]>();

        data.add(new Object[] { getField("property1"), true, true, true });
        data.add(new Object[] { getField("property2"), false, false, true });
        data.add(new Object[] { getField("property3"), true, true, false });
        data.add(new Object[] { getField("property4"), false, false, false });
        return data;
    }
    
    private static Field getField(String fieldName) throws Exception {
        return StubBusinessObject.class.getDeclaredField(fieldName);
    }
    
    // called for each parameter set in the test data
    public BusinessFieldDescriptorTest(Field businessField, boolean expectedRelevantForEquals,
            boolean expectedRelevantForHashCode, boolean expectedRelevantForToString) {
        this.businessField = businessField;
        this.expectedRelevantForEquals = expectedRelevantForEquals;
        this.expectedRelevantForHashCode = expectedRelevantForHashCode; 
        this.expectedRelevantForToString = expectedRelevantForToString;
    }
    
    @Test
    public void forBusinessField() {
        BusinessFieldDescriptor descriptor = 
            BusinessFieldDescriptor.forBusinessField(businessField);
        
        assertSame(businessField, descriptor.getBusinessField());
        assertEquals(expectedRelevantForEquals, descriptor.isRelevantForEquals());
        assertEquals(expectedRelevantForHashCode, descriptor.isRelevantForHashCode());        
        assertEquals(expectedRelevantForToString, descriptor.isRelevantForToString());        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getValue_invalidClass() {
        BusinessFieldDescriptor.forBusinessField(businessField).getValue(new Object());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getValue_nonAccessibleField() throws Exception {
        BusinessFieldDescriptor.forBusinessField(getField("nonPublicProperty"))
        .getValue(new StubBusinessObject());
    }
    
    @Test
    public void getValue() throws Exception {
        StubBusinessObject businessObject = new StubBusinessObject();
        String value = "James Bond";
        businessObject.setProperty1(value);
        assertEquals(value, BusinessFieldDescriptor.forBusinessField(getField("property1"))
                            .getValue(businessObject));
    }
    
}
