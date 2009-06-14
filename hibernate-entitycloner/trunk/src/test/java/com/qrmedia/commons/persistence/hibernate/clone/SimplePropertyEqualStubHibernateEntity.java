/*
 * @(#)SimplePropertyEqualStubHibernateEntity.java     12 Feb 2009
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
package com.qrmedia.commons.persistence.hibernate.clone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A {@link StubHibernateEntity} that uses the value of a simple property to
 * define equality.
 * 
 * @author anph
 * @since 12 Feb 2009
 * @see NonSimplePropertyEqualStubHibernateEntity
 *
 */
public class SimplePropertyEqualStubHibernateEntity extends StubHibernateEntity {

    /**
     * Constructs a <code>SimplePropertyEqualStubHibernateEntity</code>
     * 
     * @param simpleBeanProperty the value of the simple bean property
     */
    public SimplePropertyEqualStubHibernateEntity(String simpleBeanProperty) {
        this.simpleBeanProperty = simpleBeanProperty;
    }
    
    /**
     * Default constructor for bean compatibility.
     */
    public SimplePropertyEqualStubHibernateEntity() { }
    
    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SimplePropertyEqualStubHibernateEntity)) {
            return false;
        }

        return StringUtils.equals(simpleBeanProperty, 
                ((SimplePropertyEqualStubHibernateEntity) obj).simpleBeanProperty);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(simpleBeanProperty).toHashCode();
    }
    
}