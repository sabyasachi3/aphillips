/*
 * @(#)GraphAlgorithms.java     10 Dec 2007
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
package com.qrmedia.commons.graph;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.qrmedia.commons.graph.GraphAlgorithms.CardinalityMatchingResult;

/**
 * Unit tests for the graph algorithm library.
 * 
 * @author anph
 * @see GraphAlgorithmsTest
 * @since 10 Dec 2007
 */
@RunWith(value = Parameterized.class)
public class GraphAlgorithmsTest  {
    private int numNodes;
    private Set<Edge> edges;
    private Set<Edge> matching;
    private int numUnmatchedNodes;
    
    @Parameters
    public static Collection<Object[]> data() {

        // degenerate case
        int numNodes1 = 0;
        int[][] edgeNodes1 = { {}, {} };
        int[][] matchingEdgeNodes1 = { {}, {} };
        int numUnmatchedNodes1 = 0;
        
        // degenerate case
        int numNodes2 = 1;
        int[][] edgeNodes2 = { {}, {} };
        int[][] matchingEdgeNodes2 = { {}, {} };
        int numUnmatchedNodes2 = 1;
        
        // an example with n > 2 * m, with the last edges pointing to a node before n
        int numNodes3 = 3;
        int[][] edgeNodes3 = { { 1 }, { 2 } }; 
        int[][] matchingEdgeNodes3 = { { 1 }, { 2 } };
        int numUnmatchedNodes3 = 1;
        
        // an example with n > 2 * m, with the last edges pointing to node n
        int numNodes4 = 3;
        int[][] edgeNodes4 = { { 1 }, { 3 } };
        int[][] matchingEdgeNodes4 = { { 1 }, { 3 } };
        int numUnmatchedNodes4 = 1;
        
        // the example from "A Java Library of Graph Algorithms and Optimization"
        int numNodes5 = 13;
        int[][] edgeNodes5 = { { 4, 3, 1, 12, 6, 8, 13, 10, 1, 9, 1, 10, 3 }, 
                               { 2, 13, 5, 7, 11, 1, 9, 12, 6, 3, 11, 2, 1 } };
        
        // note that {3, 13} instead of {3, 9} is also a valid solution
        int[][] matchingEdgeNodes5 = { { 1, 2, 3, 6, 7 }, 
                                       { 5, 10, 9, 11, 12 } };
        int numUnmatchedNodes5 = 3;
        
        // an example that fails if nodes that have no outgoing edges are not skipped
        int numNodes6 = 5;
        int[][] edgeNodes6 = { { 2 }, { 5 } };
        int[][] matchingEdgeNodes6 = { { 2 }, { 5 } };
        int numUnmatchedNodes6 = 3;
        
        // an example where only certain choices lead to a maximum matching
        int numNodes7 = 9;
        int[][] edgeNodes7 = { { 1, 1, 3, 4, 5, 8, 9 }, 
                               { 6, 7, 7, 7, 6, 4, 5 } };
        int[][] matchingEdgeNodes7 = { { 1, 3, 8, 9 }, 
                                       { 6, 7, 4, 5 } };
        int numUnmatchedNodes7 = 1;
        
        Collection<Object[]> data = new ArrayList<Object[]>();
        
        data.add(new Object[] { numNodes1, createEdges(edgeNodes1), 
                                createEdges(matchingEdgeNodes1), numUnmatchedNodes1 });
        data.add(new Object[] { numNodes2, createEdges(edgeNodes2), 
                                createEdges(matchingEdgeNodes2), numUnmatchedNodes2 });
        data.add(new Object[] { numNodes3, createEdges(edgeNodes3), 
                                createEdges(matchingEdgeNodes3), numUnmatchedNodes3 });
        data.add(new Object[] { numNodes4, createEdges(edgeNodes4), 
                                createEdges(matchingEdgeNodes4), numUnmatchedNodes4 });
        data.add(new Object[] { numNodes5, createEdges(edgeNodes5), 
                                createEdges(matchingEdgeNodes5), numUnmatchedNodes5 });
        data.add(new Object[] { numNodes6, createEdges(edgeNodes6), 
                                createEdges(matchingEdgeNodes6), numUnmatchedNodes6 });
        data.add(new Object[] { numNodes7, createEdges(edgeNodes7), 
                                createEdges(matchingEdgeNodes7), numUnmatchedNodes7 });        
        return data;
    }
    
    // convenience method to convert an edge collection represented by the nodes into Edge objects
    private static Set<Edge> createEdges(int[][] edgeNodes) {
        
        // assumes "edgeNodes" contains two arrays of equal length (first/second nodes of each edge)
        int[] firstNodes = edgeNodes[0];
        int[] secondNodes = edgeNodes[1];
        int numEdges = firstNodes.length;
        Set<Edge> edges = new HashSet<Edge>(numEdges);
        
        for (int i = 0; i < numEdges; i++) {
            edges.add(new Edge(firstNodes[i], secondNodes[i]));
        }
        
        return edges;
    }
    
    // called for each parameter set in the test data
    public GraphAlgorithmsTest(int numNodes, Set<Edge> edges, Set<Edge> matching, 
                               int numUnmatchedNodes) {
        this.numNodes = numNodes;
        this.edges = edges;
        
        // expected result
        this.matching = matching;
        this.numUnmatchedNodes = numUnmatchedNodes;
    }
    
    @Test
    public void maximumCardinalityMatching() {
        CardinalityMatchingResult result = GraphAlgorithms.cardinalityMatching(numNodes, edges);
        
        assertEquals(matching, result.getMatching());
        assertEquals(numUnmatchedNodes, result.getNumUnmatchedNodes());
    }

}
