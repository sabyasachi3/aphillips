/*
 * @(#)BusinessObject.java     8 Jul 2008
 */
package com.qrmedia.commons.bean.businessobject.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an object as a quot;business object&quot;, supporting equality on the
 * basis of annotated {@link BusinessField &quot;business key&quot; fields}.
 * <p>
 * By default, business objects may be equal to instances of <i>any</i> class. This behaviour 
 * can be modified, if desired, by appropriately setting the {@link #equivalentClasses()} property. 
 * 
 * @author vita
 * @author anph
 * @since 2 Mar 2009
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BusinessObject {
    
    /**
     * Defines the classes which instances of this business object class may be equal to. More
     * specifically, if another object is not an instance of one of classes listed, or one of
     * their <i>sub</i>classes, it <u>cannot</u> be equal even if the business key fields match.
     * <p>
     * The class containing the <code>@BusinessObject</code> annotation is <u>automatically included</u>
     * in the collection of compatible classes. Note that any classes listed <b>must</b> be business 
     * object classes! 
     * <p>
     * The default value - an empty array - is interpreted as &quot;may be equal to an instance
     * of any business object class&quot;.
     * 
     * @return the classes which this business object can be equal to
     */
    Class<?>[] equivalentClasses() default {};
}
