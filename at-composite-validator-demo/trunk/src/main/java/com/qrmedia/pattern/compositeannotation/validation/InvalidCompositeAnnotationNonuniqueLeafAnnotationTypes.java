package com.qrmedia.pattern.compositeannotation.validation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;

/**
 * A composite with non-unique leaf annotation return types.
 * 
 * @author aphillips
 * @since 30 May 2009
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@CompositeAnnotation
public @interface InvalidCompositeAnnotationNonuniqueLeafAnnotationTypes {

    @LeafAnnotation
    Target targetLeafAnnotation() default @Target({ ElementType.METHOD });

    @LeafAnnotation
    Target otherTargetLeafAnnotation() default @Target({ ElementType.FIELD });
}
