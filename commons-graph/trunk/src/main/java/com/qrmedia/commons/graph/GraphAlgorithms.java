/*
 * @(#)GraphAlgorithms.java     6 Dec 2007
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

import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of the standard maximum cardinality matching algorithm.
 * <p>
 * Adapted from <i>A Java Library of Graph Algorithms and Optimization</i>, by Hang T. Lau.
 * <p>
 * &copy; 2007 by Taylor &amp; Francis Group, LLC
 * 
 * @author anph
 * @since 6 Dec 2007
 * 
 */
public class GraphAlgorithms {
    
    /**
     * <u>Maximum Cardinality Matching</u>
     * <p>
     * For a given undirected graph G, a matching is a subset S of edges of G in which no
     * two edges in S are adjacent in G. The maximum cardinality matching problem is to
     * find a maching of maximum cardinality.
     * <p>
     * A node that is not matched is called an exposed node. An alternating path with
     * respect to a given matching S is a path in which the edges appear alternately in S
     * and not in S. An augmenting path is an alternating path which begins with an
     * exposed node and ends with another exposed node. A fundamental theorem states that
     * a matching S in G is maximum if and only if G has no augmenting path with respect to S.
     * <p>
     * The basic method for finding the maximum matching starts with an arbitrary
     * matching Q. An augmenting path P with respect to Q is found. A new matching
     * is constructed by taking those edges of P or Q that are not in both P and Q. The
     * process is repeated and the matching is maximum when no augmenting path is
     * found. The algorithm takes O(n&#179) operations for a graph of n nodes.
     * 
     * @param n     the number of nodes of the graph, labeled from 1 to n
     * @param edges the edges of the graph, with first and second nodes between 1 and n
     * @return the set of edges representing the maximum cardinality matching, and the
     *         number of unmatched nodes
     */
    public static CardinalityMatchingResult cardinalityMatching(int n, Set<Edge> edges) {
        int m = edges.size();
        int i, j, k, n1, m2, istart, first, last, nodep, nodeq, nodeu, nodev, nodew;
        int neigh1, neigh2, unmatch;
        int nodei[] = new int[m + 1];
        int nodej[] = new int[m + 1];
        int fwdedge[] = new int[m + m + 1];
        int firstedge[] = new int[n + 2];
        int grandparent[] = new int[n + 1];
        int queue[] = new int[n + 1];
        boolean outree[] = new boolean[n + 1];
        boolean newnode, nopath;
        int pair[] = new int[n + 1];
        Set<Edge> matching = new HashSet<Edge>();
        
        // handle the special case of no edges, in which case the matching is obviously empty
        if (m == 0) {
            return new CardinalityMatchingResult(matching, n);
        }
        
        // create a node-based representation of the edge set
        i = 1;
        for (Edge edge: edges) {
            nodei[i] = edge.getFirstNode();
            nodej[i] = edge.getSecondNode();
            i++;
        }
        
        // set up the forward star graph representation
        n1 = n + 1;
        m2 = m * 2;
        k = 0;
        for (i = 1; i <= n; i++) {
            /*
             * If n > 2 * m and the last increment of k, which happens after the last edge is
             * processed, occurs *before* the final iteration of this loop, k + 1 will be 
             * 2 * m + 1, which would point beyond the end of the "fwdedge" array. 
             */
            firstedge[i] = Math.min(k + 1, m2);
            for (j = 1; j <= m; j++)
                if (nodei[j] == i) {
                    k++;
                    fwdedge[k] = nodej[j];
                } else {
                    if (nodej[j] == i) {
                        k++;
                        fwdedge[k] = nodei[j];
                    }
                }
        }
        firstedge[n1] = m + 1;
        // all nodes are unmatched
        unmatch = n;
        for (i = 1; i <= n; i++)
            pair[i] = 0;
        for (i = 1; i <= n; i++)
            if (pair[i] == 0) {
                j = firstedge[i];
                k = firstedge[i + 1] - 1;
                
                /*
                 * Skip nodes that have no outgoing edges - for these nodes and these nodes only,
                 * firstedge[i] will be equal to firstedge[i + 1], so k < j. For nodes with
                 * at least one outgoing edge, (firstedge[i + 1] - firstedge[i]) will be greater
                 * or equal to one, so k - j >= 0, i.e. k >= j. 
                 */
                if (k < j) {
                    continue;
                }
                
                while ((pair[fwdedge[j]] != 0) && (j < k))
                    j++;
                if (pair[fwdedge[j]] == 0) {
                    // match a pair of nodes
                    pair[fwdedge[j]] = i;
                    pair[i] = fwdedge[j];
                    unmatch -= 2;
                }
            }
        for (istart = 1; istart <= n; istart++)
            if ((unmatch >= 2) && (pair[istart] == 0)) {
                // 'istart' is not yet matched
                for (i = 1; i <= n; i++)
                    outree[i] = true;
                outree[istart] = false;
                // insert the root in the queue
                queue[1] = istart;
                first = 1;
                last = 1;
                nopath = true;
                do {
                    nodep = queue[first];
                    first = first + 1;

                    nodeu = firstedge[nodep];
                    nodew = firstedge[nodep + 1] - 1;
                    while (nopath && (nodeu <= nodew)) {
                        // examine the neighbor of 'nodep'
                        if (outree[fwdedge[nodeu]]) {
                            neigh2 = fwdedge[nodeu];
                            nodeq = pair[neigh2];
                            if (nodeq == 0) {
                                // an augmentation path is found
                                pair[neigh2] = nodep;
                                do {
                                    neigh1 = pair[nodep];
                                    pair[nodep] = neigh2;
                                    if (neigh1 != 0) {
                                        nodep = grandparent[nodep];
                                        pair[neigh1] = nodep;
                                        neigh2 = neigh1;
                                    }
                                } while (neigh1 != 0);
                                unmatch -= 2;
                                nopath = false;
                            } else {
                                if (nodeq != nodep) {
                                    if (nodep == istart)
                                        newnode = true;
                                    else {
                                        nodev = grandparent[nodep];
                                        while ((nodev != istart)
                                                && (nodev != neigh2))
                                            nodev = grandparent[nodev];
                                        newnode =
                                                (nodev == istart ? true : false);
                                    }
                                    if (newnode) {
                                        // add a tree link
                                        outree[neigh2] = false;
                                        grandparent[nodeq] = nodep;
                                        last++;
                                        queue[last] = nodeq;
                                    }
                                }
                            }
                        }
                        nodeu++;
                    }
                } while (nopath && (first <= last));
            }
        
        // convert the pair representation to an edge set, thus avoiding duplicates
        for (i = 1; i <= n; i++) {
            int otherNode = pair[i];
            
            // "0" represents an unmatched node
            if (otherNode != 0) {
                matching.add(new Edge(i, otherNode));
            }
            
        }
        
        return new CardinalityMatchingResult(matching, unmatch);
    }
    
    /**
     * The result of the maximum cardinality matching algorithm, which includes the
     * number of unmatched nodes alongside the matching itself;
     *
     * @author anph
     * @since 10 Dec 2007
     */
    public static class CardinalityMatchingResult {
        private Set<Edge> matching;
        private int numUnmatchedNodes;
        
        // should only be constructed from inside the library
        private CardinalityMatchingResult(Set<Edge> matching, int numUnmatchedNodes) {
            this.matching = matching;
            this.numUnmatchedNodes = numUnmatchedNodes;
        }

        /**
         * Getter for matching.
         *
         * @return the matching.
         */
        public Set<Edge> getMatching() {
            return matching;
        }

        /**
         * Getter for numUnmatchedNodes.
         *
         * @return the numUnmatchedNodes.
         */
        public int getNumUnmatchedNodes() {
            return numUnmatchedNodes;
        }
        
    }    
    
}
