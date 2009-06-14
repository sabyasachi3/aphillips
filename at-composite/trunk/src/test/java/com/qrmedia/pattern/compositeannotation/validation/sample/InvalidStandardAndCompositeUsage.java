/*
 * @(#)InvalidStandardAndCompositeUsage.java     5 Jun 2009
 */
package com.qrmedia.pattern.compositeannotation.validation.sample;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Generates a duplicate annotation type on an annotated element through
 * &quot;standard&quot; and composite annotations.
 * 
 * @author aphillips
 * @since 5 Jun 2009
 * 
 */
public class InvalidStandardAndCompositeUsage {

    // invalid as it leads to duplicate @Retention definitions
    @Retention(RetentionPolicy.CLASS)
    @ValidCompositeAnnotation
    static @interface StubAnnotation {}
}
