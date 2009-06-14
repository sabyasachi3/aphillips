/*
 * @(#)InvalidCompositeAnnotationInvalidRetention.java     30 May 2009
 */
package com.qrmedia.pattern.compositeannotation.validation.sample;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;

/**
 * A composite missing the {@link RetentionPolicy#RUNTIME RUNTIME} retention
 * policy.
 * 
 * @author aphillips
 * @since 30 May 2009
 * 
 */
@Target(ElementType.ANNOTATION_TYPE)
@CompositeAnnotation
public @interface InvalidCompositeAnnotationInvalidRetention {}
