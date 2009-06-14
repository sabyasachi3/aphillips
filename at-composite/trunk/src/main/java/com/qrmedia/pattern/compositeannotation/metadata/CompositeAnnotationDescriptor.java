/*
 * @(#)CompositeAnnotationDescriptor.java     26 May 2009
 */
package com.qrmedia.pattern.compositeannotation.metadata;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.qrmedia.commons.lang.ClassUtils;
import com.qrmedia.commons.lang.annotation.meta.MetaAnnotationUtils;
import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;
import com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory;

/**
 * Inspects a {@link CompositeAnnotation @CompositeAnnotation}-annotated method and extracts
 * relevant information.
 * 
 * @author aphillips
 * @since 26 May 2009
 *
 * @param <T>   the type of the composite annotation described
 */
public class CompositeAnnotationDescriptor<T extends Annotation> {
    private final Class<T> compositeAnnotationType;
    private final Set<ElementType> compositeAnnotationTarget;
    private final Map<Class<? extends Annotation>, LeafAnnotationFactory<? extends Annotation, T>> leafAnnotationFactories = 
        new HashMap<Class<? extends Annotation>, LeafAnnotationFactory<? extends Annotation, T>>();
    
    /**
     * Constructs a {@code CompositeAnnotationDescriptor} for the given composite annotation type
     * 
     * @param compositeAnnotationType the non-{@code null} type (class) of the composite annotation
     */
    public CompositeAnnotationDescriptor(Class<T> compositeAnnotationType) {
        assert ((compositeAnnotationType != null) 
                && compositeAnnotationType.isAnnotationPresent(CompositeAnnotation.class)) 
        : compositeAnnotationType;
        
        // the composite must have RUNTIME retention
        if (!MetaAnnotationUtils.getRetention(compositeAnnotationType).equals(RetentionPolicy.RUNTIME)) {
            throw new IllegalArgumentException(compositeAnnotationType 
                    + " does not have RUNTIME retention policy");
        }
        
        compositeAnnotationTarget = MetaAnnotationUtils.getTarget(compositeAnnotationType);
        
        for (Method leafAnnotatedMethod 
                : ClassUtils.getAnnotatedMethods(compositeAnnotationType, LeafAnnotation.class)) {
            /*
             * A leaf annotation must
             * 
             * 1) be valid (i.e. creating a descriptor does not throw an exception)
             * 2) have the same target as the composite
             * 3) be the only one of its type within a composite
             */
            LeafAnnotationDescriptor<? extends Annotation, T> leafAnnotationDescriptor = 
                new LeafAnnotationDescriptor<Annotation, T>(leafAnnotatedMethod);
            
            if (!leafAnnotationDescriptor.getTarget().equals(compositeAnnotationTarget)) {
                throw new IllegalArgumentException(leafAnnotatedMethod.getReturnType().getSimpleName()
                        + "'s target " + leafAnnotationDescriptor.getTarget() +  " does not match " 
                        + compositeAnnotationTarget + ", the target of the declaring composite " 
                        + compositeAnnotationType.getSimpleName());
            }
            
            Class<? extends Annotation> leafAnnotationType = leafAnnotationDescriptor.getType();
            
            if (leafAnnotationFactories.containsKey(leafAnnotationType)) {
                throw new IllegalArgumentException("Composite " + compositeAnnotationType.getSimpleName()
                        + " declares multiple leaf members of type " + leafAnnotationType);
            }
            
            leafAnnotationFactories.put(leafAnnotationType, leafAnnotationDescriptor.getFactory());
        }
        
        this.compositeAnnotationType = compositeAnnotationType;
    }
    
    /**
     * @return the target of the composite annotation
     */
    public Set<ElementType> getTarget() {
        return compositeAnnotationTarget;
    }
    
    /**
     * @return the types (classes) of the leaf annotations declared in the composite
     */
    public Set<Class<? extends Annotation>> getLeafAnnotationTypes() {
        return leafAnnotationFactories.keySet();
    }
    
    /**
     * Returns the leaf annotation instance of the given type (unique as there can never be more
     * then one!) of a composite annotation instance.
     * 
     * @param <V> the type of the leaf annotation required
     * @param compositeAnnotation the non-{@code null} composite annotation instance whose leaf 
     *                            instance is required
     * @param leafAnnotationType the non-{@code null} class of the leaf annotation required
     * @return the leaf annotation instance of the given type for the given composite instance, 
     *         or {@code null} if there is none
     * @throws IllegalArgumentException if the composite annotation is not an instance of the annotation
     *                                  class described by this descriptor
     * @see #getLeafAnnotations(Annotation)                                  
     */
    @SuppressWarnings("unchecked")
    public <V extends Annotation> V getLeafAnnotation(T compositeAnnotation, 
            Class<V> leafAnnotationType) {
        validateCompositeAnnotation(compositeAnnotation);
        
        return (V) (leafAnnotationFactories.containsKey(leafAnnotationType)
                    ? leafAnnotationFactories.get(leafAnnotationType).newInstance(compositeAnnotation)
                    : null);
    }
    
    private void validateCompositeAnnotation(T compositeAnnotation) {
        assert ((compositeAnnotation != null) 
                && compositeAnnotation.annotationType().isAnnotationPresent(CompositeAnnotation.class))
        : compositeAnnotation;

        if (!compositeAnnotationType.isInstance(compositeAnnotation)) {
            throw new IllegalArgumentException(compositeAnnotation + " is not an instance of "
                    + compositeAnnotationType.getSimpleName() 
                    + ", the type described by this descriptor");
        }
        
    }
    
    /**
     * Returns the leaf annotation instances of a composite annotation instance.
     * 
     * @param compositeAnnotation the non-{@code null} composite annotation instance whose leaf 
     *                            instance is required
     * @return the leaf annotation instances for the given composite instance
     * @throws IllegalArgumentException if the composite annotation is not an instance of the annotation
     *                                  class described by this descriptor
     * @see #getLeafAnnotation(Annotation, Class)                                  
     */
    public Set<Annotation> getLeafAnnotations(T compositeAnnotation) {
        validateCompositeAnnotation(compositeAnnotation);
        
        Set<Annotation> leafAnnotations = new HashSet<Annotation>(leafAnnotationFactories.size());

        for (LeafAnnotationFactory<? extends Annotation, T> leafAnnotationFactory 
                : leafAnnotationFactories.values()) {
            leafAnnotations.add(leafAnnotationFactory.newInstance(compositeAnnotation));
        }
        
        return leafAnnotations;
    }
    
}
