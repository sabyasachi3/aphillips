/*
 * @(#)DelegatingPropertyCloner.java     9 Feb 2009
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
package com.qrmedia.commons.persistence.hibernate.clone.property;

import org.apache.commons.beanutils.PropertyUtils;

import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityGraphCloner;
import com.qrmedia.commons.persistence.hibernate.clone.wiring.AbstractPropertyModifyingCommand;

/**
 * A &quot;catch-all&quot; cloner that adds the property to the queue of entities
 * to be cloned, and arranges for it to be wired up later.
 * <p>
 * The value of the property must be non-<code>null</code>; the cloner should be
 * configured so that <code>null</code> values are taken care of elsewhere, e.g.
 * by a previously-called {@link SimplePropertyCloner}.
 *  
 * @author anph
 * @see SimplePropertyCloner
 * @see SimpleCollectionCloner
 * @see CloneablePropertyCloner
 * @see DelegatingCollectionCloner 
 * @since 9 Feb 2009 
 *
 */
public class DelegatingPropertyCloner extends AbstractNonNullValueAwarePropertyCloner {

    /* (non-Javadoc)
     * @see com.qrmedia.commons.beans.clone.property.AbstractNonNullValueAwarePropertyCloner#cloneNonNullValue(java.lang.Object, java.lang.Object, java.lang.String, java.lang.Object, com.qrmedia.commons.beans.clone.HibernateEntityGraphCloner)
     */
    @Override
    protected <T> boolean cloneNonNullValue(Object source, Object target, String propertyName,
            Object propertyValue, HibernateEntityGraphCloner entityGraphCloner) 
            throws IllegalArgumentException {
        /*
         * Queue the non-simple property value for cloning and create a command to
         * wire the created clone to the target.
         */
        entityGraphCloner.addEntity(propertyValue);
        entityGraphCloner.addGraphWiringCommand(
                new SetPropertyCommand(target, propertyName, propertyValue));
        return true;
    }

    /**
     * &quot;Wires up&quot; a non-collection property by setting the value to the clone
     * of the original object.
     *  
     * @author anph
     * @since 9 Feb 2009
     *
     */
    static class SetPropertyCommand extends AbstractPropertyModifyingCommand {
        
        /**
         * Creates a <code>SetPropertyCommand</code>.
         * 
         * @param target    the object whose property should be set
         * @param propertyName  the name of the bean property to set
         * @param originalValue the original value of the property
         */
        SetPropertyCommand(Object target, String propertyName,
                Object originalValue) {
            super(target, propertyName, originalValue);
        }
        
        /* (non-Javadoc)
         * @see com.tomtom.delphi.util.HibernateEntityBeanCloner.AbstractPropertyModifyingCommand#wireUpProperty(java.lang.Object, java.lang.String, java.lang.Object)
         */
        @Override
        protected void wireUpProperty(Object target, String propertyName,
                Object originalEntityClone) {
            
            try {
                PropertyUtils.setSimpleProperty(target, propertyName, originalEntityClone);
            } catch (Exception exception) {
                throw new AssertionError("Unable to set property '" + propertyName + "' on "
                                         + target + " due to: " + exception.getMessage());
            }
            
        }

    }    

}
