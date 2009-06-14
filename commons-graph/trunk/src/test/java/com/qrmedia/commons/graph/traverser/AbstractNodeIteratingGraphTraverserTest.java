/*
 * @(#)AbstractNodeIteratingGraphTraverserTest.java     13 Aug 2008
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

import static com.qrmedia.commons.test.easymock.CollectionEqualsMatcher.colEq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.rmi.AccessException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import com.qrmedia.commons.collections.SetUtils;
import com.qrmedia.commons.reflect.ReflectionUtils;


/**
 * Unit tests for the {@link AbstractNodeIteratingGraphTraverser}.
 * 
 * @author anph
 * @since 13 Aug 2008
 *
 */
@SuppressWarnings("unchecked")
public class AbstractNodeIteratingGraphTraverserTest {
    
    // Object is important here, because it means that a collection would also match
    private AbstractNodeIteratingGraphTraverser<Object, StringBuilder> traverser;
    
    private final NodeVisitor<Object, StringBuilder> visitor1 = 
        (NodeVisitor<Object, StringBuilder>) createStrictMock(NodeVisitor.class);
    private final NodeVisitor<Object, StringBuilder> visitor2 = 
        (NodeVisitor<Object, StringBuilder>) createStrictMock(NodeVisitor.class);
    
    @Before
    public void prepareFixture() throws Exception {
        Class<AbstractNodeIteratingGraphTraverser> traverserClass = AbstractNodeIteratingGraphTraverser.class;
        traverser = createMock(traverserClass, 
                traverserClass.getMethod("hasNext", (Class[]) null),
                traverserClass.getMethod("next", (Class[]) null),
                traverserClass.getDeclaredMethod("enqueueNodes", Collection.class));  

        // creating the mock doesn't initialize instance fields, for some reason
        ReflectionUtils.setValue(traverser, "nodeVisitors", 
                                 new ArrayList<NodeVisitor<Object, StringBuilder>>());
        ReflectionUtils.setValue(traverser, "visitedOrQueuedNodes", new HashSet<Object>());        
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void remove() {
        traverser.remove();
    }
    
    /**
     * Starting from a node 1, traverse the graph. Node 1 discovers two extra nodes,
     * 1 and 2, of which only 2 should be called.
     * <p>
     * Ensures that any state maintained during traversal is cleared.
     */
    @Test
    public void traverseFrom_node() throws AccessException {
        traverser.addNodeVisitor(visitor1);
        
        // initially, node 1 will be queued
        traverser.enqueueNodes(colEq(Arrays.<Object>asList(1)));
        expectLastCall();
        
        traverser.hasNext();
        expectLastCall().andReturn(true);
        traverser.next();
        expectLastCall().andReturn(1);
        
        StringBuilder traversalState = new StringBuilder();
        visitor1.visitNode(1, traverser, traversalState);
        expectLastCall().andAnswer(new NodeVisitorAction(traverser, 
                Arrays.<Object>asList(1, 2), "James", true));
        
        // the second node 1 should not be enqueued
        traverser.enqueueNodes(colEq(Arrays.<Object>asList(2)));
        expectLastCall();

        traverser.hasNext();
        expectLastCall().andReturn(true);
        traverser.next();
        expectLastCall().andReturn(2);

        // the visitor should *not* be called again for node 1
        visitor1.visitNode(2, traverser, traversalState);
        expectLastCall().andAnswer(new NodeVisitorAction(traverser, null, " Bond", true));
        
        // all nodes have been visited
        traverser.hasNext();
        expectLastCall().andReturn(false);
        
        replay(traverser, visitor1);
        
        // none of the visitors returned "false", so the traversal should succeed
        assertTrue(traverser.traverseFrom(1, traversalState));
        
        verify(traverser, visitor1);
        
        // check that the state object was correctly updated
        assertEquals("James Bond", traversalState.toString());
        
        // check that any internal state maintained during the cloning has been cleaned up
        assertTrue(ReflectionUtils.<Set<?>>getValue(traverser, "visitedOrQueuedNodes")
                   .isEmpty());        
    }
    
    static class NodeVisitorAction implements IAnswer<Boolean> {
        private GraphTraverser<Object, StringBuilder> traverser;
        private Collection<Object> newNodes;
        private String message;
        private Boolean continueTraversal;
        
        NodeVisitorAction(GraphTraverser<Object, StringBuilder> traverser,
                Collection<Object> newNodes, String message, Boolean continueTraversal) {
            this.traverser = traverser;
            this.newNodes = newNodes;
            this.message = message;
            this.continueTraversal = continueTraversal;
        }
        
        /* (non-Javadoc)
         * @see org.easymock.IAnswer#answer()
         */
        public Boolean answer() throws Throwable {

            if (newNodes != null) {
                traverser.addNode(newNodes);
            }
            
            if (message != null) {
                
                // add the message to the traversal state, which is passed as the third parameter
                ((StringBuilder) getCurrentArguments()[2]).append(message);
            }
            
            return continueTraversal;
        }
        
    }

    /**
     * Starting from nodes 1, 1 and 2, traverse the graph. Node 1 will be called twice
     * because it is specified with the starting nodes, and the second visitor is not
     * called for node 2 because the first one aborts the traversal.
     * <p>
     * This also tests that the visitors are called in the expected order, and ensures
     * that any state maintained during traversal is cleared.
     */
    @Test
    public void traverseFrom_nodes() throws AccessException {
        traverser.addNodeVisitor(visitor1);
        traverser.addNodeVisitor(visitor2);

        // initially, nodes 1, 1 and 2 will be queued
        traverser.enqueueNodes(colEq(Arrays.<Object>asList(1, 1, 2)));
        expectLastCall();
        
        StringBuilder traversalState = new StringBuilder();
        
        /*
         * The visitors will also be called for the *second* 1 (even though it is equal 
         * to the previous one), because it was specified with the initial objects.  
         */
        traverser.hasNext();
        expectLastCall().andReturn(true).times(2);
        traverser.next();
        expectLastCall().andReturn(1).times(2);
        
        visitor1.visitNode(1, traverser, traversalState);
        expectLastCall().andAnswer(new NodeVisitorAction(traverser, null, null, true))
        .times(2);
        
        visitor2.visitNode(1, traverser, traversalState);
        expectLastCall().andAnswer(new NodeVisitorAction(traverser, null, null, true))
        .times(2);

        // graph traversal is aborted by visitor1, so no further call to visitor2 is expected
        traverser.hasNext();
        expectLastCall().andReturn(true);
        traverser.next();
        expectLastCall().andReturn(2);
        
        visitor1.visitNode(2, traverser, traversalState);
        expectLastCall().andAnswer(new NodeVisitorAction(traverser, null, null, false));
        replay(traverser, visitor1, visitor2);
        
        //  of the visitors returned "false", so the traversal should have failed
        assertFalse(traverser.traverseFrom(Arrays.<Object>asList(1, 1, 2), traversalState));
        
        verify(traverser, visitor1, visitor2);
        
        // check that the state object was not updated
        assertEquals("", traversalState.toString());        
        
        // check that any internal state maintained during the cloning has been cleaned up
        assertTrue(ReflectionUtils.<Set<?>>getValue(traverser, "visitedOrQueuedNodes")
                   .isEmpty());                
    }

    @Test
    public void addNode_node_unseen() {
        traverser.enqueueNodes(colEq(Arrays.asList(1)));
        expectLastCall();
        replay(traverser);
        
        traverser.addNode(1);
        
        verify(traverser);
    }
    
    @Test
    public void addNode_node_seen() {
        ReflectionUtils.setValue(traverser, "visitedOrQueuedNodes", SetUtils.asSet(1));
        
        // expect no calls to enqueue
        traverser.addNode(1);        
    }    
    
    @Test
    public void addNode_nodes_unseen() {
        traverser.enqueueNodes(colEq(Arrays.asList(1)));
        expectLastCall();
        replay(traverser);
        
        traverser.addNode(1);
        
        verify(traverser);
    }
    
    @Test
    public void addNode_nodes_seen() {
        ReflectionUtils.setValue(traverser, "visitedOrQueuedNodes", SetUtils.asSet(1));
        
        traverser.enqueueNodes(colEq(Arrays.asList(2)));
        expectLastCall();
        replay(traverser);
        
        traverser.addNode(Arrays.asList(1, 2));
        
        verify(traverser);
    }
    
}