/*
 * @(#)AnnotationFactory.java     1 May 2009
 */
package com.qrmedia.pattern.compositeannotation.api;

import java.lang.annotation.Annotation;

import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;

/**
 * A factory for creating annotations. This can be used to return leaf annotations
 * whose properties depend on the properties of the {@link CompositeAnnotation}
 * they are part of.
 * <p>
 * Declaring such &quot;dynamic&quot; properties is not possible using the standard
 * {@code @Annotation(value = ...)} syntax, which only supports <em>constant</em>
 * value expressions.
 *  
 * @author aphillips
 * @since 1 May 2009
 * 
 * @param <U>   the type of the leaf annotation returned
 * @param <V>   the type of the composite annotation declaring the leaf annotation
 */
public interface LeafAnnotationFactory<U extends Annotation, V extends Annotation> {

    /**
     * Creates a new instance of a leaf annotation for a given instance of the
     * composite annotation it is declared in.
     * <p>
     * Implementors can assume that {@code declaringCompositeAnnotation} is always
     * non-{@code null}.
     *  
     * @param declaringCompositeAnnotation the instance of the composite annotation containing
     *                                     the leaf annotation
     * @return an instance of the leaf annotation given the declaring composite annotation instance
     */
    U newInstance(V declaringCompositeAnnotation);
}
