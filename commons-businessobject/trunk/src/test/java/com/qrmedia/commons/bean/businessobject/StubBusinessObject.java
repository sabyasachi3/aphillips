/*
 * @(#)DummyBusinessObject.java     8 Jul 2008
 */
package com.qrmedia.commons.bean.businessobject;

import com.qrmedia.commons.bean.businessobject.annotation.BusinessField;
import com.qrmedia.commons.bean.businessobject.annotation.BusinessObject;

/**
 * A dummy business object for testing.
 * 
 * @author anph
 * @since 8 Jul 2008
 *
 */
@BusinessObject(equivalentClasses = { StubBusinessObject.class })
public class StubBusinessObject {
    
    @BusinessField
    private String property1;
    
    @BusinessField(includeInEquals = false)
    private int property2;
    
    @BusinessField(includeInToString = false)
    private int property3;
    
    @BusinessField(includeInEquals = false, includeInToString = false)
    private int property4;
    
    int nonPublicProperty;

    /**
     * Getter for property1.
     *
     * @return the property1.
     */
    public String getProperty1() {
        return property1;
    }

    /**
     * @param property1 the property1 to set
     */
    public void setProperty1(String property1) {
        this.property1 = property1;
    }

    /**
     * Getter for property2.
     *
     * @return the property2.
     */
    public int getProperty2() {
        return property2;
    }

    /**
     * @param property2 the property2 to set
     */
    public void setProperty2(int property2) {
        this.property2 = property2;
    }

    /**
     * Getter for property3.
     *
     * @return the property3.
     */
    public int getProperty3() {
        return property3;
    }

    /**
     * @param property3 the property3 to set
     */
    public void setProperty3(int property3) {
        this.property3 = property3;
    }

    /**
     * Getter for property4.
     *
     * @return the property4.
     */
    public int getProperty4() {
        return property4;
    }

    /**
     * @param property4 the property4 to set
     */
    public void setProperty4(int property4) {
        this.property4 = property4;
    }

}