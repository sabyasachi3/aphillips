/*
 * @(#)BusinessObjectDescriptor.java     8 Jul 2008
 */
package com.qrmedia.commons.bean.businessobject;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.qrmedia.commons.bean.businessobject.annotation.BusinessField;
import com.qrmedia.commons.bean.businessobject.annotation.BusinessObject;
import com.qrmedia.commons.lang.ClassUtils;

/**
 * Describes a class annotated with <code>{@link BusinessObject}</code>.
 * <p>
 * <b>Important:</b> Expects the set of business fields for this class (both those declared
 * in the class and inherited from parents) to have <u>unique</u> bean property names.<br/>
 * If, for instance, a class declares a business field &quot;foo&quot;, and one of its 
 * superclasses <i>also</i> contains a business field &quot;foo&quot;, only one of these will
 * be picked up (and there is no guarantee which one that will be).
 * 
 * @author anph
 * @since 8 Jul 2008
 *
 */
class BusinessObjectDescriptor {
    private Class<?> businessObjectClass;
    private Set<Class<?>> equivalentClasses;
    private Map<String, BusinessFieldDescriptor> businessFields = 
        new HashMap<String, BusinessFieldDescriptor>();
    
    // maintain these collections separately, for quicker retrieval later
    private Map<String, BusinessFieldDescriptor> businessFieldsRelevantForEquals = 
        new HashMap<String, BusinessFieldDescriptor>();
    private Map<String, BusinessFieldDescriptor> businessFieldsRelevantForHashCode = 
        new HashMap<String, BusinessFieldDescriptor>();
    private Map<String, BusinessFieldDescriptor> businessFieldsRelevantForToString = 
        new HashMap<String, BusinessFieldDescriptor>();    

    /**
     * Generates a descriptor for the given business object. 
     * 
     * @param businessObjectClass the class for which a descriptor should be generated
     * @throws IllegalArgumentException if the class isn't annotated with 
     *         {@link BusinessObject @BusinessObject}, or if it specifies non-business object 
     *         equivalent classes
     */
    static BusinessObjectDescriptor forBusinessObjectClass(Class<?> businessObjectClass) {
        
        if (!isBusinessObject(businessObjectClass)) {
            throw new IllegalArgumentException(businessObjectClass 
                    + " is not a business class (i.e. is not annotated with @BusinessObject)");
        }
        
        BusinessObjectDescriptor descriptor = new BusinessObjectDescriptor();
        
        descriptor.businessObjectClass = businessObjectClass;
        
        // throws an IllegalArgumentException if any of the classes are not business objects
        descriptor.equivalentClasses = getEquivalentClasses(businessObjectClass);
        
        for (Field businessField : collectBusinessFields(businessObjectClass)) {
            String fieldName = businessField.getName();
            BusinessFieldDescriptor fieldDescriptor = 
                BusinessFieldDescriptor.forBusinessField(businessField);
            
            descriptor.businessFields.put(fieldName, fieldDescriptor);
            
            if (fieldDescriptor.isRelevantForEquals()) {
                descriptor.businessFieldsRelevantForEquals.put(fieldName, fieldDescriptor);
            }
            
            if (fieldDescriptor.isRelevantForHashCode()) {
                descriptor.businessFieldsRelevantForHashCode.put(fieldName, fieldDescriptor);
            }
            
            if (fieldDescriptor.isRelevantForToString()) {
                descriptor.businessFieldsRelevantForToString.put(fieldName, fieldDescriptor);
            }
            
        }
        
        return descriptor;
    }
    
    /**
     * Checks if the class or any of its superclasses are annotated as business objects.
     * 
     * @param objectClass   the class to check
     * @return  <code>true</code> iff the class or one of its superclasses has the 
     *          <code>@BusinessObject</code> annotation
     */
    static boolean isBusinessObject(Class<?> objectClass) {
        return (getAnnotatedSuperclass(objectClass) != null);
    }
    
    // retrieves the first class in the superclass hierarchy annotated with @BusinessObject, if present
    @SuppressWarnings("unchecked")
    private static <T> Class<? super T> getAnnotatedSuperclass(Class<T> objectClass) {
        return (Class<? super T>) 
                (((objectClass == null) || objectClass.isAnnotationPresent(BusinessObject.class))
                 ? objectClass
                 : getAnnotatedSuperclass(objectClass.getSuperclass()));
    }
    
    private static Set<Class<?>> getEquivalentClasses(
            Class<?> businessObjectClass) {
        Class<?> annotatedSuperclass = getAnnotatedSuperclass(businessObjectClass);
        
        assert (annotatedSuperclass != null) : businessObjectClass;
        
        Class<?>[] declaredEquivalentClasses = 
            annotatedSuperclass.getAnnotation(BusinessObject.class).equivalentClasses();
        int numDeclaredEquivalentClasses = declaredEquivalentClasses.length;
        Set<Class<?>> equivalentClasses = new HashSet<Class<?>>(numDeclaredEquivalentClasses + 1);

        for (int i = 0; i < numDeclaredEquivalentClasses; i++) {
            Class<?> declaredEquivalentClass = declaredEquivalentClasses[i];
            
            if (!isBusinessObject(declaredEquivalentClass)) {
                throw new IllegalArgumentException(declaredEquivalentClass
                        + " is not a business class (i.e. is not annotated with @BusinessObject)");
            }
            
            equivalentClasses.add(declaredEquivalentClass);
        }
        
        /*
         * If equivalentClasses are specified, the class containing the @BusinessObject annotation 
         * is *always* one of them.
         */
        if (!equivalentClasses.isEmpty() && !equivalentClasses.contains(annotatedSuperclass)) {
            equivalentClasses.add(annotatedSuperclass);
        }
        
        return equivalentClasses;
    }    
    
    private static <T> Set<Field> collectBusinessFields(Class<T> clazz) {
        final Set<Field> businessFields = new HashSet<Field>();
        
        /*
         * By definition, only business fields declared in classes *up to and
         * including* the first superclass annotated with @BusinessObject count.
         */
        for (Field field : ClassUtils.getAllDeclaredFields(clazz, getAnnotatedSuperclass(clazz))) {
            
            if (field.getAnnotation(BusinessField.class) != null) {
                businessFields.add(field);
            }
            
        }
        
        return businessFields;        
    }
    
    /**
     * Indicates if the business objects's class is relevant to <code>equals</code> comparisons.
     * If <code>true</code>, only instances of {@link #equivalentClasses} or their subclasses can
     * be equal to instances of business object class.
     * 
     * @return <code>true</code> iff an object's class is relevant to <code>equals</code> comparisons
     *         with business objects of this class
     */
    boolean ignoreClass() {
        return equivalentClasses.isEmpty();
    }
    
    /* Getter(s) and setter(s) */
    
    /**
     * Getter for businessObjectClass.
     *
     * @return the businessObjectClass.
     */
    public Class<?> getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * @return the equivalentClasses
     */
    public Collection<Class<?>> getEquivalentClasses() {
        return equivalentClasses;
    }    
    /**
     * Getter for businessFields.
     *
     * @return the businessFields.
     */
    public Map<String, BusinessFieldDescriptor> getBusinessFields() {
        return businessFields;
    }

    /**
     * Getter for businessFieldsRelevantForEquals.
     *
     * @return the businessFieldsRelevantForEquals.
     */
    public Map<String, BusinessFieldDescriptor> getBusinessFieldsRelevantForEquals() {
        return businessFieldsRelevantForEquals;
    }

    /**
     * Getter for businessFieldsRelevantForHashCode.
     *
     * @return the businessFieldsRelevantForHashCode.
     */
    public Map<String, BusinessFieldDescriptor> getBusinessFieldsRelevantForHashCode() {
        return businessFieldsRelevantForHashCode;
    }

    /**
     * Getter for businessFieldsRelevantForToString.
     *
     * @return the businessFieldsRelevantForToString.
     */
    public Map<String, BusinessFieldDescriptor> getBusinessFieldsRelevantForToString() {
        return businessFieldsRelevantForToString;
    }

}
