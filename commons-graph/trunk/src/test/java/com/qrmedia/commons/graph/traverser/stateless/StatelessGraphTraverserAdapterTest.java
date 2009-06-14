/*
 * @(#)StatelessGraphTraverserAdapterTest.java     20 Feb 2009
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

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.qrmedia.commons.graph.traverser.BreadthFirstGraphTraverser;
import com.qrmedia.commons.graph.traverser.GraphTraverser;
import com.qrmedia.commons.graph.traverser.NodeVisitor;

/**
 * Unit tests for the {@link StatelessGraphTraverserAdapter}.
 * 
 * @author anph
 * @since 20 Feb 2009
 *
 */
@SuppressWarnings("unchecked")
public class StatelessGraphTraverserAdapterTest {
    private StatelessGraphTraverserAdapter<Object> adapter = 
        new StatelessGraphTraverserAdapter<Object>(
                new BreadthFirstGraphTraverser<Object, Object>());
    
    private NodeVisitor<Object, Object> visitor = 
        (NodeVisitor<Object, Object>) createMock(NodeVisitor.class);
    
    @Before
    public void prepareFixture() {
        adapter.addNodeVisitor(visitor);
    }
    
    /**
     * Ensures that visitors added to the stateless traverser actually
     * get called with the <u>stateless</u> traverser instance as the <code>graphTraverser</code>
     * argument to {@link NodeVisitor#visitNode(Object, GraphTraverser, Object)},
     * rather than the <i>backing</i> traverser.
     * 
     */
    @Test
    public void traverseFrom() {
        Object obj = new Object();
        visitor.visitNode(obj, adapter, null);
        expectLastCall().andReturn(true);
        replay(visitor);
        
        assertTrue(adapter.traverseFrom(obj));
        
        verify(visitor);
    }
    
}
