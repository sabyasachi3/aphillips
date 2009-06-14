/*
 * @(#)NodeVisitor.java     13 Aug 2008
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

/**
 * A variant of the GoF <a href="http://www.mcdonaldland.info/2007/11/28/40/"><i>Visitor</i></a>
 * design pattern for operations to be performed on graph nodes.
 *  
 * @param <T> the type of graph nodes
 * @param <U> the type of the state-maintaining object 
 * @author anph
 * @see GraphTraverser
 * @since 13 Aug 2008
 *
 */
public interface NodeVisitor<T, U> {

    /**
     * The <i>visitElement</i> method of the <i>Visitor</i> pattern.
     * <p>
     * During the course of the operation, the visitor may register new nodes with
     * the graph traverser, to be visited later. In this way the graph is &quot;dynamically
     * discovered&quot;.
     * <p>
     * The visitor may indicate that traversal of the graph is to be stopped, by returning
     * <code>false</code>.
     * <p>
     * An object representing the traversal's &quot;state&quot; is also passed, and may be
     * modified by the visitor, as appropriate, or may be ignored. For instance, this could
     * be a collection of accumulated errors.
     * 
     * @param node  the node to visit
     * @param graphTraverser    the traverser controlling the graph traversal
     * @param traversalState    an object representing the traversal's state
     * @return  <code>false</code> iff graph traversal is to be aborted
     */
    boolean visitNode(T node, GraphTraverser<T, U> graphTraverser, U traversalState);
}
