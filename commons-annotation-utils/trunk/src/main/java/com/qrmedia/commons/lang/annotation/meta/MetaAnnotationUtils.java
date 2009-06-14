/*
 * @(#)MetaAnnotationUtils.java     26 May 2009
 */
package com.qrmedia.commons.lang.annotation.meta;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility methods for retrieving meta-information about annotations, such as
 * {@link Target target} and {@link Retention retention}.
 * 
 * @author aphillips
 * @since 26 May 2009
 *
 */
public class MetaAnnotationUtils {
    /**
     * The {@link Target documented} default value is &quot;all element types&quot;.
     */
    private static final Set<ElementType> DEFAULT_TARGET = asUnmodifiableSet(ElementType.values());
    
    /**
     * The {@link Retention documented} default value is CLASS.
     */
    private static final RetentionPolicy DEFAULT_RETENTION = RetentionPolicy.CLASS;
    
    private static <E> Set<E> asUnmodifiableSet(E... objs) {
        return Collections.unmodifiableSet(new HashSet<E>(Arrays.asList(objs)));
    }
    
    /**
     * @param annotationType    the annotation type class whose target is required
     * @return  the value of the {@link Target @Target} annotation on the given class, if present,
     *          or the documented default value; for a {@code null} type, returns {@code null}                          
     */
    public static Set<ElementType> getTarget(Class<? extends Annotation> annotationType) {
        
        if (annotationType == null) {
            return null;
        }
        
        Target targetAnnotation = annotationType.getAnnotation(Target.class);
        return ((targetAnnotation != null) ? asUnmodifiableSet(targetAnnotation.value()) 
                                           : DEFAULT_TARGET);
    }

    /**
     * @param annotationType    the annotation type class whose retention policy is required
     * @return  the value of the {@link Retention @Retention} annotation on the given class, if 
     *          present, or the documented default value; for a {@code null} type, returns 
     *          {@code null}                          
     */
    public static RetentionPolicy getRetention(Class<? extends Annotation> annotationType) {
        
        if (annotationType == null) {
            return null;
        }
        
        Retention retentionAnnotation = annotationType.getAnnotation(Retention.class);
        return ((retentionAnnotation != null) ? retentionAnnotation.value() : DEFAULT_RETENTION);
    }
}
