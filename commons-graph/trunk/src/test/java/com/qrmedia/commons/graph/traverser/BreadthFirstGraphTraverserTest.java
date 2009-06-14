/*
 * @(#)BreadthFirstGraphTraverserTest.java     13 Aug 2008
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.rmi.AccessException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;

import com.qrmedia.commons.reflect.ReflectionUtils;


/**
 * Unit tests for the <code>{@link BreadthFirstGraphTraverser}</code>.
 * 
 * @author anph
 * @since 13 Aug 2008
 *
 */
public class BreadthFirstGraphTraverserTest {
    private final BreadthFirstGraphTraverser<Object, StringBuilder> traverser = 
        new BreadthFirstGraphTraverser<Object, StringBuilder>();

    private Queue<Object> nodeQueue;
    
    @Before
    public void prepareFixture() throws AccessException {
        nodeQueue = ReflectionUtils.getValue(traverser, "nodeQueue");
    }
    
    @Test
    public void hasNext_emptyQueue() {
        assertFalse(traverser.hasNext());
    }
    
    @Test
    public void hasNext() {
        nodeQueue.add(new Object());
        
        assertTrue(traverser.hasNext());        
    }    
    
    @Test(expected = NoSuchElementException.class)
    public void next_emptyQueue() {
        traverser.next();
    }
    
    @Test
    public void next() {
        Object obj = new Object();
        nodeQueue.add(obj);
        
        assertSame(obj, traverser.next());        
    }    
    
    @Test
    public void enqueueNodes() {
        traverser.enqueueNodes(Arrays.asList(1, 2));
        
        assertTrue(nodeQueue.containsAll(Arrays.asList(1, 2)));
    }
    
}
