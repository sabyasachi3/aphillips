package com.qrmedia.pattern.compositeannotation.validation;


import java.lang.annotation.RetentionPolicy;

import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;

/**
 * A composite missing the {@link RetentionPolicy#RUNTIME RUNTIME} retention
 * policy.
 * 
 * @author aphillips
 * @since 30 May 2009
 * 
 */
@CompositeAnnotation
public @interface InvalidCompositeAnnotationInvalidRetention {}
