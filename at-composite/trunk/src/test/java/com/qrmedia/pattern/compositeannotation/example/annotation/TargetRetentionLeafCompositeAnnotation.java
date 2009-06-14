/*
 * @(#)TargetRetentionLeafCompositeAnnotation.java     29 May 2009
 */
package com.qrmedia.pattern.compositeannotation.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;
import com.qrmedia.pattern.compositeannotation.example.factory.RetentionLeafAnnotationFactory;

/**
 * A composite annotation that contains {@link Target @Target} and {@link Retention @Retention}
 * annotations.
 * 
 * @author aphillips
 * @since 29 May 2009
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@CompositeAnnotation
public @interface TargetRetentionLeafCompositeAnnotation {
    boolean runtimeRetention() default false;
    
    @LeafAnnotation
    Target targetLeafAnnotation() default @Target({ ElementType.METHOD });
    
    // default will be ignored, the factory is always called
    @LeafAnnotation(factoryClass = RetentionLeafAnnotationFactory.class)
    Retention retentionLeafAnnotation() default @Retention(RetentionPolicy.RUNTIME);
}