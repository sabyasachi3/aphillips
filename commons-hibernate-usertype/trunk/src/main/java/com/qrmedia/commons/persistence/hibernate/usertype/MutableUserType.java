/*
 * @(#)MutableUserType.java     25 Feb 2009
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
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * A skeleton Hibernate {@link UserType}. Assumes, by default, that the return
 * type is mutable.
 * <p>
 * User types returning only <strong>immutable</strong> objects should extend 
 * {@link ImmutableUserType}.
 * 
 * @author anph
 * @since 25 Feb 2009
 *
 */
public abstract class MutableUserType implements UserType {

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
        return ObjectUtils.equals(x, y);
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
     */
    public int hashCode(Object x) throws HibernateException {
        assert (x != null);
        return x.hashCode();
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
     */
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        // also safe for mutable objects
        return deepCopy(cached);
    }

    /**
     * Disassembles the object in preparation for serialization. 
     * See {@link org.hibernate.usertype.UserType#disassemble(java.lang.Object)}.
     * <p>
     * <strong>Subtypes whose {@code deepCopy} implementation returns a
     * non-serializable object must override this method.</strong> 
     * 
     * @see #deepCopy(Object)
     */
    public Serializable disassemble(Object value) throws HibernateException {
        // also safe for mutable objects
        Object deepCopy = deepCopy(value);
        assert (deepCopy instanceof Serializable);
        return (Serializable) deepCopy;
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