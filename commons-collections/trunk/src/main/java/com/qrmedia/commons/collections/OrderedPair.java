/*
 * @(#)OrderedPair.java     29 Aug 2008
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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A <code>Pair</code> whose equivalence respects the order of the two items.
 * 
 * @author anph
 * @since 29 Aug 2008
 *
 */
public class OrderedPair<U, V> extends Pair<U, V> {

    /**
     * Creates a ordered pair consisting of two objects.
     * 
     * @param firstObject    the first object of the pair
     * @param secondObject  the second object of the pair
     */
    public OrderedPair(U firstObject, V secondObject) {
        super(firstObject, secondObject);
    }
    
    /* (non-Javadoc)
     * @see com.tomtom.commons.collections.Pair#equals(java.lang.Object)
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
        
        return new EqualsBuilder().append(firstObject, other.firstObject)
               .append(secondObject, other.secondObject).isEquals();
    }

    /* (non-Javadoc)
     * @see com.tomtom.commons.collections.Pair#hashCode()
     */
    @Override
    public int hashCode() {
        
        // HashCodeBuilder is order-sensitive
        return new HashCodeBuilder().append(firstObject).append(secondObject).toHashCode();
    }

    /* (non-Javadoc)
     * @see com.tomtom.commons.collections.Pair#toString()
     */
    @Override
    public String toString() {
        
        // use "[" rather than the parent's "{" to hopefully indicate ordering
        return "[" + getPairStringRepresentation() + "]";
    }    

}
