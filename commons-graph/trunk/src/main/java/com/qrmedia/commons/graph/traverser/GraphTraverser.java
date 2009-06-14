/*
 * @(#)GraphTraverser.java     20 Feb 2009
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

import java.util.Collection;

import org.apache.commons.collections.iterators.ObjectGraphIterator;

/**
 * &quot;Crawls&quot; a dynamically-discovered graph, starting from a given root or roots,
 * and passing each node to all registered {@link NodeVisitor NodeVisitors}.
 * <p>
 * The visitors may &quot;discover&quot; additional nodes, and register these with the
 * traverser. The order in which the nodes are visited is up to implementing classes.
 * <p>
 * Nodes will only be visited once, i.e. if a node is added that is equal to (in the sense of
 * Java's <code>equals</code> method) a node that has already been or is waiting to be visited,
 * it will not be scheduled again.<br/>
 * This does not apply to the <i>initial</i> nodes, all of which will be visited.
 * <p>
 * Traversal of the graph stops once no further nodes are queued to be visited, or if one of
 * the visitors explicitly aborts graph traversal.
 * <p>
 * An object representing the traversal's state is initially passed in by the caller, and,
 * if used, is modified by visitors as appropriate. For instance, this might be a collection
 * of errors.
 * <p>
 * If no traversal state is required, <code>null</code> may be passed.
 * <p>
 * Offers similar functionality to the {@link ObjectGraphIterator}, which may be a useful
 * alternative if this interface does not quite suit.
 * 
 * @param <T> the type of graph nodes
 * @param <U> the type of the state-maintaining object 
 * @author anph
 * @see ObjectGraphIterator
 * @since 13 Aug 2008
 *
 */
public interface GraphTraverser<T, U> {

    /**
     * Begins traversing the graph from the given starting node.
     * <p>
     * If the traversal's state is being recorded, the state object will 
     * have been appropriately modified once the method returns. 
     * 
     * @param startNode  the node to start traversal from
     * @param traversalState    an object representing the traversal's state
     * @return  <code>false</code> iff graph traversal was aborted by one of the visitors
     * @see #traverseFrom(Collection, Object)
     */
    boolean traverseFrom(T startNode, U traversalState);

    /**
     * Begins traversing the graph from the given starting nodes.
     * <p>
     * If the traversal's state is being recorded, the state object will 
     * have been appropriately modified once the method returns.
     *  
     * @param startNodes  the nodes to start traversal from
     * @param traversalState    an object representing the traversal's state
     * @return  <code>false</code> iff graph traversal was aborted by one of the visitors
     * @see #traverseFrom(Object, Object)
     */
    boolean traverseFrom(Collection<? extends T> startNodes, U traversalState);

    /**
     * Registers a node to be visited during further graph traversal.
     * <p>
     * Nodes that have already been visited, or are queued, will not be visited again, so
     * the caller does not need to check for this to prevent looping.
     * 
     * @param node  the node to be queued for visiting
     * @see #addNode(Collection)
     */
    void addNode(T node);

    /**
     * Registers nodes to be visited during further graph traversal. 
     * <p>
     * Nodes that have already been visited, or are queued, will not be visited again, so
     * the caller does not need to check for this to prevent looping.
     * 
     * @param nodes  the node to be queued for visiting
     * @see #addNode(Object)
     */
    void addNode(Collection<? extends T> nodes);

    /**
     * Registers a node visitor, which will be called for each node.
     * <p>
     * Visitors will called in the order they are registered.
     * 
     * @param nodeVisitor    the visitor to be registered
     */
    void addNodeVisitor(NodeVisitor<T, U> nodeVisitor);
}