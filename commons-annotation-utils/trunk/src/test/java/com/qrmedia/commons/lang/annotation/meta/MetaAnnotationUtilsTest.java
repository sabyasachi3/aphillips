/*
 * @(#)MetaAnnotationUtilsTest.java     26 May 2009
 */
package com.qrmedia.commons.lang.annotation.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Unit tests for the {@link MetaAnnotationUtils}.
 * 
 * @author aphillips
 * @since 26 May 2009
 *
 */
public class MetaAnnotationUtilsTest {

    static @interface UnannotatedAnnotation {}
    
    @Test
    public void getTarget_null() {
        assertNull(MetaAnnotationUtils.getTarget(null));
    }
    
    /**
     * As per the documentation, the default value for {@link Target @Target} is &quot;all elements&quot;
     */
    @Test
    public void getTarget_unannotatedAnnotation() {
        assertEquals(asSet(ElementType.values()),
                     MetaAnnotationUtils.getTarget(UnannotatedAnnotation.class));
    }
    
    // can't use SetUtils in commons-lang...*sigh*...
    private static <E> Set<E> asSet(E... objs) {
        return new HashSet<E>(Arrays.asList(objs));
    }
    
    @Test
    public void getTarget() {
        assertEquals(asSet(ElementType.ANNOTATION_TYPE), 
                     MetaAnnotationUtils.getTarget(Target.class));
    }
    
    @Test
    public void getRetention_null() {
        assertNull(MetaAnnotationUtils.getRetention(null));
    }
    
    /**
     * As per the documentation, the default value for {@link Retention @Retention} is CLASS
     */
    @Test
    public void getRetention_unannotatedAnnotation() {
        assertEquals(RetentionPolicy.CLASS,
                     MetaAnnotationUtils.getRetention(UnannotatedAnnotation.class));
    }
    
    @Test
    public void getRetention() {
        assertEquals(RetentionPolicy.RUNTIME,
                MetaAnnotationUtils.getRetention(Retention.class));
    }
    
}
