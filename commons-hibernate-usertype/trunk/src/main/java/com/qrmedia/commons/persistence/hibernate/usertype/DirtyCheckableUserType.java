/*
 * @(#)DirtyCheckableUserType.java     25 Feb 2009
 *
 * Copyright © 2009 Andrew Phillips.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.qrmedia.commons.persistence.hibernate.usertype;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * A Hibernate {@link UserType} for mutable objects that support dirty checking. This
 * can be especially attractive for objects for which it is expensive to calculate equals.
 * <p>
 * It goes without saying that dirty checking doesn't make any sense for <u>immutable</u> 
 * objects!
 * 
 * @author anph
 * @since 25 Feb 2009
 *
 */
public abstract class DirtyCheckableUserType extends MutableUserType {
    
    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
     */
    public final boolean equals(Object x, Object y) throws HibernateException {
        /*
         * x and y are references to the object as it was originally hydrated and as it is
         * now, respectively. Comparing these determines if the object needs updating.
         * 
         * The "original" version of the object is created by a call to the deepCopy
         * method when the object is loaded. But if it is too expensive to create a deepCopy
         * (say you're hydrating a large array, for instance), one option is to simply
         * return a reference to the loaded object.
         * 
         * This presents a problem for dirty checking, though: now x and y are references
         * to *the same object*, so there is no way to tell if it has been modified.
         * *Unless* the object supports some kind of dirty checking, which may be much cheaper
         * to implement than comparing two instances. 
         * 
         * This kind of dirty checking should be done in isDirty.
         */
        return (!isDirty(x) && !isDirty(y) && ObjectUtils.equals(x, y));
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
     */
    public final int hashCode(Object x) throws HibernateException {
        /*
         * Persistence equality (see equals) requires that the an object, if dirty, has a
         * *different* hash code from the *same*, *identical* object if clean.
         */
        return new HashCodeBuilder().append(x).append(isDirty(x)).toHashCode();
    }

    /**
     * Indicates if the given object has been modified since it was originally
     * loaded, and thus needs updating.
     * <p>
     * For immutable entities, this can always return <code>false</code>. For
     * mutable entities, this must return <code>true</code> iff the entity needs
     * to be updated.
     * 
     * @param object    the object to be inspected
     * @return  <code>true</code> iff the object needs to be updated
     */
    protected abstract boolean isDirty(Object object);
}