/*
 * @(#)Pair.java     28 Aug 2008
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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A pair of objects.
 * 
 * @param <U> the type of the first object
 * @param <V> the type of the second object
 * @author anph
 * @since 28 Aug 2008
 *
 */
public class Pair<U, V> {
    protected final U firstObject;
    protected final V secondObject;
    
    /**
     * Creates a pair consisting of two objects.
     * 
     * @param firstObject    the first object of the pair
     * @param secondObject  the second object of the pair
     */
    public Pair(U firstObject, V secondObject) {
        this.firstObject = firstObject;
        this.secondObject = secondObject;
    } 

    /* copy "constructors" */
    
    /**
     * Creates a copy of this pair.
     *
     * @return a copy of this pair
     */
    public Pair<U, V> copy() {
        return new Pair<U, V>(firstObject, secondObject);
    }

    /**
     * Creates a pair using the second object of this pair and the
     * new first object.
     * 
     * @param newFirstObject the new first object
     * @return a pair consisting of the new first object and the second object of this
     *  pair
     */
    public Pair<U, V> copyKeepSecond(U newFirstObject) {
        return new Pair<U, V>(newFirstObject, secondObject);
    }

    /**
     * Creates a pair using the first object of this pair and the
     * new second object.
     * 
     * @param newSecondObject the new second object
     * @return a pair consisting of the first object of this pair and the new second
     *  object
     */
    public Pair<U, V> copyKeepFirst(V newSecondObject) {
        return new Pair<U, V>(firstObject, newSecondObject);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Pair)) {
            return false;
        }

        Pair<?, ?> other = (Pair<?, ?>) obj;
        
        // the pair is *not* ordered!
        return ((ObjectUtils.equals(firstObject, other.firstObject) 
                 && ObjectUtils.equals(secondObject, other.secondObject))
                || (ObjectUtils.equals(firstObject, other.secondObject) 
                    && ObjectUtils.equals(secondObject, other.firstObject)));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        
        // HashCodeBuilder is order-sensitive, so the objects of the pair must be "ordered" too
        boolean firstObjectHashCodeGreater = (hashCode(firstObject) > hashCode(secondObject));
        return new HashCodeBuilder()        
               .append(firstObjectHashCodeGreater ? firstObject : secondObject)
               .append(firstObjectHashCodeGreater ? secondObject : firstObject).toHashCode();
    }
    
    private int hashCode(Object object) {
        return ((object != null) ? object.hashCode() : 0);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{" + getPairStringRepresentation() + "}";
    }
    
    protected String getPairStringRepresentation() {
        
        // Doh! *Why* is this (String) cast necessary??
        return toString(firstObject) + ", " + toString(secondObject);
    }
    
    private String toString(Object object) {
        return ((object != null) ? object.toString() : "null");
    }

    /* Getter(s) and setter(s) */

    /**
     * Getter for firstObject.
     *
     * @return the firstObject.
     */
    public U getFirstObject() {
        return firstObject;
    }

    /**
     * Getter for secondObject.
     *
     * @return the secondObject.
     */
    public V getSecondObject() {
        return secondObject;
    }
    
}
