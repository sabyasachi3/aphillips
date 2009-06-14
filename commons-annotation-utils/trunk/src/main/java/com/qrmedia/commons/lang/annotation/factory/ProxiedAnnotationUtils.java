/*
 * @(#)ProxiedAnnotationUtils.java     27 May 2009
 */
package com.qrmedia.commons.lang.annotation.factory;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.StandardToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Utility methods for mimicking the &quot;default&quot; {@link Annotation#equals(Object) equals}, 
 * {@link Annotation#hashCode() hashCode} and {@link Annotation#toString() toString} methods
 * of an annotation.
 * 
 * @author aphillips
 * @since 27 May 2009
 *
 */
class ProxiedAnnotationUtils {
    // only the (memberName=memberValue, ...) bit - the @annotationClass beginning needs to be added
    private static final ToStringStyle ANNOTATION_TOSTRING_STYLE = 
        new StandardToStringStyle() {
            private static final long serialVersionUID = 5666864339220268033L;
            
            {
                setUseClassName(false);
                setUseIdentityHashCode(false);
                setContentStart("(");
                setContentEnd(")");
                setFieldSeparator(", ");
                setArrayStart("[");
                setArrayEnd("]");
            }
    
        };
        
    /**
     * Compares a proxied annotation to another object according to {@link Annotation#equals(Object)}.
     *  
     * @param proxy     the proxied annotation to be compared
     * @param memberValues      the member values of the proxied annotation
     * @param obj       the object the proxy should be compared to
     * @return  {@code true} iff the object is equal to the proxy as defined by 
     *          {@link Annotation#equals(Object)}
     */
    static boolean equals(Annotation proxy, Map<String, Object> memberValues, Object obj) {
        
        if (proxy == obj) {
            return true;
        }
        
        if (!(proxy.annotationType().isInstance(obj))) {
            return false;
        }            
        
        // compare all the members
        return equalMemberValues(memberValues, (Annotation) obj);
    }
    
    private static boolean equalMemberValues(Map<String, Object> memberValues, 
            Annotation other) {
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        
        for (Entry<String, Object> expectedMember : memberValues.entrySet()) {
            equalsBuilder.append(expectedMember.getValue(), 
                                 getMemberValue(expectedMember.getKey(), other));
            
            if (!equalsBuilder.isEquals()) {
                return false;
            }
            
        }
        
        return true;            
    }

    private static Object getMemberValue(String memberName, Annotation annotation) {
        
        try {
            return annotation.getClass().getMethod(memberName).invoke(annotation);
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
        
    }
    
    /**
     * Mimics {@code AnnotationInvocationHandler.hashCodeImpl}, see the
     * <a href="http://www.docjar.com/html/api/sun/reflect/annotation/AnnotationInvocationHandler.java.html">
     * source code</a>. 
     * 
     * @param memberValues      the member values of the proxied annotation
     * @return  the hash code for the proxied annotation with the given member values
     */
    static int hashCode(Map<String, Object> memberValues) {
        int hashCode = 0;
        
        for (Entry<String, Object> member : memberValues.entrySet()) {
            Object memberValue = member.getValue();
            assert (memberValue != null) : member;
            hashCode += (127 * member.getKey().hashCode()) ^ memberValueHashCode(memberValue);
        }
        
        return hashCode;
    }
    
    private static int memberValueHashCode(Object value) {
        // mimics Arrays.hashCode for arrays
        return (value.getClass().isArray() 
                ? new HashCodeBuilder(1, 31).append(value).toHashCode()
                : value.hashCode());
    }
    
    /**
     * Mimics the &quot;default&quot; annotation {@link Annotation#toString() toString}.
     * 
     * @param memberValues      the member values of the proxied annotation
     * @param proxy
     * @return
     */
    static String toString(Map<String, Object> memberValues, Annotation proxy) {
        StringBuffer stringBuffer = new StringBuffer("@");
        stringBuffer.append(proxy.annotationType().getName());
        ToStringBuilder toStringBuilder = 
            new ToStringBuilder(proxy, ANNOTATION_TOSTRING_STYLE, stringBuffer);
        
        for (Entry<String, Object> member : memberValues.entrySet()) {
            toStringBuilder.append(member.getKey(), member.getValue());
        }

        return toStringBuilder.toString();
    }        
    
}