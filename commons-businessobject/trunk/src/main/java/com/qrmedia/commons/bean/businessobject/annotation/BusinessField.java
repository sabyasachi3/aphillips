/*
 * @(#)BusinessField.java     8 Jul 2008
 */
package com.qrmedia.commons.bean.businessobject.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.qrmedia.commons.bean.businessobject.BusinessObjectUtils;

/**
 * Indicates that the annotated field is to be considered part of the &quot;business key&quot;
 * for the given {@link BusinessObject} and used for <code>equals/hashCode/toString</code> 
 * calculations, as indicated.
 * 
 * @author anphilli
 * @since 2 Mar 2009
 *
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BusinessField {

    /**
     * @return <code>true</code> iff field is supposed to be included when determining if the
     *         object is equal to another object. Any such field will <u>also</u> be included
     *         in the {@link BusinessObjectUtils#hashCode() hashCode} calculation.
     */
    boolean includeInEquals() default true;
    
    /**
     * @return <code>true</code> iff the field is supposed to be included in the string
     *         representation of the object
     */
    boolean includeInToString() default true;
}
