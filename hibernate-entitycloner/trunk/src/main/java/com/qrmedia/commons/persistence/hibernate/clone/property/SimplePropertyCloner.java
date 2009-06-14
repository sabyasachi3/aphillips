/*
 * @(#)SimplePropertyCloner.java     9 Feb 2009
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
import com.qrmedia.commons.persistence.hibernate.clone.property.classifier.DefaultPropertyClassifier;
import com.qrmedia.commons.persistence.hibernate.clone.property.classifier.PropertyClassifier;

/**
 * Clones a &quot;simple&quot; (as defined by {@link PropertyClassifier#isSimpleProperty(Class)}) 
 * or <code>null</code> bean property.
 * <p>
 * The property must have an accessible getter and setter.
 * 
 * @author anph
 * @see SimpleCollectionCloner
 * @see DelegatingCollectionCloner 
 * @see CloneablePropertyCloner
 * @see DelegatingPropertyCloner
 * @since 9 Feb 2009
 *
 */
public class SimplePropertyCloner extends AbstractValueAwarePropertyCloner {
    private PropertyClassifier propertyClassifier = new DefaultPropertyClassifier();

    /* (non-Javadoc)
     * @see com.qrmedia.commons.beans.clone.property.AbstractValueAwarePropertyCloner#cloneValue(java.lang.Object, java.lang.Object, java.lang.String, java.lang.Object, com.qrmedia.commons.beans.clone.HibernateEntityGraphCloner)
     */
    @Override
    protected <T> boolean cloneValue(Object source, Object target, String propertyName,
            Object propertyValue, HibernateEntityGraphCloner entityGraphCloner) 
            throws IllegalArgumentException {
        
        // null properties are always be treated as simple
        if ((propertyValue == null) 
                || propertyClassifier.isSimpleProperty(propertyValue.getClass())) {
            
            try {
                PropertyUtils.setSimpleProperty(target, propertyName, propertyValue);
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

    /* Getter(s) and setter(s) */
    
    /**
     * @param propertyClassifier the propertyClassifier to set
     */
    public void setPropertyClassifier(PropertyClassifier propertyClassifier) {
        this.propertyClassifier = propertyClassifier;
    }

}