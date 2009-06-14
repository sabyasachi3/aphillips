/*
 * @(#)RetentionLeafAnnotationFactory.java     29 May 2009
 */
package com.qrmedia.pattern.compositeannotation.example.factory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.qrmedia.commons.collections.MapUtils;
import com.qrmedia.commons.lang.annotation.factory.RuntimeConfiguredAnnotationFactory;
import com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory;
import com.qrmedia.pattern.compositeannotation.example.annotation.TargetRetentionLeafCompositeAnnotation;

/**
 * Generates a {@link Retention @Rentention} leaf annotation for the 
 * {@link TargetRetentionLeafStubCompositeAnnotation @TargetRetentionLeafStubCompositeAnnotation}
 * composite based on a runtime value of a composite member.
 * 
 * @author aphillips
 * @since 29 May 2009
 *
 */
public class RetentionLeafAnnotationFactory 
        implements LeafAnnotationFactory<Retention, TargetRetentionLeafCompositeAnnotation> {

    /* (non-Javadoc)
     * @see com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory#newInstance(java.lang.annotation.Annotation)
     */
    public Retention newInstance(
            TargetRetentionLeafCompositeAnnotation declaringCompositeAnnotation) {
        return RuntimeConfiguredAnnotationFactory.newInstance(Retention.class, 
                MapUtils.<String, Object>toMap("value", 
                        declaringCompositeAnnotation.runtimeRetention() ? RetentionPolicy.RUNTIME
                                                                        : RetentionPolicy.CLASS));
    }

}
