/*
 * @(#)EntityPreserveIdPair.java     23 Feb 2009
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
package com.qrmedia.commons.persistence.hibernate.clone;

import com.qrmedia.commons.collections.Pair;


/**
 * An entity and a flag indicating whether IDs should be preserved during cloning
 * for that entity (and related entities).
 * <p>
 * Equality is based on <u>identity</u> of the entity and <u>equality</u> of the flag, i.e.
 * <code>[entity1, flag1].equals([entity2, flag2])</code> iff <code>entity1 == entity2</code>
 * and <code>flag1.equals(flag2)</code>.
 * 
 * @author anph
 * @since 23 Feb 2009
 *
 */
public final class EntityPreserveIdFlagPair extends Pair<Object, Boolean> {

    /**
     * Constructs an <code>EntityPreserveIdFlagPair</code>
     * 
     * @param entity    the entity
     * @param preserveId    the flag indicating if IDs are to be preserved
     * @throws IllegalArgumentException if the entity or preserve ID flag is null
     */
    public EntityPreserveIdFlagPair(Object entity, Boolean preserveId) {
        super(entity, preserveId);
        
        if ((entity == null) || (preserveId == null)) {
            throw new IllegalArgumentException("All arguments must not be null");
        }
        
    }
    
    /* (non-Javadoc)
     * @see com.tomtom.commons.collections.Pair#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof EntityPreserveIdFlagPair)) {
            return false;
        }
        
        EntityPreserveIdFlagPair other = (EntityPreserveIdFlagPair) obj;
        
        // based on entity *identity* and flag equality
        return (firstObject == other.firstObject) && (secondObject.equals(other.secondObject));
    }

    /* (non-Javadoc)
     * @see com.tomtom.commons.collections.Pair#hashCode()
     */
    @Override
    public int hashCode() {
        
        // based on entity *identity*        
        return System.identityHashCode(firstObject) * 31 + secondObject.hashCode();
    }    
    
    /**
     * @return  the entity
     */
    public Object getEntity() {
        return firstObject;
    }
    
    /**
     * @return  the preserveId flag
     */
    public boolean isPreserveId() {
        
        // cannot be null unless some reflection magic is used
        return secondObject.booleanValue();
    }

}
