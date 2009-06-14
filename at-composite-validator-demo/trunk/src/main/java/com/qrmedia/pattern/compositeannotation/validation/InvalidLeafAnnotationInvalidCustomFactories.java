/*
 * @(#)InvalidLeafAnnotationInvalidCustomFactoryies.java     30 May 2009
 */
package com.qrmedia.pattern.compositeannotation.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;
import com.qrmedia.pattern.compositeannotation.validation.factory.DocumentedReturningAnnotationFactory;
import com.qrmedia.pattern.compositeannotation.validation.factory.NoPublicNoargConstructorStubLeafAnnotationFactory;

/**
 * A composite annotation with an invalid custom factory.
 * 
 * @author aphillips
 * @since 30 May 2009
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@CompositeAnnotation
public @interface InvalidLeafAnnotationInvalidCustomFactories {

    // invalid as factories must declare a public no-arg constructor
    @LeafAnnotation(factoryClass = NoPublicNoargConstructorStubLeafAnnotationFactory.class)
    Target invalidLeafAnnotationMember() default @Target({});
    
    // invalid, as the factory returns a @Documented annotation
    @LeafAnnotation(factoryClass = DocumentedReturningAnnotationFactory.class)
    Retention otherInvalidLeafAnnotationMember() default @Retention(RetentionPolicy.RUNTIME);
}
