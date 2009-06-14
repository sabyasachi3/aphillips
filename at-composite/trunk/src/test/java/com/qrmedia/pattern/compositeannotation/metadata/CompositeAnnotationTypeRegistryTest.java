/*
 * @(#)CompositeAnnotationTypeRegistryTest.java     30 May 2009
 */
package com.qrmedia.pattern.compositeannotation.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;

import com.qrmedia.commons.collections.SetUtils;

/**
 * Unit tests for the {@link CompositeAnnotationTypeRegistry}.
 * 
 * @author aphillips
 * @since 30 May 2009
 *
 */
public class CompositeAnnotationTypeRegistryTest {

    /**
     * The set returned should be a <em>copy</em> of the internal set.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void getCompositeAnnotationTypes_isCopy() {
        Set<Class<? extends Annotation>> compositeAnnotationTypes = 
            SetUtils.<Class<? extends Annotation>>asSet(Resource.class);
        Set<Class<? extends Annotation>> returnedCompositeAnnotationTypes = 
            new CompositeAnnotationTypeRegistry(compositeAnnotationTypes).getCompositeAnnotationTypes();
        assertNotSame(compositeAnnotationTypes, returnedCompositeAnnotationTypes);
        assertEquals(compositeAnnotationTypes, returnedCompositeAnnotationTypes);
    }
    
}
