/*
 * @(#)LeafAnnotationDescriptorTest.java     23 May 2009
 */
package com.qrmedia.pattern.compositeannotation.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.junit.Test;

import com.qrmedia.commons.collections.MapUtils;
import com.qrmedia.commons.collections.SetUtils;
import com.qrmedia.commons.lang.annotation.factory.RuntimeConfiguredAnnotationFactory;
import com.qrmedia.commons.reflect.ReflectionUtils;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;
import com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory;


/**
 * Unit tests for the {@link LeafAnnotationDescriptor}.
 * 
 * @author aphillips
 * @since 23 May 2009
 *
 */
public class LeafAnnotationDescriptorTest {

    static @interface StubLeafMembersAnnotation {

        @LeafAnnotation
        Target leafAnnotationAttribute() default @Target({ ElementType.FIELD });

        @LeafAnnotation
        NoTargetSpecifyingStubAnnotation noTargetSpecifyingLeafAnnotationAttribute() 
            default @NoTargetSpecifyingStubAnnotation;

        @LeafAnnotation(factoryClass = StubLeafAnnotationFactory.class)
        Target customFactoryLeafAnnotationAttribute() default @Target({ ElementType.FIELD });
        
        // invalid, since the annotated attribute does not return an annotation
        @LeafAnnotation 
        int nonAnnotationAttribute() default 0;

        // invalid, since the leaf annotation type of the factory is @Target
        @LeafAnnotation(factoryClass = StubLeafAnnotationFactory.class)
        Resource wrongLeafAnnotationTypeCustomFactoryAttribute() default @Resource;    

        // invalid, since the custom factory does not have a public, no-arg constructor
        @LeafAnnotation(factoryClass = NoPublicNoargConstructorStubLeafAnnotationFactory.class)
        Target invalidConstructorCustomFactoryAttribute() default @Target({ ElementType.FIELD });    

        // invalid, since the leaf annotation does not have runtime retention
        @LeafAnnotation
        NoRuntimeRetentionStubAnnotation noRuntimeRetentionLeafAnnotationAttribute() 
            default @NoRuntimeRetentionStubAnnotation;
        
        // invalid, since the leaf annotation does not have runtime retention
        @LeafAnnotation
        NoRetentionStubAnnotation noRetentionLeafAnnotationAttribute() 
            default @NoRetentionStubAnnotation;        
    }

    static @interface NoRetentionStubAnnotation {}

    @Retention(RetentionPolicy.CLASS)
    static @interface NoRuntimeRetentionStubAnnotation {}
    
    @Retention(RetentionPolicy.RUNTIME)
    static @interface NoTargetSpecifyingStubAnnotation {}
    
    private static class NoPublicNoargConstructorStubLeafAnnotationFactory 
        implements LeafAnnotationFactory<Target, StubLeafMembersAnnotation> {

        public NoPublicNoargConstructorStubLeafAnnotationFactory(Object arg) {}
        
        NoPublicNoargConstructorStubLeafAnnotationFactory() {}

        /* (non-Javadoc)
         * @see com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory#newInstance(java.lang.annotation.Annotation)
         */
        public Target newInstance(StubLeafMembersAnnotation declaringCompositeAnnotation) {
            return null;
        }
        
    }
    
    private static class StubLeafAnnotationFactory 
        implements LeafAnnotationFactory<Target, StubLeafMembersAnnotation> {

        // required public, no-arg constructor
        public StubLeafAnnotationFactory() {}
        
        /* (non-Javadoc)
         * @see com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory#newInstance(java.lang.annotation.Annotation)
         */
        public Target newInstance(StubLeafMembersAnnotation declaringCompositeAnnotation) {
            return RuntimeConfiguredAnnotationFactory.newInstance(Target.class, 
                    MapUtils.<String, Object>toMap("value", new ElementType[] { ElementType.METHOD }));
        }

    }
    
    /**
     * {@link LeafAnnotation @LeafAnnotation} may only be used on members that return an
     * annotation. 
     */
    @Test(expected = IllegalArgumentException.class)
    public void construct_invalidLeafAnnotation_nonAnnotationType() throws Exception {
        new LeafAnnotationDescriptor<Annotation, Annotation>(getMember("nonAnnotationAttribute"));
    }
    
    private static Method getMember(String memberName) throws Exception {
        return StubLeafMembersAnnotation.class.getDeclaredMethod(memberName);
    }
    
    /**
     * Ensures that an error is thrown if the annotation type returned by a custom factory
     * does not match the type of the annotated member.
     */
    @Test(expected = IllegalArgumentException.class)
    public void construct_invalidCustomFactory_wrongLeafAnnotationType() throws Exception {
        new LeafAnnotationDescriptor<Resource, Annotation>(
                getMember("wrongLeafAnnotationTypeCustomFactoryAttribute"));
    }

    /**
     * Custom factories must declare a public, no-argument constructor.
     */
    @Test(expected = IllegalArgumentException.class)
    public void construct_invalidCustomFactory_invalidConstructor() throws Exception {
        new LeafAnnotationDescriptor<Target, Annotation>(
                getMember("invalidConstructorCustomFactoryAttribute"));
    }

    /**
     * Leaf annotations must have {@link RetentionPolicy#RUNTIME RUNTIME} retention.
     */
    @Test(expected = IllegalArgumentException.class)
    public void construct_invalidLeafAnnotation_nonRuntimeRetention() throws Exception {
        new LeafAnnotationDescriptor<NoRuntimeRetentionStubAnnotation, Annotation>(
                getMember("noRuntimeRetentionLeafAnnotationAttribute"));
    }

    /**
     * Leaf annotations must have {@link RetentionPolicy#RUNTIME RUNTIME} retention.
     */
    @Test(expected = IllegalArgumentException.class)
    public void construct_invalidLeafAnnotation_noRetention() throws Exception {
        new LeafAnnotationDescriptor<NoRetentionStubAnnotation, Annotation>(
                getMember("noRetentionLeafAnnotationAttribute"));
    }
    
    @Test
    public void getFactory_customFactory() throws Exception {
        assertTrue(new LeafAnnotationDescriptor<Target, StubLeafMembersAnnotation>(
                        getMember("customFactoryLeafAnnotationAttribute"))
                   .getFactory() instanceof StubLeafAnnotationFactory);
    }
    
    @Test
    public void getFactory() throws Exception {
        LeafAnnotationFactory<Target, ? extends Annotation> factory = 
            new LeafAnnotationDescriptor<Target, Annotation>(getMember("leafAnnotationAttribute"))
            .getFactory();
        
        assertTrue("Expected an AnnotationMemberValueLeafAnnotationFactory instance",
                   getInnerClass("AnnotationMemberValueLeafAnnotationFactory").isInstance(factory));
        assertEquals("leafAnnotationAttribute", ReflectionUtils.getValue(factory, "memberName"));
    }    
    
    private static Class<?> getInnerClass(String className) {
        
        for (Class<?> innerClass : LeafAnnotationDescriptor.class.getDeclaredClasses()) {
            
            if (innerClass.getSimpleName().contains(className)) {
                return innerClass;
            }
            
        }
        
        throw new AssertionError("Unable to find inner class " + className);
    }
    
    @Test
    public void getType() throws Exception {
        assertEquals(Target.class, 
                     new LeafAnnotationDescriptor<Target, Annotation>(
                             getMember("leafAnnotationAttribute"))
                     .getType());
    }
    
    @Test
    public void getTarget() throws Exception {
        assertEquals(SetUtils.asSet(ElementType.ANNOTATION_TYPE), 
                     new LeafAnnotationDescriptor<Target, Annotation>(
                             getMember("leafAnnotationAttribute"))
                     .getTarget());
    }  
    
    /**
     * According to the documentation, a leaf annotation that does <em>not</em> specify 
     * any {@link Target targets} can be used on any element. 
     */
    @Test
    public void getTarget_noTargetLeafAnnotation() throws Exception {
        assertEquals(SetUtils.asSet(ElementType.values()),
                     new LeafAnnotationDescriptor<NoTargetSpecifyingStubAnnotation, Annotation>(
                             getMember("noTargetSpecifyingLeafAnnotationAttribute"))
                     .getTarget());
    }
    
}