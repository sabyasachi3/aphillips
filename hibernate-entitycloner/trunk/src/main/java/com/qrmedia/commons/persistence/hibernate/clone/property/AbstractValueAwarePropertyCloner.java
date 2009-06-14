/*
 * @(#)AbstractValueAwarePropertyCloner.java     9 Feb 2009
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

/**
 * A {@link BeanPropertyCloner} that is also passed the <i>value</i> of the
 * property to be cloned.
 * 
 * @author anph
 * @since 9 Feb 2009
 *
 */
abstract class AbstractValueAwarePropertyCloner implements BeanPropertyCloner {

    /* (non-Javadoc)
     * @see com.qrmedia.commons.beans.clone.HibernateEntityBeanCloner.BeanPropertyCloner#clone(java.lang.Object, java.lang.Object, java.lang.String, com.qrmedia.commons.beans.clone.HibernateEntityGraphCloner)
     */
    public boolean clone(Object source, Object target, String propertyName,
            HibernateEntityGraphCloner entityGraphCloner)
            throws IllegalArgumentException {
        
        try {
            return cloneValue(source, target, propertyName, 
                  PropertyUtils.getSimpleProperty(source, propertyName), entityGraphCloner);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to copy property '"
                    + propertyName + "' from " + source + " to " + target
                    + " due to " + exception.getClass().getSimpleName() + ": " 
                    + exception.getMessage());
        }
        
    }
    
    /**
     * Clones the given property as described in 
     * {@link #clone(Object, Object, String, HibernateEntityGraphCloner)}.
     * 
     * @param <T>   if the property is a collection, the type of the collection elements
     * @param source    the entity whose property should be cloned
     * @param target    the entity to which the property should be cloned
     * @param propertyName  the bean name of the property
     * @param propertyValue the value of the bean property
     * @param entityGraphCloner the entity graph cloner with which new objects to clone
     *                          should be queued etc.
     * @return <code>true</code> iff the property was successfully cloned
     * @throws IllegalArgumentException if the property cannot be copied
     */
    protected abstract <T> boolean cloneValue(Object source, Object target, String propertyName,
            Object propertyValue, HibernateEntityGraphCloner entityGraphCloner)
            throws IllegalArgumentException;
}
