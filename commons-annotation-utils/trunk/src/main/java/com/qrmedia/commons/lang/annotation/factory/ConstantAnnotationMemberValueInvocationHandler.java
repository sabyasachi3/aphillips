/*
 * @(#)ConstantAnnotationMemberValueInvocationHandler.java     27 May 2009
 */
package com.qrmedia.commons.lang.annotation.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * An invocation handler that returns fixed values for annotation members and mimics the
 * {@link Annotation#equals(Object) equals}, {@link Annotation#hashCode() hashCode} and
 * {@link Annotation#toString() toString} behaviour of the &quot;real&quot; annotation.
 *  
 * @author aphillips
 * @since 27 May 2009
 *
 */
class ConstantAnnotationMemberValueInvocationHandler extends ConstantReturnValueInvocationHandler {
    private static final Method OBJECT_EQUALS_METHOD;
    private static final Method OBJECT_HASHCODE_METHOD;
    private static final Method OBJECT_TOSTRING_METHOD;
    private static final Method ANNOTATION_ANNOTATIONTYPE_METHOD;
    
    static {
        
        try {
            OBJECT_EQUALS_METHOD = 
                Object.class.getMethod("equals", new Class[] { Object.class });
            OBJECT_HASHCODE_METHOD = Object.class.getMethod("hashCode");
            OBJECT_TOSTRING_METHOD = Object.class.getMethod("toString");
            ANNOTATION_ANNOTATIONTYPE_METHOD = Annotation.class.getMethod("annotationType");
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
        
    }
    
    private final Class<? extends Annotation> annotationType;
    private final Set<Method> members = new HashSet<Method>();

    protected ConstantAnnotationMemberValueInvocationHandler(
            Class<? extends Annotation> annotationType, Map<String, Object> memberValues) {
        super(memberValues);
        this.annotationType = annotationType;
        
        for (String memberName : memberValues.keySet()) {
            
            try {
                members.add(annotationType.getMethod(memberName));
            } catch (Exception exception) {
                throw new AssertionError(exception);
            }
            
        }
        
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.lang.annotation.RuntimeConfiguredAnnotationFactory.ConstantReturnValueInvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        // provide the documented implementation for equals, hashCode, toString and annotationType
        if (method.equals(OBJECT_EQUALS_METHOD)) {
            return ProxiedAnnotationUtils.equals(
                    annotationType.cast(proxy), methodReturnValues, args[0]);
        } else if (method.equals(OBJECT_HASHCODE_METHOD)) {
            return ProxiedAnnotationUtils.hashCode(methodReturnValues);
        } else if (method.equals(OBJECT_TOSTRING_METHOD)) {
            return ProxiedAnnotationUtils.toString(methodReturnValues, annotationType.cast(proxy));
        } else if (method.equals(ANNOTATION_ANNOTATIONTYPE_METHOD)) {
            return annotationType;
        }
        
        return super.invoke(proxy, method, args);
    }
    
    
}