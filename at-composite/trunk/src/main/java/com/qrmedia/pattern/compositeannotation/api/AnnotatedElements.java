/*
 * @(#)AnnotatedElements.java     1 May 2009
 */
package com.qrmedia.pattern.compositeannotation.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;

/**
 * An extenstion to the standard annotation reflection offered by
 * {@link AnnotatedElement the Java language} that incorporates composite annotations.
 * <p>
 * For &quot;regular&quot; annotations, the behaviour of the methods is identical to
 * that of the analogous {@link AnnotatedElement} methods. 
 *  
 * @author aphillips
 * @since 1 May 2009
 * @see CompositeAnnotation
 *
 */
public interface AnnotatedElements {
    /**
     * Returns {@code true} if an annotation for the specified type
     * is present on the given element, else {@code false}. Analogous
     * to {@link AnnotatedElement#isAnnotationPresent(Class)}.
     *
     * @param annotatedElement the annotated element
     * @param annotationClass the Class object corresponding to the
     *        annotation type
     * @return {@code true} iff an annotation for the specified annotation
     *         type is present on the given element
     * @throws IllegalArgumentException
     *           if the given annotated element or annotation class is {@code null}
     */
    boolean isAnnotationPresent(AnnotatedElement annotatedElement, 
             Class<? extends Annotation> annotationClass);

   /**
     * Returns the given element's annotation for the specified type if
     * such an annotation is present, else {@code null}. Analogous
     * to {@link AnnotatedElement#getAnnotation(Class)}.
     *
     * @param annotatedElement the annotated element
     * @param annotationClass the Class object corresponding to the 
     *        annotation type
     * @return the given element's annotation for the specified annotation type if
     *     present on this element, else null
     * @throws IllegalArgumentException
     *           if the given annotated element or annotation class is {@code null}

     */
    <T extends Annotation> T getAnnotation(AnnotatedElement annotatedElement,
            Class<T> annotationClass);

    /**
     * Returns all annotations present on the given element. Analogous
     * to {@link AnnotatedElement#getAnnotations()}.
     *
     * @return all annotations present on the given element
     * @throws IllegalArgumentException
     *           if the given annotated element is {@code null}

     */
    Annotation[] getAnnotations(AnnotatedElement annotatedElement);

    /**
     * Returns all annotations that are directly present on the given
     * element. Analogous to {@link AnnotatedElement#getDeclaredAnnotations()}.
     * <p>
     * The leaf annotations of a composite are considered to be declared on the same
     * element as the composite that declares them.
     *
     * @return all annotations directly present on the given element
     * @throws IllegalArgumentException
     *           if the given annotated element is {@code null}
     */
    Annotation[] getDeclaredAnnotations(AnnotatedElement annotatedElement);
}
