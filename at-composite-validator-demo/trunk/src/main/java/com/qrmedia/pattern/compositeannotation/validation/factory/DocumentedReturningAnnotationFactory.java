/*
 * @(#)TargetReturningAnnotationFactory.java     7 Jun 2009
 */
package com.qrmedia.pattern.compositeannotation.validation.factory;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;

import com.qrmedia.commons.collections.MapUtils;
import com.qrmedia.commons.lang.annotation.factory.RuntimeConfiguredAnnotationFactory;
import com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory;
import com.qrmedia.pattern.compositeannotation.validation.InvalidLeafAnnotationInvalidCustomFactories;

/**
 * An annotation factory that returns a {@link Documented @Documented} annotation.
 * 
 * @author aphillips
 * @since 7 Jun 2009
 *
 */
public class DocumentedReturningAnnotationFactory 
        implements LeafAnnotationFactory<Documented, InvalidLeafAnnotationInvalidCustomFactories> {

    // required public, no-arg constructor
    public DocumentedReturningAnnotationFactory() {}
    
    /* (non-Javadoc)
     * @see com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory#newInstance(java.lang.annotation.Annotation)
     */
    public Documented newInstance(InvalidLeafAnnotationInvalidCustomFactories declaringCompositeAnnotation) {
        return RuntimeConfiguredAnnotationFactory.newInstance(Documented.class, 
                MapUtils.<String, Object>toMap("value", new ElementType[] { ElementType.METHOD }));
    }

}