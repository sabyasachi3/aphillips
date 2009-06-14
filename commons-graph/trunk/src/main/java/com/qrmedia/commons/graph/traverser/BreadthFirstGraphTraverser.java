/*
 * @(#)BreadthFirstGraphTraverser.java     13 Aug 2008
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
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * A {@link GraphTraverser} that crawls the discovered graph in a breadth-first (FIFO)
 * manner, i.e. nodes will be visited in the order they are added.
 * <p>
 * <b>N.B.:</b> Because this object maintains internal state, it is <u>not</u> thread-safe
 * &quot;out-of-the-box&quot;!
 * 
 * @param <T> the type of graph nodes
 * @param <U> the type of the state-maintaining object 
 * @author anph
 * @since 13 Aug 2008
 *
 */
public class BreadthFirstGraphTraverser<T, U> extends AbstractTracingNodeIteratingGraphTraverser<T, U> {
    /**
     * The active queue of nodes to be processed.
     */
    protected final Queue<T> nodeQueue = new LinkedList<T>();

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.AbstractTracingGraphTraverser#hasNext()
     */
    @Override
    public boolean hasNext() {
        return !nodeQueue.isEmpty();
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.AbstractTracingGraphTraverser#next()
     */
    @Override
    public T next() {
        
        if (nodeQueue.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        
        return nodeQueue.poll();
    }
    
    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.AbstractTracingGraphTraverser#enqueueNodes(java.util.Collection)
     */
    @Override
    protected void enqueueNodes(Collection<? extends T> nodes) {
        nodeQueue.addAll(nodes);
    }
    
}