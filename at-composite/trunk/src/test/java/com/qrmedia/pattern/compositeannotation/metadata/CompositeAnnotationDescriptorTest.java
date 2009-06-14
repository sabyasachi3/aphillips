/*
 * @(#)CompositeAnnotationDescriptorTest.java     26 May 2009
 */
package com.qrmedia.pattern.compositeannotation.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Resource;

import org.junit.Test;

import com.qrmedia.commons.collections.SetUtils;
import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;

/**
 * Unit tests for the {@link CompositeAnnotationDescriptor}.
 * 
 * @author aphillips
 * @since 26 May 2009
 *
 */
public class CompositeAnnotationDescriptorTest {

    @CompositeAnnotation
    private static @interface NoRententionStubCompositeAnnotation {}
    
    @Test(expected = IllegalArgumentException.class)
    public void construct_noRetention() {
        new CompositeAnnotationDescriptor<NoRententionStubCompositeAnnotation>(
                NoRententionStubCompositeAnnotation.class);
    }

    @Retention(RetentionPolicy.CLASS)
    @CompositeAnnotation
    private static @interface NonRuntimeRententionStubCompositeAnnotation {}
    
    @Test(expected = IllegalArgumentException.class)
    public void construct_nonRuntimeRetention() {
        new CompositeAnnotationDescriptor<NonRuntimeRententionStubCompositeAnnotation>(
                NonRuntimeRententionStubCompositeAnnotation.class);
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @CompositeAnnotation
    private static @interface InvalidLeafStubCompositeAnnotation {
        // invalid, since the annotated attribute does not return an annotation
        @LeafAnnotation 
        int nonAnnotationAttribute() default 0;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void construct_invalidLeafAnnotation() {
        new CompositeAnnotationDescriptor<InvalidLeafStubCompositeAnnotation>(
                InvalidLeafStubCompositeAnnotation.class);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @CompositeAnnotation
    private static @interface DuplicateLeafAnnotationTypesStubCompositeAnnotation {
        @LeafAnnotation
        Target leafAnnotationAttribute() default @Target({ ElementType.FIELD });

        // invalid, since it also returns a @Target
        @LeafAnnotation
        Target otherLeafAnnotationAttribute() default @Target({ ElementType.METHOD});
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void construct_nonuniqueLeafAnnotationTypes() {
        new CompositeAnnotationDescriptor<DuplicateLeafAnnotationTypesStubCompositeAnnotation>(
                DuplicateLeafAnnotationTypesStubCompositeAnnotation.class);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @CompositeAnnotation
    private static @interface DifferentTargetStubCompositeAnnotation {
        
        // invalid, since Target has target ANNOTATION_TYPE and the composite has METHOD
        @LeafAnnotation
        Target leafAnnotationAttribute() default @Target({ ElementType.FIELD });
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void construct_inconsistentTarget() {
        new CompositeAnnotationDescriptor<DifferentTargetStubCompositeAnnotation>(
                DifferentTargetStubCompositeAnnotation.class);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @CompositeAnnotation
    private static @interface StubCompositeAnnotation {
        
        @LeafAnnotation
        Target leafAnnotationAttribute() default @Target({ ElementType.FIELD });
        
        @LeafAnnotation
        Retention otherLeafAnnotationAttribute() default @Retention(RetentionPolicy.SOURCE);
    }

    @Test
    public void getTarget() {
        assertEquals(SetUtils.asSet(ElementType.ANNOTATION_TYPE),
                     new CompositeAnnotationDescriptor<StubCompositeAnnotation>(
                             StubCompositeAnnotation.class)
                     .getTarget());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getLeafAnnotationTypes() {
        assertEquals(SetUtils.asSet(Target.class, Retention.class),
                     new CompositeAnnotationDescriptor(StubCompositeAnnotation.class)
                     .getLeafAnnotationTypes());
    }
    
    @StubCompositeAnnotation
    private static @interface StubCompositeAnnotatedAnnotation {} 

    @InvalidLeafStubCompositeAnnotation
    private static @interface OtherCompositeAnnotatedAnnotation {} 
    
    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void getLeafAnnotation_wrongCompositeType() {
        
        // would cause a compile-time error with generics
        new CompositeAnnotationDescriptor(StubCompositeAnnotation.class).getLeafAnnotation(
                OtherCompositeAnnotatedAnnotation.class.getAnnotation(
                        InvalidLeafStubCompositeAnnotation.class), 
                Resource.class);
    }
    
    @Test
    public void getLeafAnnotation_absentLeafAnnotationType() {
        assertNull(new CompositeAnnotationDescriptor<StubCompositeAnnotation>(StubCompositeAnnotation.class)
                   .getLeafAnnotation(StubCompositeAnnotatedAnnotation.class.getAnnotation(
                                           StubCompositeAnnotation.class), 
                                      Resource.class));
    }

    @Test
    public void getLeafAnnotation() {
        StubCompositeAnnotation compositeAnnotation = 
            StubCompositeAnnotatedAnnotation.class.getAnnotation(StubCompositeAnnotation.class);
        assertEquals(compositeAnnotation.leafAnnotationAttribute(),
                     new CompositeAnnotationDescriptor<StubCompositeAnnotation>(StubCompositeAnnotation.class)
                     .getLeafAnnotation(compositeAnnotation, Target.class));
    }
    
    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void getLeafAnnotations_wrongCompositeType() {
        
        // would cause a compile-time error with generics        
        new CompositeAnnotationDescriptor(StubCompositeAnnotation.class).getLeafAnnotations(
                OtherCompositeAnnotatedAnnotation.class.getAnnotation(
                        InvalidLeafStubCompositeAnnotation.class));
    }
    
    @Test
    public void getLeafAnnotations() {
        StubCompositeAnnotation compositeAnnotation = 
            StubCompositeAnnotatedAnnotation.class.getAnnotation(StubCompositeAnnotation.class);
        assertEquals(SetUtils.asSet(compositeAnnotation.leafAnnotationAttribute(),
                                    compositeAnnotation.otherLeafAnnotationAttribute()),
                     new CompositeAnnotationDescriptor<StubCompositeAnnotation>(StubCompositeAnnotation.class)
                     .getLeafAnnotations(compositeAnnotation));
    }
    
}