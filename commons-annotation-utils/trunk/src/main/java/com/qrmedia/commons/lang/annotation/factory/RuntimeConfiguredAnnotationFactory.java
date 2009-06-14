/*
 * @(#)RuntimeConfiguredAnnotationFactory.java     25 May 2009
 */
package com.qrmedia.commons.lang.annotation.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ClassUtils;



/**
 * A factory that creates instances of annotations whose members (attributes) can be configured
 * <em>at runtime</em>.
 * 
 * @author aphillips
 * @since 25 May 2009
 *
 */
public class RuntimeConfiguredAnnotationFactory {

    /**
     * Creates a new instance of the requested annotation whose member values are determined
     * by the {@code members} argument, which is a map of member names to the desired values.
     * <p>
     * Values for all the members declared in the annotation must be specified, and the values
     * must have the correct types.
     * 
     * @param <U> the type of the annotation
     * @param annotationType    the class corresponding to the annotation type
     * @param memberValues      a map of {@code name -> value} pairs for the annotation's members
     * @return  an annotation whose values are determined by the given map
     * @throws IllegalArgumentException if either of the arguments are {code null} or the member
     *                                  value map does not contain a valid entry for all members
     * 
     */
    @SuppressWarnings("unchecked")
    public static <U extends Annotation> U newInstance(Class<U> annotationType, 
            Map<String, Object> members) throws IllegalArgumentException {
        
        if ((annotationType == null) || (members == null)) {
            throw new IllegalArgumentException("All arguments must be non-null");
        }
        
        Map<String, Class<?>> actualMembers = getMembers(annotationType);
        
        // ensure the given members match those of the annotation
        if (!members.keySet().equals(actualMembers.keySet())) {
            throw new IllegalArgumentException(members.keySet() 
                    + " does not match the members declared by " + annotationType);
        }
        
        validateReturnTypes(members, actualMembers);
        
        return (U) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), 
                new Class<?>[] { annotationType }, 
                new ConstantAnnotationMemberValueInvocationHandler(annotationType, 
                        new HashMap<String, Object>(members)));
    }
    
    private static Map<String, Class<?>> getMembers(Class<? extends Annotation> annotationType) {
        Map<String, Class<?>> members = new HashMap<String, Class<?>>();
        
        // only look at the methods actually declared in the annotation (to avoid equals() etc.)
        for (Method member : annotationType.getDeclaredMethods()) {
            members.put(member.getName(), member.getReturnType());
        }
        
        return members;
    } 
    
    // check the given return values against the actual method declarations
    private static void validateReturnTypes(Map<String, Object> methodReturnValues,
            Map<String, Class<?>> actualMethodSignatures) {
    
        // ensures the given values are of the required return types
        for (Entry<String, Object> methodReturnValue : methodReturnValues.entrySet()) {
            /*
             * The return values are all objects, so if the annotation actually declares
             * a primitive type a simple "instanceof" will fail. So primitive types need to
             * be converted to their wrapper types first.
             *
             * Null values will also fail at this stage (they are never instances of anything),
             * which is as required because null is not a valid annotation member value.
             */
            if (!ClassUtils.primitiveToWrapper(actualMethodSignatures
                    .get(methodReturnValue.getKey())).isInstance(methodReturnValue.getValue())) {
                String memberName = methodReturnValue.getKey();
                Object desiredValue = methodReturnValue.getValue();
                throw new IllegalArgumentException(desiredValue
                        + ((desiredValue != null) ? " of type " + desiredValue.getClass() : "")
                        + " is not an instance of " + actualMethodSignatures.get(memberName)
                        + ", which is the type required for annotation member '" + memberName + "'");
            }
            
        }
        
    }
    
}
