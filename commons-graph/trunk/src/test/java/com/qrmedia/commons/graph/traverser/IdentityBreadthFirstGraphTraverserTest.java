/*
 * @(#)IdentityBreadthFirstGraphTraverserTest.java     13 Aug 2008
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

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.qrmedia.commons.graph.traverser.AbstractNodeIteratingGraphTraverserTest.NodeVisitorAction;

/**
 * Unit tests for the <code>{@link IdentityBreadthFirstGraphTraverser}</code>.
 * 
 * @author anph
 * @since 13 Aug 2008
 *
 */
@SuppressWarnings("unchecked")
public class IdentityBreadthFirstGraphTraverserTest {
    
    // Object is important here, because it means that a collection would also match
    private final IdentityBreadthFirstGraphTraverser<Object, StringBuilder> traverser = 
        new IdentityBreadthFirstGraphTraverser<Object, StringBuilder>();
    
    private final NodeVisitor<Object, StringBuilder> visitor1 = 
        (NodeVisitor<Object, StringBuilder>) createStrictMock(NodeVisitor.class);
    
    @Test
    public void traverseFrom_nodes() {
        traverser.addNodeVisitor(visitor1);

        /*
         * The visitor will be called for "one" twice, because it was specified 
         * with the initial objects.  
         */
        
        Integer one = new Integer(1);
        Integer two1 = new Integer(2);
        Integer two2 = new Integer(2);
        StringBuilder traversalState = new StringBuilder();
        visitor1.visitNode(one, traverser, traversalState);
        expectLastCall().andAnswer(new NodeVisitorAction(traverser, 
                Arrays.<Object>asList(two1, two2), null, true)).times(2);
        
        // two1 was queued twice but will only be visited *once* 
        visitor1.visitNode(two1, traverser, traversalState);
        expectLastCall().andAnswer(
                new NodeVisitorAction(traverser, null, "Bond", true));
        
        // two2 will be visited even though it is equal to two1
        visitor1.visitNode(two2, traverser, traversalState);
        expectLastCall().andAnswer(
                new NodeVisitorAction(traverser, null, ", James Bond", true));        
        replay(visitor1);
        
        // none of the visitors returned "false", so the traversal should succeed
        assertTrue(traverser.traverseFrom(Arrays.<Object>asList(one, one, two1), 
                traversalState));
        
        verify(visitor1);
        
        // check that the state object was correctly updated
        assertEquals("Bond, James Bond", traversalState.toString());
    }
 
}