package com.qrmedia.pattern.compositeannotation.validation;


import java.lang.annotation.Target;

import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;

/**
 * A test case that annotates a non-composite member method with
 * {@link LeafAnnotation @LeafAnnotation}.
 * 
 * @author aphillips
 * @since 5 Jun 2009
 * 
 */
public class InvalidLeafAnnotationUsage {

    // invalid as not on a member of a @CompositeAnnotation
    @LeafAnnotation
    public Target stubMethod() { 
        return null;
    }
    
}
