/*
 * @(#)PairUtils.java     24 Feb 2009
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
package com.qrmedia.commons.collections;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;

import com.qrmedia.commons.lang.ClassUtils;

/**
 * Utility methods for {@link Pair Pairs}.
 * 
 * @author anph
 * @since 24 Feb 2009
 *
 */
public final class PairUtils {

    /**
     * Creates pairs by &quot;pairing-up&quot; each object in a collection with the same
     * partner object.
     * 
     * @param <U>   the type of the first object in the pair
     * @param <V>   the type of the second object in the pair
     * @param firstObjects  the objects to become the first objects in the pairs
     * @param secondObject  the object to be paired with all the <code>firstObjects</code>
     * @return a collection of pairs containing an entry in <code>firstObjects</code> and 
     *         <code>secondObject</code>
     * @see #toPairs(Class, Collection, Object)        
     */
    public static <U, V> Collection<Pair<U, V>> toPairs(Collection<? extends U> firstObjects, 
            V secondObject) {
        return toPairsInternal(null, firstObjects, secondObject);
    }
    
    /**
     * Creates pairs of the given pair subclass by &quot;pairing-up&quot; each object in a 
     * collection with the same partner object.
     * <p>
     * Expects the subclass to have a two-argument constructor, taking the first
     * and second objects as arguments. The constructor does <u>not</u> need to be visible.
     * 
     * @param <T>   the type of the pair subclass to be returned
     * @param <U>   the type of the first object in the pair
     * @param <V>   the type of the second object in the pair
     * @param firstObjects  the objects to become the first objects in the pairs
     * @param secondObject  the object to be paired with all the <code>firstObjects</code>
     * @return a collection of pairs containing an entry in <code>firstObjects</code> and 
     *         <code>secondObject</code>
     * @throws IllegalArgumentException 
     *              if <code>pairClass</code> is <code>null</code> or if the given subclass cannot 
     *              be instantiated using the expected two-argument constructor
     * @see #toPairs(Collection, Object)
     */
    public static <T extends Pair<U, V>, U, V> Collection<T> toPairs(Class<T> pairClass,
            Collection<? extends U> firstObjects, V secondObject) {
        
        if (pairClass == null) {
            throw new IllegalArgumentException("'pairClass' may not be null");
        }
        
        return toPairsInternal(pairClass, firstObjects, secondObject);
    }    
    
    private static <T extends Pair<U, V>, U, V> Collection<T> toPairsInternal(Class<T> pairClass,
            Collection<? extends U> firstObjects, V secondObject) {
        
        if (firstObjects == null) {
            return new ArrayList<T>();
        }
        
        Collection<T> pairs =  new ArrayList<T>(firstObjects.size());
        
        for (U firstObject : firstObjects) {
            
            try {
                pairs.add(toPair(pairClass, firstObject, secondObject));
            } catch (InstantiationException exception) {
                throw new IllegalArgumentException(exception.getMessage());
            }
            
        }
        
        return pairs;
    }    
    
    //if pairClass is null a "normal" pair will be generated
    @SuppressWarnings("unchecked")
    private static <T extends Pair<U, V>, U, V> T toPair(Class<T> pairClass, U firstObject, 
            V secondObject) throws InstantiationException {
        
        if (pairClass == null) {
            
            // in this case, T == Pair<U, V>
            return (T) new Pair<U, V>(firstObject, secondObject);
        }
        
        // attempt to instantiate the given class, expecting one two-argument constructor
        try {
            return getPairConstructor(pairClass).newInstance(firstObject, secondObject);
        } catch (Exception exception) {
            
            /*
             * Convert all of the possible exceptions (Instantiation, NPE if no constructor exists
             * etc. to an InstantiationException.
             */
            throw new InstantiationException("Unable to instantiate instance of "
                    + pairClass.getSimpleName() + " due to " + exception.getClass().getSimpleName() 
                    + ": " + exception.getMessage());
        }
        
    }    

    //@SuppressWarnings("unchecked")
    private static <T extends Pair<?, ?>> Constructor<T> getPairConstructor(Class<T> pairClass) 
            throws NoSuchMethodException {
        /*
         * Will throw a NoSuchMethodException if one of the pair types could not be resolved, and
         * if no matching constructor can be found, of course.
         */
        Constructor<T> constructor = pairClass.getDeclaredConstructor(
                ClassUtils.getActualTypeArguments(pairClass, Pair.class).toArray(new Class<?>[] {}));
        
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        
        return constructor;
    }
    
}
