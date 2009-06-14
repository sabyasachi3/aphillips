/*
 * @(#)SimpleCollectionCloner.java     9 Feb 2009
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

import java.util.Collection;

import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityGraphCloner;
import com.qrmedia.commons.persistence.hibernate.clone.property.classifier.DefaultPropertyClassifier;
import com.qrmedia.commons.persistence.hibernate.clone.property.classifier.PropertyClassifier;

/**
 * Clones a collection bean property of &quot;simple&quot; objects (as defined by 
 * {@link PropertyClassifier#isSimpleProperty(Class)}), and empty collections
 * of all types. 
 * <p>The first object in the collection, if non-empty, is used to determine 
 * whether the collection is simple or not.
 * 
 * @author anph
 * @see SimplePropertyCloner
 * @see DelegatingCollectionCloner 
 * @see CloneablePropertyCloner
 * @see DelegatingPropertyCloner
 * @since 9 Feb 2009
 *
 */
public class SimpleCollectionCloner extends AbstractValueAwareCollectionCloner {
    private PropertyClassifier propertyClassifier = new DefaultPropertyClassifier();
    
    /* (non-Javadoc)
     * @see com.qrmedia.commons.beans.clone.property.AbstractValueAwareCollectionCloner#cloneCollection(java.lang.Object, java.lang.Object, java.lang.String, java.util.Collection, com.qrmedia.commons.beans.clone.HibernateEntityGraphCloner)
     */
    @Override
    protected <T> Collection<T> cloneCollection(Object source, Object target,
            String propertyName, Collection<T> sourceCollection,
            HibernateEntityGraphCloner entityGraphCloner)
            throws IllegalArgumentException {

        if (sourceCollection.isEmpty() || propertyClassifier.isSimpleProperty(
                sourceCollection.iterator().next().getClass())) {
            Collection<T> collectionClone = newCollectionInterfaceInstance(sourceCollection);
            collectionClone.addAll(sourceCollection);
            return collectionClone;
        }
        
        return null;
    }

    /* Getter(s) and setter(s) */
    
    /**
     * @param propertyClassifier the propertyClassifier to set
     */
    public void setPropertyClassifier(PropertyClassifier propertyClassifier) {
        this.propertyClassifier = propertyClassifier;
    }    

}