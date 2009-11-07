/*
 * @(#)ReadableStringBuilderUserType.java     Oct 3, 2007
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
package com.xebialabs.aphillips.usertype.examples.take5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import com.qrmedia.commons.persistence.hibernate.usertype.DirtyCheckableUserType;

/**
 * A {@link UserType} that persists a {@code DirtyCheckingStringBuilder}.
 * 
 * @author anph
 * @since Oct 3, 2007
 * 
 */
public class DirtyCheckingStringBuilderUserType extends DirtyCheckableUserType {
    
    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    public Class<DirtyCheckingStringBuilder> returnedClass() {
        return DirtyCheckingStringBuilder.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
     */
    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
            throws HibernateException, SQLException {
        return nullSafeToStringBuilder(Hibernate.STRING.nullSafeGet(resultSet, names[0]));
    }
    
    private static DirtyCheckingStringBuilder nullSafeToStringBuilder(Object value) {
        return ((value != null) ? new DirtyCheckingStringBuilder(value.toString()) : null);
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
     */
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index)
            throws HibernateException, SQLException {
        Hibernate.STRING.nullSafeSet(preparedStatement, 
                (value != null) ? value.toString() : null, index);
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.persistence.hibernate.usertype.MutableUserType#isDirty(java.lang.Object)
     */
    @Override
    protected boolean isDirty(Object object) {
        return ((DirtyCheckingStringBuilder) object).wasModified();
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
     */
    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return nullSafeToStringBuilder(value);
    }

}