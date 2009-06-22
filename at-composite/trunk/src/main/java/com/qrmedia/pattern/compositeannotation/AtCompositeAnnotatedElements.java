/*
 * @(#)AtCompositeAnnotatedElements.java     28 May 2009
 */
package com.qrmedia.pattern.compositeannotation;

import static com.qrmedia.commons.validation.ValidationUtils.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;

import com.qrmedia.pattern.compositeannotation.api.AnnotatedElements;
import com.qrmedia.pattern.compositeannotation.metadata.CompositeAnnotationTypeRegistry;

/**
 * The <strong>@Composite</strong> {@link AnnotatedElements} implementations that supports composites
 * and &quot;standard&quot; annotations.
 * <p>
 * <strong><u>Thread-safe</u></strong>, so may be created once and made available to multiple
 * threads.
 * 
 * <h3>Integrating with Spring</h3>
 * 
 * Of course, <strong>@Composite</strong> integrates easily with <a href="http://www.springsource.org/about">Spring</a>, 
 * if desired. Simply ensure the {@code atComposite-beanContext.xml} context is loaded and that a 
 * set with bean ID <em>com.qrmedia.pattern.compositeannotation.compositeAnnotationTypes</em> is 
 * declared 
 * (see the <a href="http://static.springframework.org/spring/docs/2.5.x/reference/xsd-config.html#xsd-config-body-schemas-util-set">
 * Spring reference</a> for more information on creating sets).
 * 
 * <h4>Recommendation</h4>
 * 
 * If you are creating a library or other project that declares composite annotations which should
 * be available to users of this code that use Spring, define a set with an ID listing these
 * composites, e.g.
 * 
 * <pre>
 * &lt;beans&gt;
 *   ...
 *   &lt;util:set id="uk.gov.mi6.compositeAnnotationTypes"&gt;
 *     &lt;value&gt;uk.gov.mi6.compositeannotation.DoubleOhAgentCompositeAnnotation&lt;/value&gt;
 *     &lt;value&gt;uk.gov.mi6.compositeannotation.LicenceToKillCompositeAnnotation&lt;/value&gt;
 *     ...
 *   &lt;/util:set&gt;
 *   ...
 * &lt;/beans&gt;  
 * </pre>
 * 
 * In this way, the &quot;end&quot; user (i.e. the one creating the top-level context) need only
 * reference the sets of composite annotations of the various included libraries/sub-projects,
 * without needing to know precisely which classes appear in those lists.
 * <p>
 * Depending on how the context is set up, this might be done via Spring's <a href="http://static.springframework.org/spring/docs/2.5.x/reference/beans.html#beans-collection-elements-merging">
 * collection merging</a>, or by writing a simple {@code "SetMergingFactoryBean"} that
 * combines multiple sets, for instance.
 * 
 * @author aphillips
 * @since 28 May 2009
 *
 */
public class AtCompositeAnnotatedElements implements AnnotatedElements {
    private final JavaAnnotatedElements javaAnnotatedElements = new JavaAnnotatedElements();
    private final CompositeAnnotatedElements compositeAnnotatedElements;

    private final Set<Class<? extends Annotation>> compositeAnnotationTypes;
    
    /**
     * Creates an {@code AtCompositeAnnotatedElements} instance based on the given
     * composite annotation registry.
     * 
     * @param compositeAnnotationTypeRegistry the registry of supported composite annotation types
     * @throws IllegalArgumentException if {@code compositeAnnotationTypes} is {@code null} or
     *                                  one of the given annotation types is not a valid composite
     */
    public AtCompositeAnnotatedElements(CompositeAnnotationTypeRegistry compositeAnnotationTypeRegistry) {
        checkNotNull("'compositeAnnotationTypeRegistry' may not be null", compositeAnnotationTypeRegistry);
        compositeAnnotatedElements = new CompositeAnnotatedElements(compositeAnnotationTypeRegistry);
        this.compositeAnnotationTypes = compositeAnnotationTypeRegistry.getCompositeAnnotationTypes();
    }
    
    /* (non-Javadoc)
     * @see com.qrmedia.pattern.compositeannotation.api.AnnotatedElements#getAnnotation(java.lang.reflect.AnnotatedElement, java.lang.Class)
     */
    public <T extends Annotation> T getAnnotation(
            AnnotatedElement annotatedElement, Class<T> annotationClass) {
        checkNotNull("The element and class must be non-null", annotatedElement, annotationClass);
        
        // composite annotations are not visible
        if (compositeAnnotationTypes.contains(annotationClass)) {
            return null;
        }
        
        /*
         * Getting the result from the standard and composite services first, then just checking
         * for duplicates (cf. isAnnotationPresent) would be simpler.
         * However, that would *always* result in a call to compositeAnnotatedElements.getAnnotation,
         * which is presumed to be quite a bit more expensive then isAnnotationPresent.
         * 
         * The assumption behind the following implementation is that the requested annotation
         * will almost always be a "standard" annotation, so compositeAnnotatedElements.getAnnotation
         * will only very rarely be called.
         */
        T standardAnnotation = javaAnnotatedElements.getAnnotation(annotatedElement, annotationClass);
        
        if (standardAnnotation != null) {
            
            if (compositeAnnotatedElements.isAnnotationPresent(annotatedElement, annotationClass)) {
                throw new IllegalArgumentException(annotatedElement 
                        + " is annotated both with a 'standard' annotation of type " + annotationClass 
                        + " and a composite annotation that declares a leaf of that type");                
            }
            
            return standardAnnotation;
        }
        
        return compositeAnnotatedElements.getAnnotation(annotatedElement, annotationClass);
    }

    /* (non-Javadoc)
     * @see com.qrmedia.pattern.compositeannotation.api.AnnotatedElements#getAnnotations(java.lang.reflect.AnnotatedElement)
     */
    public Annotation[] getAnnotations(AnnotatedElement annotatedElement) {
        return getAnnotations(annotatedElement, true);
    }
    
    private Annotation[] getAnnotations(AnnotatedElement annotatedElement, boolean includeInherited) {
        checkNotNull("The element must be non-null", annotatedElement);
        
        Map<Annotation, Class<? extends Annotation>> standardAnnotationTypes =
            getAnnotationTypes(includeInherited 
                    ? javaAnnotatedElements.getAnnotations(annotatedElement)
                    : javaAnnotatedElements.getDeclaredAnnotations(annotatedElement));
        Map<Annotation, Class<? extends Annotation>> leafAnnotationTypes =
            getAnnotationTypes(includeInherited 
                    ? compositeAnnotatedElements.getAnnotations(annotatedElement)
                    : compositeAnnotatedElements.getDeclaredAnnotations(annotatedElement));            
        
        /*
         * An element may not have "standard" annotation and a leaf annotation from a composite
         * that are of the same type. 
         */
        if (CollectionUtils.containsAny(standardAnnotationTypes.values(), 
                                        leafAnnotationTypes.values())) {
            throw new IllegalArgumentException(annotatedElement 
                    + " is annotated with a 'standard' annotation of the same type as one of the leaf annotations declared in a composite on that type");
        }
        
        // inlining causes a generics compile error
        Collection<?> union = CollectionUtils.union(getNoncompositeAnnotations(standardAnnotationTypes), 
                                                    getNoncompositeAnnotations(leafAnnotationTypes));
        return union.toArray(new Annotation[0]);
    }
    
    private static Map<Annotation, Class<? extends Annotation>> getAnnotationTypes(
            Annotation[] annotations) {
        assert (annotations != null);
        
        Map<Annotation, Class<? extends Annotation>> annotationTypes = 
            new HashMap<Annotation, Class<? extends Annotation>>(annotations.length); 
        
        for (Annotation annotation : annotations) {
            annotationTypes.put(annotation, annotation.annotationType());
        }
        
        return annotationTypes;
    }

    private Set<Annotation> getNoncompositeAnnotations(
            Map<Annotation, Class<? extends Annotation>> annotationTypes) {
        Set<Annotation> noncompositeAnnotations = new HashSet<Annotation>(annotationTypes.size());
        
        for (Entry<Annotation, Class<? extends Annotation>> annotationAndType 
                : annotationTypes.entrySet()) {
            
            if (!compositeAnnotationTypes.contains(annotationAndType.getValue())) {
                noncompositeAnnotations.add(annotationAndType.getKey());
            }
            
        }
        
        return noncompositeAnnotations;
    }
    
    /* (non-Javadoc)
     * @see com.qrmedia.pattern.compositeannotation.api.AnnotatedElements#getDeclaredAnnotations(java.lang.reflect.AnnotatedElement)
     */
    public Annotation[] getDeclaredAnnotations(AnnotatedElement annotatedElement) {
        return getAnnotations(annotatedElement, false);
    }

    /* (non-Javadoc)
     * @see com.qrmedia.pattern.compositeannotation.api.AnnotatedElements#isAnnotationPresent(java.lang.reflect.AnnotatedElement, java.lang.Class)
     */
    public boolean isAnnotationPresent(AnnotatedElement annotatedElement,
            Class<? extends Annotation> annotationClass) {
        checkNotNull("The element and class must be non-null", annotatedElement, annotationClass);
        
        // composite annotations are not visible
        if (compositeAnnotationTypes.contains(annotationClass)) {
            return false;
        }
        
        boolean standardAnnotationPresent = 
            javaAnnotatedElements.isAnnotationPresent(annotatedElement, annotationClass);
        
        if (compositeAnnotatedElements.isAnnotationPresent(annotatedElement, annotationClass)) {
            /*
             * An element may not have "standard" annotation and a leaf annotation from a composite
             * that are of the same type. 
             */
            if (standardAnnotationPresent) {
                throw new IllegalArgumentException(annotatedElement 
                        + " is annotated both with a 'standard' annotation of type " + annotationClass 
                        + " and a composite annotation that declares a leaf of that type");
            }
         
            return true;
        } else {
            return standardAnnotationPresent;
        }
        
    }

}
