/*
 * @(#)BusinessFieldDescriptor.java     8 Jul 2008
 */
package com.qrmedia.commons.bean.businessobject;

import java.lang.reflect.Field;

import org.apache.commons.beanutils.PropertyUtils;

import com.qrmedia.commons.bean.businessobject.annotation.BusinessField;

/**
 * Describes a field annotated with {@link BusinessField @BusinessField} in a business object.
 * 
 * @author anph
 * @see BusinessObjectDescriptor
 * @since 8 Jul 2008
 *
 */
class BusinessFieldDescriptor {
    private Field businessField;
    private BusinessField businessFieldAnnotation;
    
    /**
     * Retrieves this field's value from the given object.
     * <p>
     * Assumes that the given object is not <code>null</code>.
     * 
     * @param businessObject the object whose value is to be retrieved
     * @throws IllegalAccessException if the class of the business object is not a
     *                                subclass of the field's declaring class, or if the
     *                                field cannot be accessed
     */
    Object getValue(Object businessObject) throws IllegalArgumentException {
        
        if (!(businessField.getDeclaringClass().isAssignableFrom(businessObject.getClass()))) {
            throw new IllegalArgumentException(businessObject + " is not an instance of " 
                                               + businessField.getDeclaringClass());
        }
        
        try {
            return PropertyUtils.getSimpleProperty(businessObject, businessField.getName());
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to retrieve value for field '"
                    + businessField.getName() + "'", exception);
        }
        
    }
    
    /**
     * Generates a descriptor for the given business field. 
     * <p>
     * Expects the given field to contain a <code>BusinessField</code> annotation.
     * 
     * @param businessField the field for which a descriptor should be generated
     */
    static BusinessFieldDescriptor forBusinessField(Field businessField) {
        BusinessFieldDescriptor descriptor = new BusinessFieldDescriptor();
        
        descriptor.businessField = businessField;
        
        // assumes the field *has* a @BusinessField annotation
        descriptor.businessFieldAnnotation = businessField.getAnnotation(BusinessField.class);
        return descriptor;
    }  
    
    /**
     * @return <code>true</code> iff the field is relevant to the <i>equals</i> evaluation
     */
    public boolean isRelevantForEquals() {
        return businessFieldAnnotation.includeInEquals();
    }
    
    /**
     * @return <code>true</code> iff the field is relevant to the <i>hashCode</i> evaluation
     */
    public boolean isRelevantForHashCode() {
        return businessFieldAnnotation.includeInEquals();
    }
    
    /**
     * @return <code>true</code> iff the field is relevant to the <i>toString</i> evaluation
     */
    public boolean isRelevantForToString() {
        return businessFieldAnnotation.includeInToString();
    }
    
    /* Getter(s) and setter(s) */
    
    /**
     * Getter for businessField.
     *
     * @return the businessField.
     */
    public Field getBusinessField() {
        return businessField;
    }

}
