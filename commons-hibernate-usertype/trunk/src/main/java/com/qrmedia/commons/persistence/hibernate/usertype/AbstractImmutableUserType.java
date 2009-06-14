/*
 * @(#)AbstractImmutableUserType.java     25 Feb 2009
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

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * A base {@link UserType} for immutable values.
 * 
 * @author anph
 * @since 25 Feb 2009
 *
 */
public abstract class AbstractImmutableUserType extends AbstractUserType {

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    public boolean isMutable() {
        return false;
    }
    
    /* (non-Javadoc)
     * @see com.qrmedia.commons.persistence.hibernate.usertype.AbstractUserType#isDirty(java.lang.Object)
     */
    @Override
    protected boolean isDirty(Object object) {
        // immutable objects can't be dirty
        return false;
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
     */
    public Object deepCopy(Object value) throws HibernateException {
        // for immutable objects, a reference to the original is fine
        return value;
    }

}
