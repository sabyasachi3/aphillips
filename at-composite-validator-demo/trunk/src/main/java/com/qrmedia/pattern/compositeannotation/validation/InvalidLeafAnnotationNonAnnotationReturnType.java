package com.qrmedia.pattern.compositeannotation.validation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;

/**
 * A composite annotation with an invalid leaf annotation which does not return
 * an annotation.
 * 
 * @author aphillips
 * @since 30 May 2009
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@CompositeAnnotation
public @interface InvalidLeafAnnotationNonAnnotationReturnType {

    // invalid as leaf members must return an annotation
    @LeafAnnotation
    int invalidLeafAnnotationMember() default 0;
}
