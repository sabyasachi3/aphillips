/*
 * @(#)AbstractValueAwareCollectionCloner.java     10 Feb 2009
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.collection.PersistentCollection;

import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityGraphCloner;

/**
 * An {@link AbstractValueAwarePropertyCloner} that handles {@linkplain Collection Collections}.
 * <p>
 * Creates a copy of the runtime instance of the source collection (i.e. a <code>Set</code>
 * if the source is a <code>Set</code>, a <code>List</code> if a <code>List</code> etc.) and 
 * delegates <i>populating</i> the collection to implementations.
 * <p>
 * If the collection property was not ignored by this cloner (see 
 * {@link #cloneCollection(Object, Object, String, Collection, Collection, HibernateEntityGraphCloner)}, 
 * the cloned collection is written to the target's collection property. 
 * <p>
 * The copying is necessary because Hibernate hydrates objects with {@linkplain PersistentCollection 
 * PersistentCollections}, which &quot;remember&quot; the entity they were hydrated with.<br/>
 * Assigning such a collection to a different entity (i.e. 
 * <code>newEntity.setCollection(oldEntity.getCollection())</code>) and persisting the new
 * entity will remove the elements from <code>oldEntity.collection</code>!
 * <p>
 * Note that there is <u>no</u> guarantee that the actual collection implementation class 
 * of the target's property will be the same as the runtime class of the source, e.g. a 
 * <code>LinkedList</code> source may be replaced by an <code>ArrayList</code> in the target. 
 * 
 * @author anph
 * @since 10 Feb 2009
 *
 */
abstract class AbstractValueAwareCollectionCloner extends
        AbstractValueAwarePropertyCloner {

    /* (non-Javadoc)
     * @see com.qrmedia.commons.beans.clone.property.AbstractValueAwarePropertyCloner#clone(java.lang.Object, java.lang.Object, java.lang.String, java.lang.Object, com.qrmedia.commons.beans.clone.HibernateEntityGraphCloner)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected <T> boolean cloneValue(Object source, Object target, String propertyName,
            Object propertyValue, HibernateEntityGraphCloner entityGraphCloner)
            throws IllegalArgumentException {

        if (!(propertyValue instanceof Collection<?>)) {
            return false;
        }
        
        Collection<T> clonedCollection = cloneCollection(source, target, propertyName, 
                (Collection<T>) propertyValue, entityGraphCloner);
        boolean cloneSuccessful = (clonedCollection != null);
        
        /*
         * The cloned collection should only be linked to the target if the cloner
         * actually processed it!
         */
        if (cloneSuccessful) {
            
            try {
                PropertyUtils.setSimpleProperty(target, propertyName, clonedCollection);
            } catch (Exception exception) {
                throw new IllegalArgumentException("Unable to set collection '"
                        + propertyName + "' on " + target + " due to " 
                        + exception.getClass().getSimpleName() + ": " 
                        + exception.getMessage());
            }

        }
        
        return cloneSuccessful;
    }
    
    /**
     * Creates a new, empty instance of same runtime &quot;interface type&quot; as the 
     * given collection (e.g. if the collection is a <code>Set</code>, some new 
     * <code>Set</code> instance is generated etc.).
     * <p>
     * Currently supports {@link List Lists} and {@link Set Sets}.
     * 
     * @param <E>   the type of the collection
     * @param <T>   the type of the elements of the collection
     * @param collection    the collection for which a new instance is required 
     * @return  a new, empty instance of the same runtime &quot;interface type&quot;
     *          as the given collection
     * @throws IllegalArgumentException if an unsupported collection instance is passed
     */
    @SuppressWarnings("unchecked")
    protected static <E, T extends Collection<E>> T newCollectionInterfaceInstance(
            T collection) {

        if (collection instanceof List) {
            return (T) new ArrayList<E>();
        } else if (collection instanceof Set) {
            return (T) new HashSet<E>();
        } else {
            throw new IllegalArgumentException("Unsupported collection type: "
                    + collection.getClass());
        }

    }        
    
    /**
     * Clones the given property as described in 
     * {@link #clone(Object, Object, String, HibernateEntityGraphCloner)}, and
     * indicates if the content of the collection was dealt with.
     * 
     * @param <T>   the type of the elements of the source and target collections
     * @param source    the entity whose collection should be cloned
     * @param target    the entity to which the collection should be cloned
     * @param propertyName  the bean name of the collection
     * @param sourceCollection  the collection that should be copied
     * @param entityGraphCloner the entity graph cloner with which new objects to clone
     *                          should be queued etc.
     * @return a non-<code>null</code> collection iff the content of the source collection 
     *         could be processed, i.e. was not ignored. In this case, the returned
     *         collection will be linked to the target object.
     * @throws IllegalArgumentException if the collection cannot be copied
     */
    protected abstract <T> Collection<T> cloneCollection(Object source, Object target, 
            String propertyName, Collection<T> sourceCollection, 
            HibernateEntityGraphCloner entityGraphCloner)
            throws IllegalArgumentException;
}
