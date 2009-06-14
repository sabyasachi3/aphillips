/*
 * @(#)JavaAnnotatedElements.java     13 May 2009
 */
package com.qrmedia.pattern.compositeannotation;

import static com.qrmedia.commons.validation.ValidationUtils.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import com.qrmedia.pattern.compositeannotation.api.AnnotatedElements;

/**
 * An implementation of {@link AnnotatedElements} based on the &quot;standard&quot; Java
 * annotations.
 * 
 * @author aphillips
 * @since 13 May 2009
 * 
 */
public class JavaAnnotatedElements implements AnnotatedElements {
    /*
     * (non-Javadoc)
     * 
     * @see com.qrmedia.pattern.compositeannotation.api.AnnotatedElements#getAnnotation(java.lang.reflect.AnnotatedElement, java.lang.Class)
     */
    public <T extends Annotation> T getAnnotation(AnnotatedElement annotatedElement, 
            Class<T> annotationClass) {
        checkNotNull("The element and class must be non-null", annotatedElement, annotationClass);
        return annotatedElement.getAnnotation(annotationClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.qrmedia.pattern.compositeannotation.api.AnnotatedElements#getAnnotations(java.lang.reflect.AnnotatedElement)
     */
    public Annotation[] getAnnotations(AnnotatedElement annotatedElement) {
        checkNotNull("The element must be non-null", annotatedElement);
        return annotatedElement.getAnnotations();
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.qrmedia.pattern.compositeannotation.api.AnnotatedElements#
     * getDeclaredAnnotations(java.lang.reflect.AnnotatedElement)
     */
    public Annotation[] getDeclaredAnnotations(AnnotatedElement annotatedElement) {
        checkNotNull("The element must be non-null", annotatedElement);
        return annotatedElement.getDeclaredAnnotations();
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.qrmedia.pattern.compositeannotation.api.AnnotatedElements#
     * isAnnotationPresent(java.lang.reflect.AnnotatedElement, java.lang.Class)
     */
    public boolean isAnnotationPresent(AnnotatedElement annotatedElement,
            Class<? extends Annotation> annotationClass) {
        checkNotNull("The element and class must be non-null", annotatedElement, annotationClass);
        return annotatedElement.isAnnotationPresent(annotationClass);
    }

}
