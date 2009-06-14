/*
 * @(#)AtCompositeDemo.java     28 May 2009
 */
package com.qrmedia.pattern.compositeannotation.example;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.qrmedia.pattern.compositeannotation.AtCompositeAnnotatedElements;
import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;
import com.qrmedia.pattern.compositeannotation.example.annotation.TargetRetentionLeafCompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.validation.CompositeAnnotationValidationProcessor;

/**
 * A demonstration of integrating <strong>@Composite</strong> with Spring.
 * <p>
 * You can test the {@linkplain CompositeAnnotationValidationProcessor validating processor}
 * by passing a {@code -processor com.qrmedia.pattern.compositeannotation.validation.CompositeAnnotationValidationProcessor} 
 * argument to <strong>javac</strong>.
 * 
 * @author aphillips
 * @since 28 May 2009
 *
 */
public class AtCompositeDemo {
    private static final String[] CONTEXT_FILENAMES = 
        new String[] { "example-applicationContext.xml" };
    
    @Retention(RetentionPolicy.RUNTIME)
    @CompositeAnnotation
    private static @interface InvalidLeafStubCompositeAnnotation {
        // invalid, since the annotated attribute does not return an annotation
        @LeafAnnotation 
        int nonAnnotationAttribute() default 0;
    }

    @Resource
    @TargetRetentionLeafCompositeAnnotation(runtimeRetention = true)
    private static @interface AnnotatedAnnotation {}

    @Resource
    @TargetRetentionLeafCompositeAnnotation(runtimeRetention = false)
    private static @interface OtherAnnotatedAnnotation {}
    
    @InvalidLeafStubCompositeAnnotation
    public static void main(String[] args) {
        Log log = LogFactory.getLog(AtCompositeDemo.class);
        
        AtCompositeAnnotatedElements annotatedElements = 
            (AtCompositeAnnotatedElements) new ClassPathXmlApplicationContext(CONTEXT_FILENAMES)
            .getBean("com.qrmedia.pattern.compositeannotation.atCompositeAnnotatedElements");
        
        log.warn("IMPORTANT: for demonstration purposes only! Composite annotations cannot be used to \"macro\" annotations such as @Retention or @Target that need to be visible to the compiler and/or standard annotation reflection methods! See the Javadoc for @CompositeAnnotation.");
        
        log.info("Retrieving annotations from AnnotatedAnnotation.class");
        log.info(Arrays.toString(annotatedElements.getAnnotations(AnnotatedAnnotation.class)));
        
        log.info("Retrieving annotations from OtherAnnotatedAnnotation.class");
        log.info(Arrays.toString(annotatedElements.getAnnotations(OtherAnnotatedAnnotation.class)));
    }
    
}
