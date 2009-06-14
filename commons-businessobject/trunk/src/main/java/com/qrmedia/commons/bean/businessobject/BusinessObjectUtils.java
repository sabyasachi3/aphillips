/*
 * @(#)BusinessObjectUtils.java     7 Jul 2008
 */
package com.qrmedia.commons.bean.businessobject;

import static com.qrmedia.commons.bean.businessobject.BusinessObjectDescriptor.isBusinessObject;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.qrmedia.commons.bean.businessobject.annotation.BusinessObject;
import com.qrmedia.commons.lang.ClassUtils;

/**
 * An optimized version of the <a href="http://code.google.com/p/simplestuff/">SimpleStuff</a> 
 * utility that provides a fail-fast <code>equals<code> implementation that can also deal with 
 * <code>null</code> arguments.
 * 
 * @author anph
 * @since 7 Jul 2008
 */
public class BusinessObjectUtils {

    
    /**
     * Compares two business objects.
     * 
     * @param firstBean the first business object
     * @param secondBean        the second business object
     * @return  <code>true</code> iff the two instances are equal according to the business object
     *          rules
     * @see Object#equals(Object)
     */
    public static boolean equals(Object firstBean, Object secondBean) {
        
        if ((firstBean == null) || (secondBean == null)) {
            
            // two null objects *are* equal
            return ((firstBean == null) && (secondBean == null));
        } 
        
        Class<? extends Object> firstBeanClass = firstBean.getClass();
        Class<? extends Object> secondBeanClass = secondBean.getClass();

        if (!isBusinessObject(firstBeanClass) || !isBusinessObject(secondBeanClass)) {
            return false;
        }
        
        /*
         * If either of the objects specifies permitted equivalent classes, the other object
         * must be an instance of one of these, or one of their subclasses. 
         */
        BusinessObjectDescriptor firstBeanDescriptor = 
            BusinessObjectContext.getDescriptor(firstBeanClass);
        Collection<Class<?>> firstBeanEquivalentClasses = firstBeanDescriptor.getEquivalentClasses();
        
        if (!firstBeanDescriptor.ignoreClass()
                && !ClassUtils.isAnyAssignableFrom(firstBeanEquivalentClasses, secondBeanClass)) {
            return false;
        }
            
        BusinessObjectDescriptor secondBeanDescriptor = 
            BusinessObjectContext.getDescriptor(secondBeanClass);
        Collection<Class<?>> secondBeanEquivalentClasses = secondBeanDescriptor.getEquivalentClasses();
        
        if (!secondBeanDescriptor.ignoreClass() 
                && !ClassUtils.isAnyAssignableFrom(secondBeanEquivalentClasses, firstBeanClass)) {
            return false;
        }
        
        /*
         * Compare the set of declared business fields of both object (which may be of different
         * classes!) to ensure true is returned iff we really have two objects with identical
         * business fields (which of course also have to have the same values).
         */
        Map<String, BusinessFieldDescriptor> firstBeanBusinessFieldsRelevantForEquals = 
            firstBeanDescriptor.getBusinessFieldsRelevantForEquals();
        Map<String, BusinessFieldDescriptor> secondBeanBusinessFieldsRelevantForEquals = 
            secondBeanDescriptor.getBusinessFieldsRelevantForEquals();
        
        if (!firstBeanBusinessFieldsRelevantForEquals.keySet().equals(
                secondBeanBusinessFieldsRelevantForEquals.keySet())) {
            return false;
        }
        
        // compare properties, failing as soon one is found that does not match
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        
        for (Entry<String, BusinessFieldDescriptor> fieldRelevantForEquals 
                : firstBeanBusinessFieldsRelevantForEquals.entrySet()) {
            /*
             * The descriptor for the first bean's property can *not* be used to retrieve the
             * value from the second bean since, even though the properties have the same *name*,
             * they may be declared in different *classes*. 
             */
            equalsBuilder.append(fieldRelevantForEquals.getValue().getValue(firstBean),
                                 secondBeanBusinessFieldsRelevantForEquals
                                 .get(fieldRelevantForEquals.getKey()).getValue(secondBean));
            
            if (!equalsBuilder.isEquals()) {
                return false;
            }
            
        }
        
        return true;
    }


    /**
     * Calculates the hash code for a business object.
     * <p>
     * <b>Note:</b> The implementation conforms to the hash code contract in that it returns
     * identical hash codes for equal objects. In general, it also returns distinct hash codes for
     * <i>un</i>equal objects.<br/>
     * However, two objects whose business fields match, but which are not equal due to incompatible
     * {@link BusinessObject#equivalentClasses() equivalent class specifications}, will have the
     * same hash code. 
     * 
     * @param bean      the business object whose hash code should be calculated
     * @return  the hash code for the business object
     * @throws IllegalArgumentException if the bean is not a business object  
     * @see Object#hashCode()
     */
    /*
     * It would be nice to avoid having identical hash codes for field-equal but 
     * class-incompatible instances, but it doesn't appear doable. 
     * For this to work, one would need to add some "special sauce" value to the hash code of an 
     * instance with class restrictions, to prevent it from being equal to a field-equals but 
     * class-incompatible class.
     * 
     * But now imagine a business object class with an empty "equivalentClasses" array, i.e. can be
     * compared to any class. This clearly could *not* have any special sauce value; its hash code
     * would have to be purely based on fields.
     * Assume, then, that a business object with equals field values that *does* contain class 
     * restrictions includes this "free" class in its equivalentClasses array. 
     * Then one class *must* have a special sauce value in its hash code, and the other must 
     * *not*, yet their hash codes are supposed to be equal. QED.
     */
    public static int hashCode(Object bean) {
        validateBusinessObject(bean);
        
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        
        for (BusinessFieldDescriptor fieldRelevantForHashCodeDescriptor 
                : BusinessObjectContext.getDescriptor(bean.getClass())
                  .getBusinessFieldsRelevantForHashCode().values()) {
            hashCodeBuilder.append(fieldRelevantForHashCodeDescriptor.getValue(bean));
        }
        
        return hashCodeBuilder.toHashCode();
    }

    private static void validateBusinessObject(Object bean) {
        
        if (bean == null) {
            throw new IllegalArgumentException("A non-null business object is expected");
        }
        
        if (!isBusinessObject(bean.getClass())) {
            
            // Careful! Can't use "bean" in the error message since that would call toString!
            throw new IllegalArgumentException("The object's " + bean.getClass() 
                                               + " is not a business object class");            
        }
        
    }
    

    /**
     * Creates a human-readable string representation of a business object, based on the
     * appropriate business fields.
     * 
     * @param bean      the business object for which a string representation is required
     * @return  a business-field based string representation of the object
     * @throws IllegalArgumentException if the bean is not a business object 
     * @see Object#toString()
     */
    public static String toString(Object bean) {
        validateBusinessObject(bean);
        
        ToStringBuilder toStringBuilder = new ToStringBuilder(bean);
        
        for (Entry<String, BusinessFieldDescriptor> fieldRelevantForToString 
                : BusinessObjectContext.getDescriptor(bean.getClass())
                  .getBusinessFieldsRelevantForToString().entrySet()) {
            toStringBuilder.append(fieldRelevantForToString.getKey(),
                                   fieldRelevantForToString.getValue().getValue(bean));
        }
        
        return toStringBuilder.toString();
    }    

}
