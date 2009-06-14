/*
 * @(#)NonSimplePropertyEqualStubHibernateEntity.java     12 Feb 2009
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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A {@link StubHibernateEntity} that uses the value of a non-simple property to
 * define equality.
 * 
 * @author anph
 * @since 12 Feb 2009
 * @see SimplePropertyEqualStubHibernateEntity
 *
 */
public class NonSimplePropertyEqualStubHibernateEntity extends StubHibernateEntity {

    /**
     * Constructs a <code>NonSimplePropertyEqualStubHibernateEntity</code>
     * 
     * @param nonSimpleBeanProperty the value of the simple bean property
     */
    public NonSimplePropertyEqualStubHibernateEntity(StubHibernateEntity nonSimpleBeanProperty) {
        this.nonSimpleBeanProperty = nonSimpleBeanProperty;
    }
    
    /**
     * Default constructor for bean compatibility.
     */
    public NonSimplePropertyEqualStubHibernateEntity() { }
    
    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NonSimplePropertyEqualStubHibernateEntity)) {
            return false;
        }

        return ObjectUtils.equals(nonSimpleBeanProperty, 
                ((NonSimplePropertyEqualStubHibernateEntity) obj).nonSimpleBeanProperty);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(nonSimpleBeanProperty).toHashCode();
    }
    
}