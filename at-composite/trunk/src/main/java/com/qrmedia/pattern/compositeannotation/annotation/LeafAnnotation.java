/*
 * @(#)LeafAnnotation.java     1 May 2009
 */
package com.qrmedia.pattern.compositeannotation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory;

/**
 * Indicates that an member of a composite annotation is one of the leaf annotations 
 * comprising the composite.
 * <p>
 * {@code @LeafAnnotation} should thus only be applied to annotation members that have
 * an <em>annotation</em> type; annotating members of other types may result in an exception.
 * <p>
 * By default, the <u>value</u> of the leaf annotation will be value of the <u>member</u>.
 * However, if more flexibility is required (e.g. to generate leaf annotations that depend on
 * derived or other non-constant values), a factory class can be specified via the {@link #factoryClass()}
 * member.
 * <p>
 * This must have type parameters {@code <LeafAnnotationClass, CompositeClass>}
 * and declare a public no-argument constructor. Otherwise, a compilation error will be thrown
 * by the validating processor, if used.
 * <p>
 * <strong>Note:</strong> in keeping with the restriction on "regular" annotations there are
 * certain rules for the use of the {@code @LeafAnnotation} annotation. Adherence to these
 * rules is verified by the {@linkplain com.qrmedia.pattern.compositeannotation.validation.CompositeAnnotationValidationProcessor 
 * validating processor}.
 * 
 * <ol>
 * <li>The retention policy of the leaf annotation must be {@link RetentionPolicy#RUNTIME RUNTIME}.</li>
 * <li>There may also only be <u>one</u> leaf annotation of a given type per composite.</li>
 * <li>The {@link ElementType target} of a leaf annotation must match that of the composite
 * annotation in which it is declared.</li>
 * <li>An element with a "regular" annotation of type {@code T} may not also be annotated with
 * a composite that declares a leaf annotation of type {@code T}.</li>
 * <li>An element with a composite annotation that declares a leaf of type {@code T} may not 
 * also be annotated with a <em>different</em> composite that <u>also</u> declares a leaf 
 * annotation of type {@code T}.</li>
 * </ol>
 * If conditions 1-3 are violated, an exception may be thrown at runtime when the composite
 * is examined. Violations of conditions 4 and 5 result in undefined behaviour when querying for
 * annotations of the element affected.
 * <p>
 * Example:
 * <pre>
 * public interface @AgentRecord {
 *   int minNumberOfKills() ;
 *   
 *   &#064;LeafAnnotation
 *   CodeNumber codeNumber() default @CodeNumber("007");
 *   
 *   &#064;LeafAnnotation    // invalid - two leaf annotations of the same type!
 *   CodeNumber previousCodeNumber() default @CodeNumber("006");
 *   
 *   /*
 *    * The default is *ignored* (the factory is always called) but is useful to specify
 *    * to avoid being asked to supply a value every time the composite annotation is used.
 *    * /
 *   &#064;LeafAnnotation(factoryClass = LicenseToKillEvaluationFactory.class)
 *   LicenseToKill hasLicenseToKill default @LicenseToKill(false);
 * }
 * 
 * class LicenseToKillEvaluationFactory implements LeafAnnotationFactory&lt;LicenseToKill, AgentRecord&gt; {
 *   ...
 * }
 * 
 * /*
 *  * Equivalent to
 *  *
 *  * @CodeNumber("007")
 *  * @LicenseToKill // whatever the factory returns
 *  * /
 * @AgentRecord(minNumberOfKills = 10);
 * class JuniorAgent {
 *   ...  
 * }
 * 
 * &#064;AgentRecord(minNumberOfKills = 50);
 * &#064;LicenseToKill(false) // invalid - @AgentRecord also declared an annotation of this type
 * class SeniorAgent {
 *   ...  
 * }
 * 
 * /* 
 *  * Overwrites the default codeNumber, so equivalent to
 *  *
 *  * @CodeNumber("00")
 *  * @LicenseToKill // whatever the factory returns
 *  * /
 * &#064;AgentRecord(minNumberOfKills = 99, codeNumber = @CodeNumber("00"));
 * class DoubleOhAgent {
 *   ...  
 * }  
 * </pre> 
 * 
 * @author aphillips
 * @since 1 May 2009
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LeafAnnotation {
    
    /**
     * If this attribute is specified, an instance of the class will be created and called
     * when an instance of the leaf annotation is required.
     * <p>
     * The class must declare a public, no-argument constructor and needs to be thread-safe,
     * as the same factory instance may be called for concurrent requests for a leaf instance.
     * 
     * @return  the class of the factory to create instances of the leaf annotation
     */
    @SuppressWarnings("unchecked")
    Class<? extends LeafAnnotationFactory> factoryClass() default LeafAnnotationFactory.class;
}
