/*
 * @(#)CompositeAnnotationTypeRegistry.java     30 May 2009
 */
package com.qrmedia.pattern.compositeannotation.metadata;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * A registry of all the composite annotation types available at runtime.
 * 
 * @author aphillips
 * @since 30 May 2009
 *
 */
public class CompositeAnnotationTypeRegistry {
    private final Set<Class<? extends Annotation>> compositeAnnotationTypes;

    /**
     * Creates a {@code CompositeAnnotationTypeRegistry} containing the given 
     * (non-{@code null}!) set of annotation types.
     *  
     * @param compositeAnnotationTypes
     */
    public CompositeAnnotationTypeRegistry(
            Set<Class<? extends Annotation>> compositeAnnotationTypes) {
        assert (compositeAnnotationTypes != null);
        this.compositeAnnotationTypes = compositeAnnotationTypes;
    }
    
    /**
     * Callers are free to modify the returned set.
     * 
     * @return the compositeAnnotationTypes
     */
    public Set<Class<? extends Annotation>> getCompositeAnnotationTypes() {
        return new HashSet<Class<? extends Annotation>>(compositeAnnotationTypes);
    }
    
}
