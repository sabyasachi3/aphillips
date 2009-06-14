/*
 * @(#)BusinessObjectUtilsTest.java     8 Jul 2008
 */
package com.qrmedia.commons.bean.businessobject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.qrmedia.commons.bean.businessobject.annotation.BusinessField;
import com.qrmedia.commons.bean.businessobject.annotation.BusinessObject;

/**
 * Unit tests for the {@link BusinessObjectUtils}.
 * 
 * @author anph
 * @since 8 Jul 2008
 *
 */
@RunWith(value = Parameterized.class)
public class BusinessObjectUtilsTest {
    private Object object1;
    private Object object2;
    
    private boolean equalsExpected;
    private Collection<String> expectedToStringSections;
    private Collection<String> forbiddenToStringSections;

    @BusinessObject
    public static class StubBusinessObject1 { 
        
        @BusinessField
        private int property;

        public int getProperty() {
            return property;
        }
        
    }
    
    @BusinessObject
    public static class StubBusinessObject2 { 
        
        @BusinessField
        private int property;
        
        public int getProperty() {
            return property;
        }
        
    }
    
    @BusinessObject(equivalentClasses = { StubBusinessObject2.class })
    public static class StubBusinessObject3 { 
        
        @BusinessField
        private int property;
        
        public int getProperty() {
            return property;
        }
        
    }    
    
    public static class StubBusinessObject4 extends StubBusinessObject3 {}    
    
    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> data = new ArrayList<Object[]>();
        
        // some unequal objects
        data.add(new Object[] { new StubBusinessObject(), null, false, 
                                Arrays.asList("property1", "property2"),
                                Arrays.asList("property3", "property4") });
        data.add(new Object[] { new StubBusinessObject1(), new StubBusinessObject(), false, 
                                Arrays.asList("property"), null });
        
        // "property3" is relevant for equals
        StubBusinessObject stubBusinessObject1 = new StubBusinessObject();
        stubBusinessObject1.setProperty3(7);
        StubBusinessObject stubBusinessObject2 = new StubBusinessObject();
        stubBusinessObject2.setProperty3(8);
        data.add(new Object[] { stubBusinessObject1, stubBusinessObject2, false, null, null });

        // StubBusinessObject3 can only be compared to StubBusinessObject2 and 3 (sub)classes
        data.add(new Object[] { new StubBusinessObject3(), new StubBusinessObject1(), false, 
                                Arrays.asList("property"), null });   
        
        // some equal objects
        data.add(new Object[] { null, null, true, null, null});
        data.add(new Object[] { new StubBusinessObject(), new StubBusinessObject(), true, null, 
                                null });
        
        StubBusinessObject1 stubBusinessObject3 = new StubBusinessObject1();
        int value = 7;
        stubBusinessObject3.property = value;
        StubBusinessObject1 stubBusinessObject4 = new StubBusinessObject1();
        stubBusinessObject4.property = value;        
        data.add(new Object[] { stubBusinessObject3, stubBusinessObject4, true, 
                                null, null });
        
        // different classes, but should still be equal
        StubBusinessObject1 stubBusinessObject5 = new StubBusinessObject1();
        stubBusinessObject5.property = value;
        StubBusinessObject2 stubBusinessObject6 = new StubBusinessObject2();
        stubBusinessObject6.property = value;        
        data.add(new Object[] { stubBusinessObject5, stubBusinessObject6, true, 
                                null, null });       
        
        // "property4" is not relevant for equals or hashCode
        StubBusinessObject stubBusinessObject7 = new StubBusinessObject();
        stubBusinessObject7.setProperty4(7);
        StubBusinessObject stubBusinessObject8 = new StubBusinessObject();
        stubBusinessObject8.setProperty4(8);
        data.add(new Object[] { stubBusinessObject7, stubBusinessObject8, true, null, null });

        // StubBusinessObject3 can be equal to StubBusinessObject2 and 3 (sub)classes
        data.add(new Object[] { new StubBusinessObject3(), new StubBusinessObject2(), true, 
                                Arrays.asList("property"), null });           
        data.add(new Object[] { new StubBusinessObject3(), new StubBusinessObject3(), true, 
                                Arrays.asList("property"), null });   
        data.add(new Object[] { new StubBusinessObject3(), new StubBusinessObject4(), true, 
                                Arrays.asList("property"), null });
        
        return data;
    }
    
    // called for each parameter set in the test data
    public BusinessObjectUtilsTest(Object object1, Object object2, boolean equalsExpected,
            Collection<String> expectedToStringSections, 
            Collection<String> forbiddenToStringSections) {
        this.object1 = object1;
        this.object2 = object2;
        this.equalsExpected = equalsExpected;
        this.expectedToStringSections = expectedToStringSections;
        this.forbiddenToStringSections = forbiddenToStringSections;
    }

    @Test
    public void equals() {
        assertEquals(equalsExpected, BusinessObjectUtils.equals(object1, object2));
        assertEquals(equalsExpected, BusinessObjectUtils.equals(object2, object1));
    }
    
    @Test
    public void hashCodeTest() {
        
        // hash codes cannot be calculated for null objects
        if ((object1 != null) && (object2 != null)) {
            /*
             * The hashCode contract only states that two equal objects must have the same hash code.
             * In general, the BusinessObject implementation also ensures that non-equal objects have
             * different hash codes, but instances that are field-equals but do not conform to the
             * equivalentClass requirement will have the same hash code. 
             */
            if (equalsExpected) {
                assertEquals(BusinessObjectUtils.hashCode(object1), 
                             BusinessObjectUtils.hashCode(object2));
            } 
            
        }
        
    }
    
    @Test
    public void toStringTest() {
        
        // toString cannot be calculated for null objects
        if (object1 != null) {
            String stringRepresentation = BusinessObjectUtils.toString(object1);

            if (expectedToStringSections != null) {
                
                for (String expectedToStringSection : expectedToStringSections) {
                    assertTrue(stringRepresentation.contains(expectedToStringSection));
                }
                
            }
            
            if (forbiddenToStringSections != null) {
                
                for (String forbiddenToStringSection : forbiddenToStringSections) {
                    assertFalse(stringRepresentation.contains(forbiddenToStringSection));
                }
                
            }
            
        }        
        
    }
    
}
