/*
 * @(#)DelegatingCollectionCloner.java     9 Feb 2009
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
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.util.IdentitySet;

import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityGraphCloner;
import com.qrmedia.commons.persistence.hibernate.clone.wiring.AbstractPropertyModifyingCommand;
import com.qrmedia.commons.persistence.hibernate.clone.wiring.GraphPostProcessingCommand;

/**
 * A &quot;catch-all&quot; cloner for collection bean properties.
 * <p>
 * The members of the collection are added to the list of objects to be cloned, and 
 * commands created to then wire add them to the collection property of the clone of the 
 * source object.
 * <p>
 * <b>Note:</b> Will <u>not</u> work if the source collection is not an equals-based
 * <code>Set</code> that contains multiple equal (but not identical) entities. But then,
 * a non-equals-based set violates the set contract, in any case.
 * 
 * @author anph
 * @see SimplePropertyCloner
 * @see SimpleCollectionCloner 
 * @see CloneablePropertyCloner
 * @see DelegatingPropertyCloner
 * @since 9 Feb 2009
 *
 */
/**
 *
 * @author anph
 * @since 18 Feb 2009
 *
 */
public class DelegatingCollectionCloner extends AbstractValueAwareCollectionCloner {

    /* (non-Javadoc)
     * @see com.qrmedia.commons.beans.clone.property.AbstractValueAwareCollectionCloner#cloneCollection(java.lang.Object, java.lang.Object, java.lang.String, java.util.Collection, com.qrmedia.commons.beans.clone.HibernateEntityGraphCloner)
     */
    @Override
    protected <T> Collection<T> cloneCollection(Object source, Object target,
            String propertyName, Collection<T> sourceCollection,
            HibernateEntityGraphCloner entityGraphCloner)
            throws IllegalArgumentException {
        /*
         * All members of the collection need to be queued for cloning, and
         * need to be wired up later.
         */
        for (T member : sourceCollection) {
            entityGraphCloner.addEntity(member);
            entityGraphCloner.addGraphWiringCommand(
                    new AddToCollectionCommand(target, propertyName, member));
        }
            
        /*
         * If the original collection was a set, the provisional target collection
         * will be an IdentitySet. Using any equals-based set could mean that two
         * objects that are temporarily equal until they are fully wired up could
         * not co-exist in the same set.
         * The IdentitySet is replaced with a "normal" set once the wiring-up
         * process has completed. 
         * 
         * Note that there cannot be any "overshoot" (i.e. having *more* entities 
         * in the result graph that somehow survive because of the IdentitySet)
         * because, if the set in the original graph contained distinct entities,
         * these entities *must* have been different w.r.t. equals.
         */
        
        if (sourceCollection instanceof Set) {
            entityGraphCloner.addGraphPostProcessingCommand(new SetReplacementCommand<T>(
                    target, propertyName, 
                    AbstractValueAwareCollectionCloner.newCollectionInterfaceInstance(
                                    (Set<T>) sourceCollection)));
        }
            
        return newCollectionInterfaceInstance(sourceCollection);
    }

    /**
     * As {@link com.qrmedia.commons.persistence.hibernate.clone.property.AbstractValueAwareCollectionCloner#newCollectionInterfaceInstance(Collection) 
     * AbstractValueAwareCollectionCloner#newCollectionInterfaceInstance(Collection)},
     * but returns an {@link IdentitySet} for {@link Set} instances passed.
     *  
     * @param <E>   the type of the collection
     * @param <T>   the type of the elements of the collection
     * @param collection    the collection for which a new instance is required 
     * @return  a new, empty instance of the same runtime &quot;interface type&quot;
     *          as the given collection (<code>IdentitySets</code> for <code>Sets</code>)
     */
    @SuppressWarnings("unchecked")
    protected static <E, T extends Collection<E>> T newCollectionInterfaceInstance(
            T collection) {
        
        if (collection instanceof Set) {
            return (T) new IdentitySet();
        } else {
            return AbstractValueAwareCollectionCloner.newCollectionInterfaceInstance(collection);
        }

    }   
    
    /**
     * Contributes to the &quot;wiring up&quot; of a collection property by adding the 
     * clone of the original member to the collection.
     * 
     * @author anph
     * @since 9 Feb 2009
     *
     */
    static class AddToCollectionCommand extends AbstractPropertyModifyingCommand {
        
        /**
         * Creates a <code>AddToCollectionCommand</code>.
         * 
         * @param target    the object whose collection property should be wired up
         * @param propertyName  the name of the collection bean property to set
         * @param originalMember the original member of the collection
         */
        AddToCollectionCommand(Object target, String propertyName,
                Object originalMember) {
            super(target, propertyName, originalMember);
        }

        /* (non-Javadoc)
         * @see com.tomtom.delphi.util.HibernateEntityBeanCloner.AbstractPropertyModifyingCommand#wireUpProperty(java.lang.Object, java.lang.String, java.lang.Object)
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void wireUpProperty(Object target, String propertyName,
                Object originalEntityClone) {
            Object collectionProperty;
            
            try {
                collectionProperty = PropertyUtils.getSimpleProperty(target, propertyName);
            } catch (Exception exception) {
                throw new AssertionError("Unable to get property '" + propertyName + "' of "
                                         + target + "' due to: " + exception.getMessage());
            }
                
            if (!(collectionProperty instanceof Collection)) {
                throw new AssertionError("Property '" + propertyName + "' of object "
                                         + target + " is not a collection");
            }
            
            /*
             * It would be nice to be able to specify a type parameter for the collection,
             * but it's only known at runtime. Collection<Object> won't work because it may,
             * for instance, be a Collection<ContentItem>!
             */
            ((Collection) collectionProperty).add(originalEntityClone);
        }
        
    }
    
    /**
     * Replaces the property of the given name on the specified object, which is
     * assumed to be a <code>Set</code> instance, by another set.
     * <p>
     * All the elements of the old set are added to the replacement first.
     * 
     * @param <T>   the type of the elements of the original (and thus replacement) sets
     * @author anph
     * @since 18 Feb 2009
     */
    static class SetReplacementCommand<T> implements GraphPostProcessingCommand {
        private final Object target;
        private final String propertyName;
        private final Set<T> replacement;
        
        /**
         * Creates a <code>SetReplacementCommand</code>.
         * 
         * @param target    the object whose set is to be replaced
         * @param propertyName  the name of the bean property of the set to be replaced
         * @param replacement   the set to replace the original
         */
        SetReplacementCommand(Object target, String propertyName, Set<T> replacement) {
            this.target = target;
            this.propertyName = propertyName;
            this.replacement = replacement;
        }
        
        /* (non-Javadoc)
         * @see com.qrmedia.commons.beans.clone.wiring.GraphPostProcessingCommand#execute()
         */
        public void execute() {
            Object setProperty; 
            
            try {
                setProperty = PropertyUtils.getSimpleProperty(target, propertyName);
            } catch (Exception exception) {
                throw new AssertionError("Unable to get property '" + propertyName + "' of "
                                         + target + "' due to: " + exception.getMessage());
            }
                
            if (!(setProperty instanceof IdentitySet)) {
                throw new AssertionError("Property '" + propertyName + "' of object "
                                         + target + " is not an IdentitySet");
            }
            
            // IdentitySet's elements are IdentityMapEntry objects, so plain addAll doesn't work
            addAll(replacement, (IdentitySet) setProperty);
            
            try {
                PropertyUtils.setSimpleProperty(target, propertyName, replacement);
            } catch (Exception exception) {
                throw new AssertionError("Unable to set property '" + propertyName + "' of "
                        + target + "' due to: " + exception.getMessage());
            }
            
        }
        
        @SuppressWarnings("unchecked")
        private static <T> void addAll(Set<T> target, IdentitySet source) {
            
            // IdentitySet's elements are IdentityMapEntry objects, whose key is the set member
            for (Iterator<Entry<T, ?>> iterator = source.iterator(); 
                    iterator.hasNext();) {
                
                // assumes all the elements in the IdentitySet are instances of T
                target.add(iterator.next().getKey());
            }
            
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof SetReplacementCommand)) {
                return false;
            }

            SetReplacementCommand<T> other = (SetReplacementCommand<T>) obj;
            return new EqualsBuilder().append(target, other.target)
                   .append(propertyName, other.propertyName)
                   .append(replacement, other.replacement).isEquals();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(target).append(propertyName)
                   .append(replacement).toHashCode();
        }        
        
    }
                                                                                      
}
