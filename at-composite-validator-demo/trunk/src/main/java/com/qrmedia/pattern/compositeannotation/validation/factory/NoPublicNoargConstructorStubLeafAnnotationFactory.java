package com.qrmedia.pattern.compositeannotation.validation.factory;


import java.lang.annotation.Target;

import com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory;
import com.qrmedia.pattern.compositeannotation.validation.InvalidLeafAnnotationInvalidCustomFactories;

/**
 * An invalid leaf annotation factory that does not contain the required public no-arg constructor.
 * 
 * @author aphillips
 * @since 6 Jun 2009
 * 
 */
public class NoPublicNoargConstructorStubLeafAnnotationFactory
        implements LeafAnnotationFactory<Target, InvalidLeafAnnotationInvalidCustomFactories> {

    public NoPublicNoargConstructorStubLeafAnnotationFactory(Object arg) {}

    NoPublicNoargConstructorStubLeafAnnotationFactory() {}

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory#newInstance
     * (java.lang.annotation.Annotation)
     */
    public Target newInstance(InvalidLeafAnnotationInvalidCustomFactories declaringCompositeAnnotation) {
        return null;
    }

}
