/*
 * @(#)JavaAnnotatedElementsTest.java     13 May 2009
 */
package com.qrmedia.pattern.compositeannotation;

import static org.junit.Assert.*;

import java.beans.ConstructorProperties;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.junit.Test;

import com.qrmedia.pattern.compositeannotation.JavaAnnotatedElementsTest.StubAnnotatedObject.AnnotatedAnnotation;


/**
 * Unit tests for the {@link JavaAnnotatedElements}.
 * 
 * @author aphillips
 * @since 13 May 2009
 * 
 */
public class JavaAnnotatedElementsTest {
    private final JavaAnnotatedElements annotatedElements = new JavaAnnotatedElements();

    static class StubAnnotatedObject {

        @Retention(RetentionPolicy.RUNTIME)
        @Inherited
        static @interface AnnotatedAnnotation {}

        @ConstructorProperties({})
        public StubAnnotatedObject() {}

        public void unannotatedMethod() {}
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isAnnotationPresent_nullElement() {
        annotatedElements.isAnnotationPresent(null, AnnotatedAnnotation.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isAnnotationPresent_nullAnnotationType() {
        annotatedElements.isAnnotationPresent(StubAnnotatedObject.class, null);
    }

    @Test
    public void isAnnotationPresent_missing() {
        assertFalse(annotatedElements.isAnnotationPresent(StubAnnotatedObject.class, 
                                                          PostConstruct.class));
    }

    @AnnotatedAnnotation
    private static class ParentAnnotatedObject {}

    @Resource
    private static class ChildAnnotatedObject extends ParentAnnotatedObject {}

    @Test
    public void isAnnotationPresent_inherited() throws Exception {
        assertTrue(annotatedElements.isAnnotationPresent(ChildAnnotatedObject.class, 
                                                         AnnotatedAnnotation.class));
    }

    @Test
    public void isAnnotationPresent_direct() throws Exception {
        assertTrue(annotatedElements.isAnnotationPresent(ParentAnnotatedObject.class, 
                                                         AnnotatedAnnotation.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAnnotation_nullElement() {
        annotatedElements.getAnnotation(null, AnnotatedAnnotation.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAnnotation_nullAnnotationType() {
        annotatedElements.getAnnotation(StubAnnotatedObject.class, null);
    }

    @Test
    public void getAnnotation_missing() {
        assertNull(annotatedElements.getAnnotation(AnnotatedAnnotation.class, Documented.class));
    }

    @Test
    public void getAnnotation_inherited() throws Exception {
        Class<ChildAnnotatedObject> clazz = ChildAnnotatedObject.class;
        assertSame(clazz.getAnnotation(AnnotatedAnnotation.class), 
                   annotatedElements.getAnnotation(clazz, AnnotatedAnnotation.class));
    }

    @Test
    public void getAnnotation_direct() throws Exception {
        Constructor<StubAnnotatedObject> constructor = 
            StubAnnotatedObject.class.getConstructor();
        assertSame(constructor.getAnnotation(ConstructorProperties.class),
                   annotatedElements.getAnnotation(constructor, ConstructorProperties.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAnnotations_nullElement() {
        annotatedElements.getAnnotations(null);
    }

    @Test
    public void getAnnotations_none() throws Exception {
        assertEquals(0, annotatedElements.getAnnotations(
                            StubAnnotatedObject.class.getMethod("unannotatedMethod"))
                        .length);
    }

    @Test
    public void getAnnotations() throws Exception {
        Class<ChildAnnotatedObject> clazz = ChildAnnotatedObject.class;
        assertArrayEquals(clazz.getAnnotations(), annotatedElements.getAnnotations(clazz));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDeclaredAnnotations_nullElement() {
        annotatedElements.getDeclaredAnnotations(null);
    }

    @Test
    public void getDeclaredAnnotations_none() throws Exception {
        assertEquals(0, annotatedElements.getDeclaredAnnotations(
                            StubAnnotatedObject.class.getMethod("unannotatedMethod"))
                        .length);
    }

    @Test
    public void getDeclaredAnnotations() throws Exception {
        Class<ChildAnnotatedObject> clazz = ChildAnnotatedObject.class;
        assertArrayEquals(clazz.getDeclaredAnnotations(), 
                          annotatedElements.getDeclaredAnnotations(clazz));
    }
    
}