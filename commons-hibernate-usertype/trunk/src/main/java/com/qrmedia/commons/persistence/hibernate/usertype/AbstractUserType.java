/*
 * @(#)AbstractUserType.java     25 Feb 2009
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

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * A skeleton Hibernate {@link UserType}. Assumes, by default, that the return
 * type is mutable.
 * <p>
 * User types returning only <strong>immutable</strong> objects should extend 
 * {@link AbstractImmutableUserType}.
 * 
 * @author anph
 * @since 25 Feb 2009
 *
 */
public abstract class AbstractUserType implements UserType {

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    public boolean isMutable() {
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
     */
    public boolean equals(Object x, Object y) throws HibernateException {
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
         * to implement than making a deep copy. 
         * 
         * This kind of dirty checking should be done in isDirty.
         */
        return (!isDirty(x) && !isDirty(y) && ObjectUtils.equals(x, y));
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
     */
    public int hashCode(Object x) throws HibernateException {
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

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
     */
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        // also safe for mutable objects
        return deepCopy(cached);
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
     */
    public Serializable disassemble(Object value) throws HibernateException {
        // also safe for mutable objects
        return (Serializable) deepCopy(value);
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        // also safe for mutable objects
        return deepCopy(original);
    }

}