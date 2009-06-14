/*
 * @(#)CompositeAnnotation.java     1 May 2009
 */
package com.qrmedia.pattern.compositeannotation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.qrmedia.pattern.compositeannotation.api.AnnotatedElements;
import com.qrmedia.pattern.compositeannotation.validation.CompositeAnnotationValidationProcessor;

/**
 * Declares an annotation as a composite or &quot;macro&quot; annotation.
 * A composite annotation is simply a &quot;shorthand&quot; way of expressing a
 * number of annotations that commonly occur together.
 * <p>
 * Runtime information about the composite annotations of an annotated element are
 * available via the {@link AnnotatedElements} interface. The composite annotation
 * itself is &quot;transparent&quot; at runtime: only the annotations a composite
 * <em>stands for</em> are visible. Of course, composite annotations <em>are</em> still visible 
 * to the &quot;standard&quot; annotation reflection methods such as {@link Class#getAnnotations()}.
 * <p>
 * To create a composite annotation, create a new annotation class and annotate it
 * with {@code @CompositeAnnotation}. <strong>The new annotation class
 * must have a {@link RetentionPolicy#RUNTIME RUNTIME} retention policy.</strong>
 * <p>
 * Any members of the annotation that 
 * <ul>
 * <li>are of an annotation type <em>and</em>
 * <li>are annotated with {@link LeafAnnotation @LeafAnnotation}</li>
 * </ul>
 * will be visible to callers inspecting the element via {@code AnnotatedElements}.
 * <p>
 * For more details about requirements for leaf annotations see {@link LeafAnnotation}.
 * <p>
 * <strong>NB: Since the leaf annotations are not visible to the compiler or standard annotation 
 * reflection methods, composite annotations cannot be * used to &quot;macro&quot; annotations such 
 * as {@code @Retention} and {@code @Target} that affect compilation.</strong>
 * 
 * @author aphillips
 * @since 1 May 2009
 * @see CompositeAnnotationValidationProcessor
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface CompositeAnnotation { }
