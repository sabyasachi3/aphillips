/**
 * Utility AspectJ advice classes for basic profiling and tracing.
 * 
 * <h3>Usage</h3>
 * 
 * To use Commons/Aspect, a class {@code com.qrmedia.commons.aspect.pointcut.Pointcuts} must
 * be present on the classpath, whose required methods {@code profiledOperation} and
 * {@code tracedOperation} must be valid pointcuts.
 * <p>
 * Instead of defining these pointcuts directly, e.g.
 * 
 * <pre>
 * public final class Pointcuts {
 *     &#064;Pointcut("execution(* uk.gov.mi6.AdvisedAgentService.*(..)) || within(...")
 *     public void profiledOperation() { }
 *     ...
 * }
 * </pre>
 * 
 * it is suggested to use a level of indirection, e.g.
 * 
 * <pre>
 *     ...
 *     &#064;Pointcut("uk.gov.mi6.AgentPointcuts.serviceOperation() 
 *                || uk.gov.mi6.AgentPointcuts.deniableOperation())
 *     public void profiledOperation() { }
 *     ...
 *     
 * public final class AgentPointcuts {
 *     &#064;Pointcut("execution(* uk.gov.mi6.AdvisedAgentService.*(..)) || ...)
 *     public void serviceOperation() { }
 *     
 *     &#064;Pointcut("within(* uk.gov.mi6.IllegalActivitiesService.*(..)) || ...)
 *     public void deniableOperation() { }
 *     ...
 * }
 * </pre>
 * This allows profiling to be defined <em>abstractly</em> for certain <u>logical</u> sections
 * of the code. The (implementation-dependent!) definition of which <u>concrete</u> classes constitute
 * these sections is cleanly separated.
 * 
 * <h3>Spring Integration</h3>
 * 
 * The Commons/Aspect advices are &quot;Spring-ready&quot;. To integrate them into your Spring
 * project, all you need to do is 
 * <ol>
 * <li>load the {@code commonsAspect-adviceContext.xml} context and
 * <li>declare a bean of the required {@code com.qrmedia.commons.aspect.pointcut.Pointcuts} type.
 * </ol> 
 */
package com.qrmedia.commons.aspect;