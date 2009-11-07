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
package com.xebialabs.aphillips.usertype.examples.take3;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * A {@link UserType} that persists a {@code StringBuilder} legibly (i.e. not
 * using default Java serialization) - take3.
 * 
 * @author anph
 * @since Oct 3, 2007
 * 
 */
public class ReadableStringBuilderUserType implements UserType {
    
    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    public Class<StringBuilder> returnedClass() {
        return StringBuilder.class;
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
    
    private static StringBuilder nullSafeToStringBuilder(Object value) {
        return ((value != null) ? new StringBuilder(value.toString()) : null);
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
     */
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index)
            throws HibernateException, SQLException {
        Hibernate.STRING.nullSafeSet(preparedStatement, 
                (value != null) ? value.toString() : null, index);
    }

    /* "Maybe if we make actually create a copy..?" */
    
    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return nullSafeToStringBuilder(value);
    }
    
    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return ObjectUtils.equals(x, y);
    }
    
    @Override
    public int hashCode(Object x) throws HibernateException {
        assert (x != null);
        return x.hashCode();
    }
    
    /* should return copies for mutables, according to the documentation */
    
    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return deepCopy(original);
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return deepCopy(cached);
    }
    
}