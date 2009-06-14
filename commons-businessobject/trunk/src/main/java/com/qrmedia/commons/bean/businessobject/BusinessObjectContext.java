/*
 * @(#)BusinessObjectContext.java     9 Jul 2008
 */
package com.qrmedia.commons.bean.businessobject;

import java.util.HashMap;
import java.util.Map;

/**
 * A cache of <code>{@link BusinessObjectDescriptor}</code>s.
 * <p>
 * Thread-safe as long as there are not two different classes with the same (full) name in
 * the environment in which the <code>BusinessObjectUtils</code> are used.
 *  
 * @author anph
 * @see BusinessObjectUtils
 * @since 9 Jul 2008
 *
 */
class BusinessObjectContext {
    private static final Map<Class<?>, BusinessObjectDescriptor> BUSINESS_OBJECT_DESCRIPTORS = 
        new HashMap<Class<?>, BusinessObjectDescriptor>();
    
    /**
     * Gets a descriptor for the given business object class.
     * <p>
     * Assumes that the class actually is a valid business object class.
     * 
     * @param businessObjectClass   the class whose descriptor is required
     * @return  a descriptor for the given business object class
     */
    static BusinessObjectDescriptor getDescriptor(Class<?> businessObjectClass) {
        
        if (!BUSINESS_OBJECT_DESCRIPTORS.containsKey(businessObjectClass)) {
            BUSINESS_OBJECT_DESCRIPTORS.put(businessObjectClass, 
                    BusinessObjectDescriptor.forBusinessObjectClass(businessObjectClass));
        }
        
        return BUSINESS_OBJECT_DESCRIPTORS.get(businessObjectClass);
    }
    
}
