/*
 * @(#)AtCompositeAnnotatedElementsTest.java     28 May 2009
 */
package com.qrmedia.pattern.compositeannotation;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import com.qrmedia.commons.collections.SetUtils;
import com.qrmedia.commons.reflect.ReflectionUtils;
import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;
import com.qrmedia.pattern.compositeannotation.metadata.CompositeAnnotationTypeRegistry;

/**
 * Unit tests for the {@link AtCompositeAnnotatedElements}.
 * 
 * @author aphillips
 * @since 28 May 2009
 *
 */
public class AtCompositeAnnotatedElementsTest {
    @SuppressWarnings("unchecked")
    private final AtCompositeAnnotatedElements annotatedElements =
        new AtCompositeAnnotatedElements(new CompositeAnnotationTypeRegistry(
                SetUtils.<Class<? extends Annotation>>asSet(EmptyStubCompositeAnnotation.class)));

    private final CompositeAnnotatedElements compositeAnnotatedElements = 
        createMock(CompositeAnnotatedElements.class);
    private final JavaAnnotatedElements javaAnnotatedElements = 
        createMock(JavaAnnotatedElements.class);

    private Method method;
    private final Class<Resource> annotationClass = Resource.class;
    private Resource resourceAnnotation;

    @Resource
    @Before
    public void prepareFixture() throws Exception {
        ReflectionUtils.setValue(annotatedElements, "compositeAnnotatedElements", compositeAnnotatedElements);
        ReflectionUtils.setValue(annotatedElements, "javaAnnotatedElements", javaAnnotatedElements);
        
        method = AtCompositeAnnotatedElementsTest.class.getMethod("prepareFixture");
        resourceAnnotation = getAnnotation("prepareFixture", Resource.class);
    }
    
    private static <T extends Annotation> T getAnnotation(String methodName, Class<T> annotationType) 
            throws Exception {
        return AtCompositeAnnotatedElementsTest.class.getMethod(methodName)
               .getAnnotation(annotationType);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void construct_nullCompositeAnnotationTypes() {
        new AtCompositeAnnotatedElements(null);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @CompositeAnnotation
    private static @interface InvalidLeafStubCompositeAnnotation {
        // invalid, since the annotated attribute does not return an annotation
        @LeafAnnotation 
        int nonAnnotationAttribute() default 0;
    }
    
    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void construct_invalidCompositeAnnotationTypes() {
        new AtCompositeAnnotatedElements(new CompositeAnnotationTypeRegistry(
                SetUtils.<Class<? extends Annotation>>asSet(InvalidLeafStubCompositeAnnotation.class)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isAnnotationPresent_nullElement() {
        annotatedElements.isAnnotationPresent(null, Resource.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isAnnotationPresent_nullAnnotationType() throws Exception {
        annotatedElements.isAnnotationPresent(method, null);
    }
    
    /**
     * Any {@code IllegalArgumentException IllegalArgumentExceptions} thrown by the composite
     * annotation service should be propagated. 
     */
    @Test(expected = IllegalArgumentException.class)
    public void isAnnotationPresent_invalidCompositeAnnotations() throws Exception {
        
        // ensure values for both standard and composite are retrieved
        expect(javaAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andReturn(true);
        expect(compositeAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andThrow(new IllegalArgumentException());
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        annotatedElements.isAnnotationPresent(method, annotationClass);
    }
    
    /**
     * If a composite has a leaf annotation of the same type as a &quot;standard&quot; annotation
     * on that element, an {@code IllegalArgumentException} should be thrown. 
     */
    @Test(expected = IllegalArgumentException.class)
    public void isAnnotationPresent_nonuniqueAnnotationTypes() throws Exception {
        
        // ensure values for both standard and composite are retrieved
        expect(javaAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andReturn(true);
        expect(compositeAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andReturn(true);
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        annotatedElements.isAnnotationPresent(method, annotationClass);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @CompositeAnnotation    
    static @interface EmptyStubCompositeAnnotation {}
    
    /**
     * Composite annotations should not be accessible, only their <em>leaf</em> annotations.
     */
    @Test
    public void isAnnotationPresent_hideComposites() throws Exception {
        
        // since composite annotations are "invisible", neither service should be called
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        assertFalse("Expected composite annotation to be hidden",
                    annotatedElements.isAnnotationPresent(method, EmptyStubCompositeAnnotation.class));
    }
    
    @Test
    public void isAnnotationPresent_fromCompositeAnnotation() throws Exception {
        
        // ensure values for both standard and composite are retrieved
        expect(javaAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andReturn(false);
        expect(compositeAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andReturn(true);
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        assertTrue("Expected annotation to be present",
                   annotatedElements.isAnnotationPresent(method, annotationClass));
    }
    
    @Test
    public void isAnnotationPresent_fromStandardAnnotation() throws Exception {
        
        // ensure values for both standard and composite are retrieved
        expect(javaAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andReturn(true);
        expect(compositeAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andReturn(false);
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        assertTrue("Expected annotation to be present",
                   annotatedElements.isAnnotationPresent(method, annotationClass));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAnnotation_nullElement() {
        annotatedElements.getAnnotation(null, Resource.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getAnnotation_nullAnnotationType() throws Exception {
        annotatedElements.getAnnotation(method, null);
    }
    
    /**
     * Any {@code IllegalArgumentException IllegalArgumentExceptions} thrown by the composite
     * annotation service should be propagated. 
     */
    @Test(expected = IllegalArgumentException.class)
    public void getAnnotation_invalidCompositeAnnotations() throws Exception {
        
        // how the services are called is an implementation detail
        expect(javaAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andStubReturn(true);
        expect(javaAnnotatedElements.getAnnotation(method, annotationClass))
        .andStubReturn(resourceAnnotation);
        
        // how compositeAnnotatedElements is called is an implementation detail
        expect(compositeAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andStubThrow(new IllegalArgumentException());
        expect(compositeAnnotatedElements.getAnnotation(method, annotationClass))
        .andStubThrow(new IllegalArgumentException());
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        annotatedElements.getAnnotation(method, annotationClass);
    }
    
    /**
     * If a composite has a leaf annotation of the same type as a &quot;standard&quot; annotation
     * on that element, an {@code IllegalArgumentException} should be thrown. 
     */
    @Resource
    @Test(expected = IllegalArgumentException.class)
    public void getAnnotation_nonuniqueAnnotationTypes() throws Exception {
        
        // how the services are called is an implementation detail
        expect(javaAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andStubReturn(true);
        expect(javaAnnotatedElements.getAnnotation(method, annotationClass))
        .andStubReturn(resourceAnnotation);
        
        expect(compositeAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andStubReturn(true);
        expect(compositeAnnotatedElements.getAnnotation(method, annotationClass))
        .andStubReturn(getAnnotation("getAnnotation_nonuniqueAnnotationTypes", Resource.class));
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        annotatedElements.getAnnotation(method, annotationClass);
    }

    /**
     * Composite annotations should not be accessible, only their <em>leaf</em> annotations.
     */
    @Test
    public void getAnnotation_hideComposites() throws Exception {
        
        // since composite annotations are "invisible", neither service should be called
        replay(javaAnnotatedElements, compositeAnnotatedElements);

        assertNull("Expected composite annotation to be hidden",
                   annotatedElements.getAnnotation(method, EmptyStubCompositeAnnotation.class));
    }
    
    @Test
    public void getAnnotation_fromCompositeAnnotation() throws Exception {

        // how javaAnnotatedElements is called is an implementation detail
        expect(javaAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andStubReturn(false);
        expect(javaAnnotatedElements.getAnnotation(method, annotationClass))
        .andStubReturn(null);
        
        expect(compositeAnnotatedElements.getAnnotation(method, annotationClass))
        .andReturn(resourceAnnotation);
        replay(javaAnnotatedElements, compositeAnnotatedElements);

        assertSame(resourceAnnotation, annotatedElements.getAnnotation(method, annotationClass));
    }
    
    @Test
    public void getAnnotation_fromStandardAnnotation() throws Exception {
        expect(javaAnnotatedElements.getAnnotation(method, annotationClass))
        .andReturn(resourceAnnotation);
        
        // how compositeAnnotatedElements is called is an implementation detail
        expect(compositeAnnotatedElements.isAnnotationPresent(method, annotationClass))
        .andStubReturn(false);
        expect(compositeAnnotatedElements.getAnnotation(method, annotationClass))
        .andStubReturn(null);
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        assertSame(resourceAnnotation, annotatedElements.getAnnotation(method, annotationClass));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getAnnotations_nullElement() {
        annotatedElements.getAnnotations(null);
    }
    
    /**
     * Any {@code IllegalArgumentException IllegalArgumentExceptions} thrown by the composite
     * annotation service should be propagated. 
     */
    @Test(expected = IllegalArgumentException.class)
    public void getAnnotations_invalidCompositeAnnotations() throws Exception {
        expect(javaAnnotatedElements.getAnnotations(method))
        .andReturn(new Annotation[] { resourceAnnotation });
        expect(compositeAnnotatedElements.getAnnotations(method))
        .andThrow(new IllegalArgumentException());
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        annotatedElements.getAnnotations(method);
    }
    
    /**
     * If a composite has a leaf annotation of the same type as a &quot;standard&quot; annotation
     * on that element, an {@code IllegalArgumentException} should be thrown. 
     */
    @Resource
    @Test(expected = IllegalArgumentException.class)
    public void getAnnotations_nonuniqueAnnotationTypes() throws Exception {
        expect(javaAnnotatedElements.getAnnotations(method))
        .andReturn(new Annotation[] { resourceAnnotation });
        expect(compositeAnnotatedElements.getAnnotations(method))
        .andReturn(new Annotation[] { 
                getAnnotation("getAnnotations_nonuniqueAnnotationTypes", Resource.class) });
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        annotatedElements.getAnnotations(method);
    }

    /**
     * Composite annotations returned by the &quot;standard&quot; service should be hidden.
     */
    @EmptyStubCompositeAnnotation
    @PostConstruct
    @Test
    public void getAnnotations() throws Exception {
        // the standard service will return composite annotations!
        expect(javaAnnotatedElements.getAnnotations(method))
        .andReturn(new Annotation[] { resourceAnnotation,
                getAnnotation("getAnnotations", EmptyStubCompositeAnnotation.class)});
        
        PostConstruct postConstructAnnotation = getAnnotation("getAnnotations", PostConstruct.class);
        expect(compositeAnnotatedElements.getAnnotations(method))
        .andReturn(new Annotation[] { postConstructAnnotation });
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        assertTrue("Returned annotations not as expected", 
                CollectionUtils.isEqualCollection(
                    Arrays.asList(resourceAnnotation, postConstructAnnotation),
                    Arrays.asList(annotatedElements.getAnnotations(method))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDeclaredAnnotations_nullElement() {
        annotatedElements.getDeclaredAnnotations(null);
    }
    
    /**
     * Any {@code IllegalArgumentException IllegalArgumentExceptions} thrown by the composite
     * annotation service should be propagated. 
     */
    @Test(expected = IllegalArgumentException.class)
    public void getDeclaredAnnotations_invalidCompositeAnnotations() throws Exception {
        expect(javaAnnotatedElements.getDeclaredAnnotations(method))
        .andReturn(new Annotation[] { resourceAnnotation });
        expect(compositeAnnotatedElements.getDeclaredAnnotations(method))
        .andThrow(new IllegalArgumentException());
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        annotatedElements.getDeclaredAnnotations(method);
    }
    
    /**
     * If a composite has a leaf annotation of the same type as a &quot;standard&quot; annotation
     * on that element, an {@code IllegalArgumentException} should be thrown. 
     */
    @Resource
    @Test(expected = IllegalArgumentException.class)
    public void getDeclaredAnnotations_nonuniqueAnnotationTypes() throws Exception {
        expect(javaAnnotatedElements.getDeclaredAnnotations(method))
        .andReturn(new Annotation[] { resourceAnnotation });
        expect(compositeAnnotatedElements.getDeclaredAnnotations(method))
        .andReturn(new Annotation[] { 
                getAnnotation("getAnnotations_nonuniqueAnnotationTypes", Resource.class) });
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        annotatedElements.getDeclaredAnnotations(method);
    }

    /**
     * Composite annotations returned by the &quot;standard&quot; service should be hidden.
     */
    @EmptyStubCompositeAnnotation
    @PostConstruct
    @Test
    public void getDeclaredAnnotations() throws Exception {
        // the standard service will return composite annotations!
        expect(javaAnnotatedElements.getDeclaredAnnotations(method))
        .andReturn(new Annotation[] { resourceAnnotation,
                getAnnotation("getAnnotations", EmptyStubCompositeAnnotation.class)});
        
        PostConstruct postConstructAnnotation = getAnnotation("getAnnotations", PostConstruct.class);
        expect(compositeAnnotatedElements.getDeclaredAnnotations(method))
        .andReturn(new Annotation[] { postConstructAnnotation });
        replay(javaAnnotatedElements, compositeAnnotatedElements);
        
        assertTrue("Returned annotations not as expected", 
                CollectionUtils.isEqualCollection(
                    Arrays.asList(resourceAnnotation, postConstructAnnotation),
                    Arrays.asList(annotatedElements.getDeclaredAnnotations(method))));
    }
    
}
