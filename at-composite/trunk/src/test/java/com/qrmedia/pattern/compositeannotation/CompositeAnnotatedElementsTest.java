/*
 * @(#)CompositeAnnotatedElements.java     28 May 2009
 */
package com.qrmedia.pattern.compositeannotation;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import com.qrmedia.commons.collections.SetUtils;
import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;
import com.qrmedia.pattern.compositeannotation.metadata.CompositeAnnotationTypeRegistry;

/**
 * Unit tests for the {@code CompositeAnnotatedElements}.
 * 
 * @author aphillips
 * @since 28 May 2009
 *
 */
public class CompositeAnnotatedElementsTest {
    @SuppressWarnings("unchecked")
    private final CompositeAnnotatedElements annotatedElements = 
        new CompositeAnnotatedElements(new CompositeAnnotationTypeRegistry(SetUtils.asSet(
                TargetRetentionLeafStubCompositeAnnotation.class,
                RetentionLeafStubCompositeAnnotation.class, ResourceStubCompositeAnnotation.class,
                EmptyStubCompositeAnnotation.class, OtherResourceStubCompositeAnnotation.class)));
    
    @Test(expected = IllegalArgumentException.class)
    public void construct_null() {
        new CompositeAnnotatedElements(null);
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @CompositeAnnotation
    private static @interface InvalidLeafStubCompositeAnnotation {
        // invalid, since the annotated attribute does not return an annotation
        @LeafAnnotation 
        int nonAnnotationAttribute() default 0;
    }
    
    @SuppressWarnings("unchecked")
    public void construct_invalidCompositeType() {
        new CompositeAnnotatedElements(new CompositeAnnotationTypeRegistry(
                SetUtils.<Class<? extends Annotation>>asSet(InvalidLeafStubCompositeAnnotation.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isAnnotationPresent_nullElement() {
        annotatedElements.isAnnotationPresent(null, Resource.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isAnnotationPresent_nullAnnotationType() throws Exception {
        annotatedElements.isAnnotationPresent(getMethod("isAnnotationPresent_nullAnnotationType"), null);
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @CompositeAnnotation
    static @interface TargetRetentionLeafStubCompositeAnnotation {
        @LeafAnnotation
        Target targetLeafAnnotation() default @Target({ ElementType.METHOD });
        
        @LeafAnnotation
        Retention retentionLeafAnnotation() default @Retention(RetentionPolicy.RUNTIME);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @CompositeAnnotation
    static @interface RetentionLeafStubCompositeAnnotation {
        @LeafAnnotation
        Retention retentionLeafAnnotation() default @Retention(RetentionPolicy.CLASS);
    }

    // having both composites together is actually invalid as they both have a @Retention leaf
    @TargetRetentionLeafStubCompositeAnnotation
    @RetentionLeafStubCompositeAnnotation
    private static @interface AnnotatedAnnotation {}
    
    /**
     * Multiple composites on an element may not declare leaf annotations of the same type. 
     */
    @Test(expected = IllegalArgumentException.class)
    public void isAnnotationPresent_nonuniqueLeafAnnotationTypes() throws Exception {
        annotatedElements.isAnnotationPresent(AnnotatedAnnotation.class, Retention.class);
    }
    
    private static Method getMethod(String methodName) throws Exception {
        return CompositeAnnotatedElementsTest.class.getMethod(methodName);
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
    @Inherited
    @CompositeAnnotation    
    static @interface ResourceStubCompositeAnnotation {
        @LeafAnnotation
        Resource resourceLeafAnnotation() default @Resource;

        @LeafAnnotation
        StubResourceAnnotation stubResourceLeafAnnotation() default @StubResourceAnnotation;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
    private static @interface StubResourceAnnotation {}

    @ResourceStubCompositeAnnotation
    private static class ParentAnnotatedObject {}

    private static class ChildAnnotatedObject extends ParentAnnotatedObject {}

    @Test
    public void isAnnotationPresent_inherited() throws Exception {
        assertTrue("Annotation should be inherited",
                   annotatedElements.isAnnotationPresent(ChildAnnotatedObject.class, 
                                                         Resource.class));
    }

    @Test
    public void isAnnotationPresent_direct() throws Exception {
        // asking for Retention would cause an IllegalAnnotationException
        assertTrue("Annotation should be present",
                   annotatedElements.isAnnotationPresent(AnnotatedAnnotation.class, Target.class)); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAnnotation_nullElement() {
        annotatedElements.getAnnotation(null, Resource.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAnnotation_nullAnnotationType() throws Exception {
        annotatedElements.getAnnotation(getMethod("getAnnotation_nullAnnotationType"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAnnotation_nonuniqueLeafAnnotationTypes() throws Exception {
        annotatedElements.getAnnotation(AnnotatedAnnotation.class, Retention.class);
    }
    
    @Test
    public void getAnnotation_inherited() throws Exception {
        Class<ChildAnnotatedObject> clazz = ChildAnnotatedObject.class;
        assertSame(clazz.getAnnotation(ResourceStubCompositeAnnotation.class).resourceLeafAnnotation(), 
                   annotatedElements.getAnnotation(clazz, Resource.class));
    }

    @Test
    public void getAnnotation_direct() throws Exception {
        Class<AnnotatedAnnotation> clazz = AnnotatedAnnotation.class;
        assertSame(clazz.getAnnotation(TargetRetentionLeafStubCompositeAnnotation.class)
                   .targetLeafAnnotation(),
                   annotatedElements.getAnnotation(clazz, Target.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAnnotations_nullElement() {
        annotatedElements.getAnnotations(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getAnnotations_nonuniqueLeafAnnotationTypes() throws Exception {
        annotatedElements.getAnnotations(AnnotatedAnnotation.class);
    }    

    @Retention(RetentionPolicy.RUNTIME)
    @CompositeAnnotation    
    static @interface EmptyStubCompositeAnnotation {}
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
    @Inherited
    @CompositeAnnotation    
    static @interface OtherResourceStubCompositeAnnotation {
        @LeafAnnotation
        OtherStubResourceAnnotation otherStubResourceAnnotation() default @OtherStubResourceAnnotation;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
    private static @interface OtherStubResourceAnnotation {}
    
    @OtherResourceStubCompositeAnnotation
    private static class GrandChildAnnotatedObject extends ChildAnnotatedObject {}
    
    /**
     * Both direct and inherited leaf annotations should be included.
     */
    @Test
    public void getAnnotations() throws Exception {
        Class<GrandChildAnnotatedObject> clazz = GrandChildAnnotatedObject.class;
        assertTrue("Returned annotations not as expected", 
                CollectionUtils.isEqualCollection(
                    Arrays.asList(clazz.getAnnotation(OtherResourceStubCompositeAnnotation.class)
                                  .otherStubResourceAnnotation(),
                                  clazz.getAnnotation(ResourceStubCompositeAnnotation.class)
                                  .resourceLeafAnnotation(),
                                  clazz.getAnnotation(ResourceStubCompositeAnnotation.class)
                                  .stubResourceLeafAnnotation()), 
                    Arrays.asList(annotatedElements.getAnnotations(clazz))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDeclaredAnnotations_nullElement() {
        annotatedElements.getDeclaredAnnotations(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getDeclaredAnnotations_nonuniqueLeafAnnotationTypes() throws Exception {
        annotatedElements.getDeclaredAnnotations(AnnotatedAnnotation.class);
    }    

    /**
     * Only direct leaf annotations should be included.
     */
    @Test
    public void getDeclaredAnnotations() throws Exception {
        Class<GrandChildAnnotatedObject> clazz = GrandChildAnnotatedObject.class;
        assertTrue("Returned annotations not as expected", 
                CollectionUtils.isEqualCollection(
                    Arrays.asList(clazz.getAnnotation(OtherResourceStubCompositeAnnotation.class)
                                  .otherStubResourceAnnotation()), 
                    Arrays.asList(annotatedElements.getDeclaredAnnotations(clazz))));
    }
    
}
