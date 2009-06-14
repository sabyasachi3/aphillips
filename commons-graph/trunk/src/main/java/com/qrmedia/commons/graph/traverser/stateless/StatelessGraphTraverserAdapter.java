/*
 * @(#)StatelessNodeIteratingGraphTraverserAdapter.java     20 Feb 2009
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
import com.qrmedia.commons.graph.traverser.NodeVisitor;

/**
 * An adapter that implements <code>StatelessGraphTraverser</code> by
 * decorating a {@link GraphTraverser}.
 * 
 * @param <T> the type of graph nodes
 * @author anph
 * @since 20 Feb 2009
 *
 */
public class StatelessGraphTraverserAdapter<T> implements
        StatelessGraphTraverser<T> {
    private final GraphTraverser<T, Object> backingTraverser;
    
    /**
     * Creates a <code>StatelessGraphTraverserAdapter</code> for the given
     * backing traverser.
     * 
     * @param backingTraverser  the <code>GraphTraverser</code> to be made stateless
     */
    public StatelessGraphTraverserAdapter(GraphTraverser<T, Object> backingTraverser) {
        this.backingTraverser = backingTraverser;
    }
    
    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.stateless.StatelessGraphTraverser#traverseFrom(java.lang.Object)
     */
    public boolean traverseFrom(T startNode) {
        return traverseFrom(startNode, null);
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.GraphTraverser#traverseFrom(java.lang.Object, java.lang.Object)
     */
    public boolean traverseFrom(T startNode, Object traversalState) {
        return backingTraverser.traverseFrom(startNode, null);
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.stateless.StatelessGraphTraverser#traverseFrom(java.util.Collection)
     */
    public boolean traverseFrom(Collection<? extends T> startNodes) {
        return traverseFrom(startNodes, null);
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.GraphTraverser#traverseFrom(java.util.Collection, java.lang.Object)
     */
    public boolean traverseFrom(Collection<? extends T> startNodes,
            Object traversalState) {
        return backingTraverser.traverseFrom(startNodes, traversalState);
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.GraphTraverser#addNode(java.lang.Object)
     */
    public void addNode(T node) {
        backingTraverser.addNode(node);
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.GraphTraverser#addNode(java.util.Collection)
     */
    public void addNode(Collection<? extends T> nodes) {
        backingTraverser.addNode(nodes);
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.GraphTraverser#addNodeVisitor(com.qrmedia.commons.graph.traverser.NodeVisitor)
     */
    public void addNodeVisitor(NodeVisitor<T, Object> nodeVisitor) {
        /*
         * Passing the visitor through "unwrapped" means it will be called by the backing
         * traverser *directly*, and thus the graphTraverser argument to visitNode will
         * not be *this* instance. 
         * However, since this is the executing traverser, it should be the object passed
         * to the visitors, not the backing traverser,
         */
        backingTraverser.addNodeVisitor(
                new TraverserSubstitutingNodeVisitorDelegator(nodeVisitor));
    }
    
    /**
     * &quot;Wraps&quot; a {@link NodeVisitor} in order to be able to intercept
     * the graph traverser passed to the <code>visitNode</code> method and replace
     * it with the traverser represented by this enclosing {@link StatelessGraphTraverserAdapter}
     * instance.
     * 
     * @author anph
     * @since 20 Feb 2009
     *
     */
    private class TraverserSubstitutingNodeVisitorDelegator implements NodeVisitor<T, Object> {
        private final NodeVisitor<T, Object> targetVisitor;
        
        private TraverserSubstitutingNodeVisitorDelegator(NodeVisitor<T, Object> targetVisitor) {
            this.targetVisitor = targetVisitor;
        }
        
        /* (non-Javadoc)
         * @see com.qrmedia.commons.graph.traverser.NodeVisitor#visitNode(java.lang.Object, com.qrmedia.commons.graph.traverser.GraphTraverser, java.lang.Object)
         */
        public boolean visitNode(T node,
                GraphTraverser<T, Object> graphTraverser, Object traversalState) {
            return targetVisitor.visitNode(node, StatelessGraphTraverserAdapter.this, 
                    traversalState);
        }
        
    }

}