/*
 * @(#)CloneablePropertyCloner.java     11 Feb 2009
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
 * Clones objects marked with the {@link Cloneable} interface.
 * <p>
 * <b>N.B.:</b> Certain collection implementations (e.g. <code>TreeSet</code>)
 * are also <code>Cloneable</code>!
 * 
 * @author anph
 * @see SimplePropertyCloner
 * @see SimpleCollectionCloner
 * @see DelegatingCollectionCloner 
 * @see DelegatingPropertyCloner
 * @since 11 Feb 2009
 *
 */
public class CloneablePropertyCloner extends AbstractNonNullValueAwarePropertyCloner {

    /* (non-Javadoc)
     * @see com.qrmedia.commons.beans.clone.property.AbstractNonNullValueAwarePropertyCloner#cloneNonNullValue(java.lang.Object, java.lang.Object, java.lang.String, java.lang.Object, com.qrmedia.commons.beans.clone.HibernateEntityGraphCloner)
     */
    @Override
    protected <T> boolean cloneNonNullValue(Object source, Object target,
            String propertyName, Object propertyValue,
            HibernateEntityGraphCloner entityGraphCloner)
            throws IllegalArgumentException {
        
        if (propertyValue instanceof Cloneable) {
            
            try {
                PropertyUtils.setSimpleProperty(target, propertyName, 
                                                clone((Cloneable) propertyValue));
                return true;
            } catch (Exception exception) {
                throw new IllegalArgumentException("Unable to set property '"
                        + propertyName + "' on " + target + " due to " 
                        + exception.getClass().getSimpleName() + ": " 
                        + exception.getMessage());
            }
            
        }
        
        // other properties can't be handled by this cloner
        return false;
    }

    private static Cloneable clone(Cloneable cloneable) throws IllegalArgumentException {
        /*
         * Assumes - in spite of the detailed warnings in the documentation
         * for Clonable - that the cloneable *will* have a public clone() method,
         * as per convention.
         */
        try {
            return (Cloneable) cloneable.getClass().getMethod("clone", (Class[]) null)
                               .invoke(cloneable, (Object[]) null);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to clone " + cloneable + " due to "
                    + exception.getClass().getSimpleName() + ": " + exception.getMessage());
        }
        
    }

}
