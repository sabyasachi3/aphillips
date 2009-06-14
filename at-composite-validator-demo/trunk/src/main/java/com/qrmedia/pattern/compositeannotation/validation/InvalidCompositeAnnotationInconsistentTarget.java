package com.qrmedia.pattern.compositeannotation.validation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
@CompositeAnnotation
public @interface InvalidCompositeAnnotationInconsistentTarget {

    // @PostConstruct is only applicable to methods, but the composite has the default target
    @LeafAnnotation
    PostConstruct postConstructLeafAnnotation() default @PostConstruct();
}
