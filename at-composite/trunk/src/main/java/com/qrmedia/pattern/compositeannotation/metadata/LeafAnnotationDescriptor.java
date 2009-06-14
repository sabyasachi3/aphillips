/*
 * @(#)LeafAnnotationDescriptor.java     23 May 2009
 */
package com.qrmedia.pattern.compositeannotation.metadata;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import com.qrmedia.commons.lang.ClassUtils;
import com.qrmedia.commons.lang.annotation.meta.MetaAnnotationUtils;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;
import com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory;

/**
 * Inspects a {@link LeafAnnotation @LeafAnnotation}-annotated method and extracts
 * relevant information.
 * 
 * @author aphillips
 * @since 23 May 2009
 * 
 * @param <U>   the type of the leaf annotation described
 * @param <V>   the type of the composite declaring the leaf annotation
 */
class LeafAnnotationDescriptor<U extends Annotation, V extends Annotation> {
    // the value of the LeafAnnotation.factoryClass member that should be taken to mean "use member value"
    @SuppressWarnings("unchecked")
    private static final Class<LeafAnnotationFactory> DEFAULT_FACTORY_CLASS_VALUE = 
        LeafAnnotationFactory.class;
    
    private final Class<U> leafType;
    private final Set<ElementType> target;
    private final LeafAnnotationFactory<U, V> factory;

    /**
     * Creates a descriptor for the given method annotated with {@code @LeafAnnotation}.
     * 
     * @param leafMember     the {@code @LeafAnnotation-}annotated method
     */
    @SuppressWarnings("unchecked")
    LeafAnnotationDescriptor(Method leafMember) {
        /*
         * Assumes the (non-null) leaf member is actually annotated with @LeafAnnotation
         * and is a method of an Annotation class.
         */
        assert ((leafMember != null)
                && leafMember.isAnnotationPresent(LeafAnnotation.class)
                && Annotation.class.isAssignableFrom(leafMember.getDeclaringClass())) 
        : leafMember;
        
        Class<?> leafMemberReturnType = leafMember.getReturnType();

        // the leaf member must return an annotation
        if (!Annotation.class.isAssignableFrom(leafMemberReturnType)) {
            throw new IllegalArgumentException("The return type of the leaf member, '"
                    + leafMemberReturnType + "', is not an annotation");
        }
        
        leafType = (Class<U>) leafMemberReturnType;

        // the leaf annotation type must have a RUNTIME retention policy
        if (!MetaAnnotationUtils.getRetention(leafType).equals(RetentionPolicy.RUNTIME)) {
            throw new IllegalArgumentException(
                    "Leaf annotations must have a RUNTIME retention policy");
        }
    
        target = MetaAnnotationUtils.getTarget(leafType);
        
        Class<? extends LeafAnnotationFactory> factoryClass = 
            leafMember.getAnnotation(LeafAnnotation.class).factoryClass();
        
        // if the value is still the default assume the user is not providing a custom factory
        if (factoryClass.equals(DEFAULT_FACTORY_CLASS_VALUE)) {
            factory = new AnnotationMemberValueLeafAnnotationFactory(leafMember.getName());
        } else {
            // verify that the given factory class has the correct generic type parameters
            if (!ClassUtils.getActualTypeArguments(factoryClass, LeafAnnotationFactory.class)
                 .equals(Arrays.asList(leafType, leafMember.getDeclaringClass()))) {
                throw new IllegalArgumentException("Custom factory class '"
                        + factoryClass.getSimpleName() + "' must have generic type parameters <"
                        + leafType.getSimpleName() + ", " + leafMember.getDeclaringClass().getSimpleName()
                        + ">");
            }
            
            factory = newFactoryInstance(factoryClass);
        }
        
    }
    
    @SuppressWarnings("unchecked")
    private final LeafAnnotationFactory<U, V> 
            newFactoryInstance(Class<? extends LeafAnnotationFactory> factoryClass) {
        // assumes a public, no-argument constructor
        try {
            return (LeafAnnotationFactory<U, V>) 
                   factoryClass.getConstructor().newInstance();
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to instantiate '" + factoryClass.getSimpleName()
                    + " using the expected public, no-argument constructor", exception);
        }
        
    }
    
    /**
     * @return  a {@code LeafAnnotationFactory} for this leaf annotation
     */
    LeafAnnotationFactory<U, V> getFactory() {
        return factory;
    }
    
    /**
     * A {@code LeafAnnotationFactory} that returns the value of composite annotation's member.
     *
     * @author aphillips
     * @since 25 May 2009
     *
     */
    private class AnnotationMemberValueLeafAnnotationFactory implements LeafAnnotationFactory<U, V> {
        private final String memberName;
        
        /**
         * Creates a new {@code AnnotationMemberLeafAnnotationFactory} that will return the
         * value of the member, which is assumed to be a method of the declaring composite.
         * 
         * @param member     the member whose value should be returned
         */
        private AnnotationMemberValueLeafAnnotationFactory(String memberName) {
            assert (memberName != null) : memberName;
            this.memberName = memberName;
        }

        /* (non-Javadoc)
         * @see com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory#newInstance(java.lang.annotation.Annotation)
         */
        @SuppressWarnings("unchecked")
        public U newInstance(Annotation declaringCompositeAnnotation) {
            assert (declaringCompositeAnnotation != null); 
            
            try {
                /*
                 * Use the method of the *runtime* class (rather than the annotation class) because
                 * the annotation proxies override the member methods. The "actual" methods are
                 * abstract and not callable on the proxy. 
                 */
                Method member = declaringCompositeAnnotation.getClass().getMethod(memberName);
                
                // assumes the member exists and returns an annotation
                assert ((member != null) && Annotation.class.isAssignableFrom(member.getReturnType()))
                : new Object[] { declaringCompositeAnnotation, memberName };
            
                return (U) member.invoke(declaringCompositeAnnotation);
            } catch (Exception exception) {
                /*
                 * Nothing should really go wrong here: the method is assumed to exist and
                 * the declaring and return type should be valid.
                 */
                throw new AssertionError("Unable to retrieve leaf annotation " + memberName 
                        + " from composite " + declaringCompositeAnnotation + " due to: " 
                        + exception.getMessage());
            }
               
        }
        
    }
    
    /**
     * @return  the type (class) of the leaf annotation, which will be an {@link Annotation}
     *          subclass 
     */
    Class<U> getType() {
        return leafType;
    }
    
    /**
     * @return  the element types the leaf annotation may be applied to (as specified by a
     *          {@code @Target} annotation on the leaf element type, if present)
     */
    Set<ElementType> getTarget() {
        return target;
    }
    
}
