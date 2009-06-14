/*
 * @(#)InvalidCompositeAnnotationInconsistentTarget.java     30 May 2009
 */
package com.qrmedia.pattern.compositeannotation.validation.sample;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.PostConstruct;

import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;

/**
 * A composite with a leaf annotation whose target does not match the
 * composite's target.
 * 
 * @author aphillips
 * @since 30 May 2009
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@CompositeAnnotation
public @interface InvalidCompositeAnnotationInconsistentTarget {

    @LeafAnnotation
    PostConstruct postConstructLeafAnnotation() default @PostConstruct();
}
