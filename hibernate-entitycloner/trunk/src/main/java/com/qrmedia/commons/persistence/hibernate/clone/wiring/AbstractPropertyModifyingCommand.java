/*
 * @(#)AbstractPropertyModifyingCommand.java     9 Feb 2009
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
package com.qrmedia.commons.persistence.hibernate.clone.wiring;

import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Contributes to the &quot;wiring up&quot; of a property of a target object using a 
 * clone of an original value.
 * 
 * @author anph
 * @since 9 Feb 2009
 *
 */
public abstract class AbstractPropertyModifyingCommand implements GraphWiringCommand {
    protected final Object target;
    protected final String propertyName;
    protected final Object originalEntity;
    
    /**
     * Creates an <code>AbstractPropertyModifyingCommand</code>.
     * 
     * @param target    the object whose property should be set
     * @param propertyName  the name of the bean property to set
     * @param originalEntity an entity originally related to the property 
     *                       (e.g. the original value, or a member of the original collection)
     */
    public AbstractPropertyModifyingCommand(Object target, String propertyName,
            Object originalEntity) {
        this.target = target;
        this.propertyName = propertyName;
        this.originalEntity = originalEntity;
    }
    
    /* (non-Javadoc)
     * @see com.tomtom.delphi.util.HibernateEntityGraphCloner.GraphWiringCommand#execute(java.util.Map)
     */
    public void execute(IdentityHashMap<Object, Object> entityClones) {
        
        if (!entityClones.containsKey(originalEntity)) {
            throw new AssertionError("No clone for " + originalEntity + " available?!");
        }
        
        wireUpProperty(target, propertyName, entityClones.get(originalEntity));
    }
    
    /**
     * Wires up - perhaps partially (e.g. one object of many in a collection) -
     * a bean property of a target object using a clone of the original value.
     *  
     * @param target    the object whose property will be modified
     * @param propertyName  the name of the property
     * @param originalEntityClone    a clone of an entity originally related to the property 
     */
    protected abstract void wireUpProperty(Object target, String propertyName, 
            Object originalEntityClone);

    /* (non-Javadoc)
     * @see com.tomtom.delphi.util.HibernateEntityGraphCloner.GraphWiringCommand#forEntities()
     */
    public Collection<Object> forEntities() {
        return Arrays.asList(originalEntity);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        }

        // "!(obj.runtimeClass == this.runtimeClass)"
        if ((obj == null) || !(getClass().equals(obj.getClass()))) {
            return false;
        }

        AbstractPropertyModifyingCommand other = (AbstractPropertyModifyingCommand) obj;
        return new EqualsBuilder().append(target, other.target)
               .append(propertyName, other.propertyName)
               .append(originalEntity, other.originalEntity).isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        
        // different subclasses of AbstractPropertyModifyingCommand are different
        return new HashCodeBuilder().append(getClass()).append(target)
               .append(propertyName).append(originalEntity).toHashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }          
    
}