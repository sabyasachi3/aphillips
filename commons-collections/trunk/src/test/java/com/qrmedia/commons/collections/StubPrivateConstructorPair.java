/*
 * @(#)StubPrivateConstructorPair.java     24 Feb 2009
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

/**
 * A pair object with a private two-argument constructor. Can be instantiated via
 * a no-argument constructor, and will then have <code>null</code> as first and second
 * objects. 
 * 
 * @author anph
 * @since 24 Feb 2009
 *
 */
public class StubPrivateConstructorPair extends Pair<Object, Object> {

    /**
     * Private constructor for <code>StubPrivateConstructorPair</code>.
     * 
     * @param firstObject   the first object of the pair
     * @param secondObject  the second object of the pair
     */
    private StubPrivateConstructorPair(Object firstObject, Object secondObject) {
        super(firstObject, secondObject);
    }
    
    /**
     * Accessible constructor for <code>StubPrivateConstructorPair</code>.
     */
    public StubPrivateConstructorPair() {
        this(null, null);
    }

}
