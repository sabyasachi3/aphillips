/*
 * @(#)RuntimeConfiguredAnnotationFactoryTest.java     25 May 2009
 */
package com.qrmedia.commons.lang.annotation.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

import org.junit.Test;

import com.qrmedia.commons.collections.MapUtils;

/**
 * Unit tests for the {@link RuntimeConfiguredAnnotationFactory}.
 * 
 * @author aphillips
 * @since 25 May 2009
 *
 */
public class RuntimeConfiguredAnnotationFactoryTest {

    
    @Test(expected = IllegalArgumentException.class)
    public void newInstance_nullClass() {
        RuntimeConfiguredAnnotationFactory.newInstance(null, new HashMap<String, Object>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void newInstance_nullMembers() {
        RuntimeConfiguredAnnotationFactory.newInstance(StubAnnotation.class, null);
    }

    @Retention(RetentionPolicy.RUNTIME)
    static @interface StubAnnotation {
        String objMember() default "";
        int primitiveMember() default 0;
        Class<?>[] arrayMember() default {};
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void newInstance_missingMemberName() {
        RuntimeConfiguredAnnotationFactory.newInstance(StubAnnotation.class, 
                new HashMap<String, Object>());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void newInstance_nonexistentMethodName() {
        RuntimeConfiguredAnnotationFactory.newInstance(StubAnnotation.class,
                MapUtils.<String, Object>toMap("objMember", "", "primitiveMember", 0, 
                        "arrayMember", new Class<?>[0], "nonexistent", new Object()));
    }
    
    /**
     * Only values for <em>members</em> should be supplied, not other methods. 
     */
    @Test(expected = IllegalArgumentException.class)
    public void newInstance_nonexistentMemberName() {
        RuntimeConfiguredAnnotationFactory.newInstance(StubAnnotation.class,
                MapUtils.<String, Object>toMap("objMember", "", "primitiveMember", 0, 
                        "arrayMember", new Class<?>[0], "hashCode", 7));
    }    
    
    @Test(expected = IllegalArgumentException.class)
    public void newInstance_invalidMemberValue() {
        RuntimeConfiguredAnnotationFactory.newInstance(StubAnnotation.class,
                MapUtils.<String, Object>toMap("objMember", "", "primitiveMember", 0, 
                        "arrayMember", new String[0]));
    }

    /**
     * Annotation member values may not be {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void newInstance_nullMemberValue() {
        RuntimeConfiguredAnnotationFactory.newInstance(StubAnnotation.class,
                MapUtils.<String, Object>toMap("objMember", "", "primitiveMember", 0, 
                        "arrayMember", null));
    }
    
    @Test
    @StubAnnotation(objMember = "James Bond", primitiveMember = 7, arrayMember = { Class.class })
    public void newInstance() throws Exception {
        StubAnnotation expectedAnnotation = RuntimeConfiguredAnnotationFactoryTest.class
                .getMethod("newInstance").getAnnotation(StubAnnotation.class);
        StubAnnotation actualAnnotation = RuntimeConfiguredAnnotationFactory.newInstance(StubAnnotation.class,
                 MapUtils.<String, Object>toMap("objMember", "James Bond", 
                         "primitiveMember", 7, 
                         "arrayMember", new Class<?>[] { Class.class }));
        assertEquals(expectedAnnotation, actualAnnotation);
        
        // check that equals, hashCode, toString and annotationType are properly implemented
        assertTrue("actualAnnotation.equals(expectedAnnotation) fails",
                   actualAnnotation.equals(expectedAnnotation));
        assertEquals(expectedAnnotation.hashCode(), actualAnnotation.hashCode());
        assertEquals(expectedAnnotation.toString(), actualAnnotation.toString());
        assertEquals(StubAnnotation.class, actualAnnotation.annotationType());
    }
    
}
