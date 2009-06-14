/*
 * @(#)StatelessGraphTraverser.java     14 Aug 2008
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
package com.qrmedia.commons.graph.traverser.stateless;

import java.util.Collection;

import com.qrmedia.commons.graph.traverser.GraphTraverser;

/**
 * A <code>GraphTraverser</code> that does not maintain any traversal state.
 * <p>
 * Accepts node visitors of type <code>NodeVisitor&lt;T, Object&gt;</code>.
 * 
 * @param <T> the type of graph nodes
 * @author anph
 * @since 14 Aug 2008
 *
 */
public interface StatelessGraphTraverser<T> extends GraphTraverser<T, Object> {

    /**
     * Begins traversing the graph from the given starting node.
     * 
     * @param startNode  the node to start traversal from
     * @return  <code>false</code> iff graph traversal was aborted by one of the visitors
     */
    boolean traverseFrom(T startNode);
//    public boolean traverseFrom(T startNode) {
//        return traverseFrom(startNode, null);
//    }
    
    /**
     * Begins traversing the graph from the given starting nodes.
     * <p>
     * The order in which the starting nodes are visited is <u>not</u> guaranteed.
     *  
     * @param startNodes  the nodes to start traversal from
     * @return  <code>false</code> iff graph traversal was aborted by one of the visitors
     */
    boolean traverseFrom(Collection<? extends T> startNodes);
//    public boolean traverseFrom(Collection<? extends T> startNodes) {
//        return traverseFrom(startNodes, null);
//    }
    
}
