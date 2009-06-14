/*
 * @(#)AbstractTracingGraphTraverser.java     20 Feb 2009
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

import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.qrmedia.commons.graph.traverser.stateless.StatelessGraphTraverserAdapter;
import com.qrmedia.commons.log.LoggingUtils;

/**
 * A &quot;plug-in&quot; subclass of {@link AbstractNodeIteratingGraphTraverser} that
 * transparently adds method logging.
 * <p>
 * The logging is transparent in the sense that subclasses of this class can be made direct 
 * subclasses of the &quot;real&quot; parent {@link AbstractNodeIteratingGraphTraverser} 
 * without any code changes. Apart from the <code>extends</code> definition, of course!
 * 
 * @author anph
 * @since 20 Feb 2009
 *
 */
public abstract class AbstractTracingNodeIteratingGraphTraverser<T, U> extends AbstractNodeIteratingGraphTraverser<T, U> {
    private static final Field VISITED_OR_QUEUED_NODES_FIELD;

    /*
     * Strictly speaking, it is bad that this class knows or makes guesses about the implementation
     * details of subclasses. However, since the purpose of this class is simply to enable
     * logging, and since it should be transparently removable, usefulness trumps design
     * considerations in this case.
     */
    private static final Field NODE_QUEUE_FIELD;
    
    private static final Field BACKING_TRAVERSER_FIELD;
    
    private static final String FIELD_RETRIEVAL_ERROR_MSG = "<error>";
    
    /**
     * Private because this class is intended to be transparent, and subclasses shouldn't
     * depend on properties declared here.
     */
    private final Log log = LogFactory.getLog(AbstractTracingNodeIteratingGraphTraverser.class);
    
    static {
        
        try {
            VISITED_OR_QUEUED_NODES_FIELD = AbstractNodeIteratingGraphTraverser.class
                                            .getDeclaredField("visitedOrQueuedNodes");
            NODE_QUEUE_FIELD = BreadthFirstGraphTraverser.class.getDeclaredField("nodeQueue");
            BACKING_TRAVERSER_FIELD = StatelessGraphTraverserAdapter.class
                                      .getDeclaredField("backingTraverser");
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
        
    }
    
    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.AbstractGraphTraverser#addNode(java.util.Collection)
     */
    @Override
    public void addNode(Collection<? extends T> nodes) {
        boolean traceLoggingEnabled = log.isTraceEnabled();
        
        if (traceLoggingEnabled) {
            log.trace("Entering 'addNode'. Adding nodes " 
                      + LoggingUtils.collectionToString(nodes, true) + " to the queue. "
                      + getTraverserStatus(null));
        }
        
        super.addNode(nodes);
        
        if (traceLoggingEnabled) {
            log.trace("Exiting 'addNode'. " + getTraverserStatus(null));
        }
        
    }

    private String getTraverserStatus(U traversalState) {
        StringBuilder status = new StringBuilder();
        
        status.append("[traverser class: ").append(getClass().getSimpleName());
        
        if (traversalState != null) {
            status.append(", traversal state: ").append(traversalState);
        }
        
        return LoggingUtils.toPackageNameFreeString(
                status.append(", queue: ").append(getValue(NODE_QUEUE_FIELD))
                .append(", visited/queued: ").append(getValue(VISITED_OR_QUEUED_NODES_FIELD))
                .append("]"));
    }

    private Object getValue(Field field) {
        
        // subclasses of StatelessGraphTraverserAdapter actually use a backing traverser
        try {
            return field.get(StatelessGraphTraverserAdapter.class.isAssignableFrom(getClass())
                             ? BACKING_TRAVERSER_FIELD.get(this) : this);
        } catch (Exception exception) {
            return FIELD_RETRIEVAL_ERROR_MSG;
        }
        
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.AbstractGraphTraverser#traverseFrom(java.util.Collection, java.lang.Object)
     */
    @Override
    public boolean traverseFrom(Collection<? extends T> startNodes,
            U traversalState) {
        boolean traceLoggingEnabled = log.isTraceEnabled();
        
        if (traceLoggingEnabled) {
            log.trace("Entering 'traverseFrom'. Staring with nodes " 
                      + LoggingUtils.collectionToString(startNodes, true) 
                      + " and traversal state " 
                      + LoggingUtils.toPackageNameFreeString(traversalState));
        }

        boolean retVal = super.traverseFrom(startNodes, traversalState);
        
        if (traceLoggingEnabled) {
            log.trace("Exiting 'traverseFrom' with result '" + retVal + "'. " 
                      + getTraverserStatus(traversalState));
        }
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.traverser.AbstractNodeIteratingGraphTraverser#visitNode(com.qrmedia.commons.graph.traverser.NodeVisitor, java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean visitNode(NodeVisitor<T, U> visitor, T node,
            U traversalState) {
        boolean traceLoggingEnabled = log.isTraceEnabled();
        
        if (traceLoggingEnabled) {
            log.trace("Entering 'visitNode'. About to pass node " 
                      + LoggingUtils.toPackageNameFreeString(node) + " to visitor " 
                      + LoggingUtils.toPackageNameFreeString(visitor) 
                      + ". " + getTraverserStatus(traversalState));
        }
        
        boolean retVal = super.visitNode(visitor, node, traversalState);
        
        if (traceLoggingEnabled) {
            log.trace("Exiting 'visitNode' with result '" + retVal + "'. " 
                      + getTraverserStatus(traversalState));
        }
        
        return retVal;
    }

}
