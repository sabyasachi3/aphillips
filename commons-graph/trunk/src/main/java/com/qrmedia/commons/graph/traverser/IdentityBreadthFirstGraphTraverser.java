/*
 * @(#)IdentityBreadthFirstGraphTraverser.java     13 Feb 2009
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
package com.qrmedia.commons.graph.traverser;

import java.util.IdentityHashMap;
import java.util.Set;

import org.hibernate.util.IdentitySet;

/**
 * A {@link BreadthFirstGraphTraverser} that uses object <i>identity</i> rather than
 * <i>equality</i> as the criterion for revisting objects. In other words, the
 * traverser will <u>revisit</u> items that are equal but not identical 
 * (<code>o1.equals(o2) && (o1 != o2)</code>). Identical objects are only visited
 * once (this does not hold for the objects initially passed).
 * <p>
 * Intended for the rare cases in which object identity is required, e.g. deep cloning.
 *  
 * @author anph
 * @since 13 Feb 2009
 *
 * @param <T> the type of graph nodes
 * @param <U> the type of the state-maintaining object
 * @see IdentityHashMap
 */
public class IdentityBreadthFirstGraphTraverser<T, U> extends BreadthFirstGraphTraverser<T, U> {

    /**
    * Creates an <code>IdentityBreadthFirstGraphTraverser</code>.
    */
    @SuppressWarnings("unchecked")
    public IdentityBreadthFirstGraphTraverser() {
        /*
         * Replace the set storing the list of visited nodes with a set
         * that uses identity, not equality semantics.
         */
        visitedOrQueuedNodes = (Set<T>) new IdentitySet();
    }
    
}
