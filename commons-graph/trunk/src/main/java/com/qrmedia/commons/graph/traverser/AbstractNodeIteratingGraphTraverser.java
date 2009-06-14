/*
 * @(#)AbstractNodeIteratingGraphTraverser.java     20 Feb 2009
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ListUtils;

/**
 * A {@link GraphTraverser} that visits nodes in the order returned by an <code>Iterator</code>,
 * which subclasses should implement.
 * 
 * @param <T> the type of graph nodes
 * @param <U> the type of the state-maintaining object 
 * @author anph
 * @since 20 Feb 2009
 */
public abstract class AbstractNodeIteratingGraphTraverser<T, U> implements GraphTraverser<T, U>, Iterator<T> {
    /**
     * A list of the visitors to be called (in order of insertion!) for each node.
     */
    protected final List<NodeVisitor<T, U>> nodeVisitors = new ArrayList<NodeVisitor<T, U>>();
    
    /**
     * A set of visited and queued items to ensure items are processed only once.
     * <p>
     * Accessible to subclasses because these might wish to provide different set
     * implementations depending on their semantics, e.g. a set based on object
     * identity rather than equality. 
     */
    protected Set<T> visitedOrQueuedNodes = new HashSet<T>();

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public abstract boolean hasNext();

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public abstract T next();

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException("Nodes cannot be removed from the traverser");
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.GraphTraverser#traverseFrom(java.lang.Object, java.lang.Object)
     */
    public boolean traverseFrom(T startNode, U traversalState) {
        
        // Arrays.asList returns a stub ArrayList that does not support all operations!
        return traverseFrom(asList(startNode), traversalState);
    }
    
    protected static <T> List<T> asList(T item) {
        List<T> items = new ArrayList<T>(1);
        
        items.add(item);
        return items;
    }    

    /** 
     * See {@link GraphTraverser#traverseFrom(Collection, Object)}.
     * <p>
     * The order in which the starting nodes are visited is <u>not</u> guaranteed.
     *  
     * @param startNodes  the nodes to start traversal from
     * @param traversalState    an object representing the traversal's state
     * @return  <code>false</code> iff graph traversal was aborted by one of the visitors
     * @see #traverseFrom(Object, Object)
     */
    public boolean traverseFrom(Collection<? extends T> startNodes, U traversalState) {
        addNode(startNodes);
        boolean retVal = traverseGraph(traversalState);
        
        // clean up internal state
        visitedOrQueuedNodes.clear();
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.GraphTraverser#addNode(T)
     */
    public void addNode(T node) {
        
        // Arrays.asList returns a stub ArrayList that does not support all operations!
        addNode(asList(node));
    }
    
    /**
     * See {@link GraphTraverser#addNode(Collection)}. 
     * <p>
     * The order in which these nodes will be visited is <u>not</u> guaranteed - they will,
     * of course, be visited after any node(s) registered in previous <code>addNode</code> calls,
     * and before any queued in subsequent calls.
     *  
     * @param nodes  the node to be queued for visiting
     * @see #addNode(Object)
     */
    @SuppressWarnings("unchecked")
    public void addNode(Collection<? extends T> nodes) {
        /*
         * Strip out all the nodes that have already been visited, *without* modifying the 
         * original. Can't call CollectionUtils.removeAll as the 3.2.1 version contains a bug!
         */
        Collection<T> unseenNodes = 
            (Collection<T>) ListUtils.removeAll(nodes, visitedOrQueuedNodes);

        if (!unseenNodes.isEmpty()) {
            visitedOrQueuedNodes.addAll(unseenNodes);
            enqueueNodes(unseenNodes);
        }
        
    }
    
    /**
     * Adds the given nodes to the set still to be visited.
     * <p>
     * The nodes are guaranteed to be unseen, i.e. previously unvisited and not yet
     * enqueued.
     * 
     * @param nodes the nodes to be enqueued for visiting
     */
    protected abstract void enqueueNodes(Collection<? extends T> nodes);
    
    // visits all the queued nodes
    private boolean traverseGraph(U traversalState) {
        
        // keep visiting queued items until none remain
        while (hasNext()) {
            T node = next();
            
            // pass the node to all the registered visitors
            for (NodeVisitor<T, U> visitor : nodeVisitors) {
                
                // a visitor may abort the traversal by returning false
                if (!visitNode(visitor, node, traversalState)) {
                    
                    // indicate the the traversal was aborted
                    return false;
                }
                
            }
            
        }
        
        // indicate successful traversal
        return true;
    }
    
    /**
     * Calls a visitor to visit a given node, returning the visitor's result.
     * <p>
     * <b>Should be called by subclasses when calling a visitor, rather than
     * executing <code>visitor.visitNode</code> directly.</b>
     * <p>
     * Intended as an extension point for subclasses that wish to perform additional
     * logic at this point, e.g. logging.

     * @param visitor   the visitor that should be called to visit the node
     * @param node  the node to be visited
     * @param traversalState the traversal state to be passed to the visitor
     * @return  the result of the call to the visitor's {@link NodeVisitor#visitNode(Object, GraphTraverser, Object)
     *          visitNode} method
     */
    protected boolean visitNode(NodeVisitor<T, U> visitor, T node, U traversalState) {
        return visitor.visitNode(node, this, traversalState);
    }
    
    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.GraphTraverser#addNodeVisitor(com.qrmedia.commons.graph.NodeVisitor)
     */
    public void addNodeVisitor(NodeVisitor<T, U> nodeVisitor) {
        nodeVisitors.add(nodeVisitor);
    }

}
