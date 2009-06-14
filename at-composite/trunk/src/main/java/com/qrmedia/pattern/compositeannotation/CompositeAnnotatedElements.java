/*
 * @(#)CompositeAnnotatedElements.java     28 May 2009
 * 
 * Copyright © 2009 Andrew Phillips.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
import com.qrmedia.pattern.compositeannotation.metadata.CompositeAnnotationDescriptor;
import com.qrmedia.pattern.compositeannotation.metadata.CompositeAnnotationTypeRegistry;

/**
 * An implementation of {@link AnnotatedElements} based solely on composite
 * annotations.
 * 
 * @author aphillips
 * @since 28 May 2009
 *
 */
public class CompositeAnnotatedElements implements AnnotatedElements {
    private final Map<Class<? extends Annotation>, CompositeAnnotationDescriptor<? extends Annotation>> compositeAnnotationTypeDescriptors = 
        new HashMap<Class<? extends Annotation>, CompositeAnnotationDescriptor<? extends Annotation>>();
    
    // a map of "standard" annotation -> composites providing that annotation
    private final Map<Class<? extends Annotation>, Set<Class<? extends Annotation>>> leafAnnotationTypeProvidingCompositeTypes =
        new HashMap<Class<? extends Annotation>, Set<Class<? extends Annotation>>>();
    
    /**
     * Creates a {@code CompositeAnnotatedElements} instance based on the given
     * composite annotation registry.
     * 
     * @param compositeAnnotationTypeRegistry the registry of supported composite annotation types
     * @throws IllegalArgumentException if {@code compositeAnnotationTypeRegistry} is {@code null} or
     *                                  one of the given annotation types is not a valid composite
     */
    @SuppressWarnings("unchecked")
    public CompositeAnnotatedElements(CompositeAnnotationTypeRegistry compositeAnnotationTypeRegistry) {
        checkNotNull("'compositeAnnotationTypeRegistry' may not be null", compositeAnnotationTypeRegistry);
        
        // create descriptors for each composite - will blow up if one is not valid
        for (Class<? extends Annotation> compositeAnnotationType 
                : compositeAnnotationTypeRegistry.getCompositeAnnotationTypes()) {
            // TODO: Urgh: see if we can avoid the cast here
            compositeAnnotationTypeDescriptors.put(compositeAnnotationType, 
                    new CompositeAnnotationDescriptor<Annotation>(
                            (Class<Annotation>) compositeAnnotationType));
        }
        
        for (Entry<Class<? extends Annotation>, CompositeAnnotationDescriptor<? extends Annotation>> compositeAnnotationDescriptor
                : compositeAnnotationTypeDescriptors.entrySet()) {
            addProvidingCompositeType(
                    compositeAnnotationDescriptor.getValue().getLeafAnnotationTypes(),
                    compositeAnnotationDescriptor.getKey());
        }
        
    }
    
    // add to the "standard" -> providing composite map based on the descriptor information
    private void addProvidingCompositeType(Set<Class<? extends Annotation>> leafAnnotationTypes,
            Class<? extends Annotation> compositeAnnotationType) {
        
        for (Class<? extends Annotation> leafAnnotationType : leafAnnotationTypes) {
            // before the first entry is added the value collection must be created
            if (!leafAnnotationTypeProvidingCompositeTypes.containsKey(leafAnnotationType)) {
                leafAnnotationTypeProvidingCompositeTypes.put(leafAnnotationType, 
                        new HashSet<Class<? extends Annotation>>());
            }
            
            leafAnnotationTypeProvidingCompositeTypes.get(leafAnnotationType)
            .add(compositeAnnotationType);
        }
        
    }

    /* (non-Javadoc)
     * @see com.qrmedia.pattern.compositeannotation.api.AnnotatedElements#getAnnotation(java.lang.reflect.AnnotatedElement, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotation(
            AnnotatedElement annotatedElement, Class<T> annotationClass) {
        checkNotNull("The element and class must be non-null", annotatedElement, annotationClass);
        
        // if *no* composite provides the type it cannot be present
        if (!leafAnnotationTypeProvidingCompositeTypes.containsKey(annotationClass)) {
            return null;
        }
        
        // throws an IllegalArgumentException if there is more than one providing composite
        Collection<Class<? extends Annotation>> presentProvidingCompositeTypes = 
            getPresentProvidingCompositeTypes(annotatedElement, annotationClass);
        
        if (presentProvidingCompositeTypes.isEmpty()) {
            return null;
        }
        
        Class<? extends Annotation> presentProvidingCompositeType = 
            presentProvidingCompositeTypes.iterator().next();
        
        return ((CompositeAnnotationDescriptor<Annotation>) 
                compositeAnnotationTypeDescriptors.get(presentProvidingCompositeType))
               .getLeafAnnotation(annotatedElement.getAnnotation(presentProvidingCompositeType), 
                                  annotationClass);
    }

    @SuppressWarnings("unchecked")
    private Collection<Class<? extends Annotation>> getPresentProvidingCompositeTypes(
            AnnotatedElement annotatedElement, Class<? extends Annotation> annotationClass) {
        // check how many of the composite elements that deliver the required elements are present
        Collection<Class<? extends Annotation>> presentProvidingCompositeTypes =
            (Collection<Class<? extends Annotation>>) CollectionUtils.intersection(
                    getCompositeAnnotationTypes(annotatedElement, true),
                    leafAnnotationTypeProvidingCompositeTypes.get(annotationClass));
        
        // at most one providing composite may be present
        if (presentProvidingCompositeTypes.size() > 1) {
            throw new IllegalArgumentException(annotatedElement 
                   + " is annotated with multiple composite annotations ("
                    + presentProvidingCompositeTypes + ") that declare leaves of type "
                    + annotationClass);
        }
        
        return presentProvidingCompositeTypes;
    }
    
    // uses getMethods() or getDeclaredMethods(), depending on the value of includeInherited
    private Set<Class<? extends Annotation>> getCompositeAnnotationTypes(AnnotatedElement annotatedElement,
            boolean includeInherited) {
        Annotation[] annotations = (includeInherited ? annotatedElement.getAnnotations()
                                                     : annotatedElement.getDeclaredAnnotations());
        Set<Class<? extends Annotation>> compositeAnnotations = 
            new HashSet<Class<? extends Annotation>>();
        
        // collect all the annotations that are annotated with @CompositeAnnotation
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            
            if (compositeAnnotationTypeDescriptors.containsKey(annotationType)) {
                compositeAnnotations.add(annotationType);
            }
            
        }
        
        return compositeAnnotations;
    }

    /* (non-Javadoc)
     * @see com.qrmedia.pattern.compositeannotation.api.AnnotatedElements#getAnnotations(java.lang.reflect.AnnotatedElement)
     */
    public Annotation[] getAnnotations(AnnotatedElement annotatedElement) {
        return getAnnotations(annotatedElement, true);
    }
    
    @SuppressWarnings("unchecked")
    private Annotation[] getAnnotations(AnnotatedElement annotatedElement, boolean includeInherited) {
        checkNotNull("The element must be non-null", annotatedElement);
        
        Set<Annotation> leafAnnotations = new HashSet<Annotation>();
        Set<Class<? extends Annotation>> presentLeafAnnotationTypes = 
            new HashSet<Class<? extends Annotation>>();
        
        for (Class<? extends Annotation> compositeAnnotationType 
                : getCompositeAnnotationTypes(annotatedElement, includeInherited)) {
            CompositeAnnotationDescriptor<Annotation> compositeAnnotationDescriptor = 
                (CompositeAnnotationDescriptor<Annotation>) 
                    compositeAnnotationTypeDescriptors.get(compositeAnnotationType);
            Set<Class<? extends Annotation>> newLeafAnnotationTypes = 
                compositeAnnotationDescriptor.getLeafAnnotationTypes();
            
            // multiple composites may not return leaf annotations of the same type
            if (CollectionUtils.containsAny(presentLeafAnnotationTypes, newLeafAnnotationTypes)) {
                throw new IllegalArgumentException(annotatedElement 
                   + " is annotated with multiple composite annotations that declare leaves of the same type");
            }
            
            presentLeafAnnotationTypes.addAll(newLeafAnnotationTypes);
            leafAnnotations.addAll(compositeAnnotationDescriptor.getLeafAnnotations(
                    annotatedElement.getAnnotation(compositeAnnotationType)));
        }
        
        return leafAnnotations.toArray(new Annotation[0]);        
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
        
        // if *no* composite provides the type it cannot be present
        if (!leafAnnotationTypeProvidingCompositeTypes.containsKey(annotationClass)) {
            return false;
        }
        
        // will throw an IllegalArgumentException if there is more than providing composite
        return (getPresentProvidingCompositeTypes(annotatedElement, annotationClass).size() == 1);
    }

}
