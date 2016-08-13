package com.sepgroup.sep.tests.structural;

import com.sepgroup.sep.analysis.Graph;
import com.sepgroup.sep.analysis.Node;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayDeque;

import static org.junit.Assert.assertTrue;

/**
 * Test breadthFirstSearch() from Graph.java.
 * 4 tests for basis path coverage.
 * Created by jeremybrown on 2016-08-12.
 */
public class BreadthFirstSearchTests {

    Graph g;

    @Before
    public void setUp() {
        g = new Graph();
    }

    /**
     * Path: <73,74,75,81,84,exit>
     */
    @Test
    public void testBreadthFirstSearchPath1() throws Exception {
        ArrayDeque<Node> q = new ArrayDeque<>();
        Node n = new Node();
        g = new Graph();

        g.breadthFirstSearch(q, n);
        assertTrue(q.isEmpty());
    }

    /**
     * Path: <73,74,75,76,77,78,75,81,84,exit>
     */
    @Ignore
    @Test
    public void testBreadthFirstSearchPath2() throws Exception {
        // Test path unreachable
        // line 78 adds a node to q, so line 82 must be part of the path
    }

    /**
     * Path: <73,74,75,76,75,81,84,exit>
     */
    @Test
    public void testBreadthFirstSearchPath3() throws Exception {
        ArrayDeque<Node> q = new ArrayDeque<>();
        Node n1 = new Node();
        Node n2 = new Node(n1);
        n2.setVisited();
        g = new Graph();

        g.breadthFirstSearch(q, n1);
        assertTrue(q.isEmpty());
        assertTrue(n2.wasVisited());
    }

    /**
     * Path: <73,74,75,81,82,84,exit>
     */
    @Test
    public void testBreadthFirstSearchPath4() throws Exception {
        ArrayDeque<Node> q = new ArrayDeque<>();
        Node n1 = new Node();
        n1.setVisited();
        Node n2 = new Node();
        n2.setVisited();
        q.add(n2);
        g = new Graph();

        g.breadthFirstSearch(q, n1);
        assertTrue(q.isEmpty());
        assertTrue(n1.wasVisited());
    }

}
