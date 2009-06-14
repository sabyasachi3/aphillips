package com.qrmedia.pattern.compositeannotation.validation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;

/**
 * A valid composite annotation.
 * 
 * @author aphillips
 * @since 30 May 2009
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@CompositeAnnotation
public @interface ValidCompositeAnnotation {
    @LeafAnnotation
    Target targetLeafAnnotation() default @Target({});

    @LeafAnnotation
    Retention retentionLeafAnnotation() default @Retention(RetentionPolicy.SOURCE);
}
