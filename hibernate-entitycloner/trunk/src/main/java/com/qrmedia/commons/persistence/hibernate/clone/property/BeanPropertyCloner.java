/*
 * @(#)BeanPropertyCloner.java     11 Feb 2009
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

import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityGraphCloner;

/**
 * A cloner of a certain type of bean property. Should ignore property types
 * that it is unable to process.
 * 
 * @author anph
 * @since 9 Feb 2009
 *
 */
public interface BeanPropertyCloner {
    
    /**
     * Clones the specific property <u>only if</u> it is a property this cloner
     * can handle (some cloners might only deal with <code>Collections</code>,
     * for instance).
     * 
     * @param source    the entity whose property should be cloned
     * @param target    the entity to which the property should be cloned
     * @param propertyName  the bean name of the property
     * @param entityGraphCloner the entity graph cloner with which new objects to clone
     *                          should be queued etc.
     * @return <code>true</code> iff the property was successfully cloned
     * @throws IllegalArgumentException if the property cannot be found, accessed or copied
     */
    boolean clone(Object source, Object target, String propertyName, 
            HibernateEntityGraphCloner entityGraphCloner) throws IllegalArgumentException;
}