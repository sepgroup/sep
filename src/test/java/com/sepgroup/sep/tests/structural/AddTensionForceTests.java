package com.sepgroup.sep.tests.structural;

import com.sepgroup.sep.analysis.GraphDisplay.PhysicsNode;
import org.junit.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * Test addTensionForce() from PhysicsNode.java.
 * 7 tests for basis path coverage.
 * Created by mark on 8/13/16.
 */
public class AddTensionForceTests {

    /**
     * <p>
     *     Identifier: S4-1
     * </p>
     * <p>
     *     Path: <52, 71,exit>
     * </p>
     * <p>
     */
    @Test
    public void testAddTensionForce1() throws Exception {
        PhysicsNode node = new PhysicsNode();
        PhysicsNode outNode = new PhysicsNode();

        outNode.setPosition(16, 16);
        node.addOutNode(outNode);
        node.setPosition(4, 4);

        node.setPhysics(false);

        // method we are testing
        node.addTensionForce();
        double[] expected = {0.0, 0.0};
        assertArrayEquals(expected, node.getNetForce(), 0.0000000000001);
    }

    /**
     * <p>
     *     Identifier: S4-2
     * </p>
     * <p>
     *     Path: <52,53,71,exit>
     * </p>
     * <p>
     */
    @Test
    public void testAddTensionForce2() throws Exception {
        PhysicsNode node = new PhysicsNode();
        node.setPosition(4, 4);

        node.setPhysics(true);

        // method we are testing
        node.addTensionForce();
        double[] expected = {0.0, 0.0};
        assertArrayEquals(expected, node.getNetForce(), 0.0000000000001);
    }

    /**
     * <p>
     *     Identifier: S4-3
     * </p>
     * <p>
     *     Path: <52,53,54,55,53,71,exit>
     * </p>
     * <p>
     */
    @Test
    public void testAddTensionForce3() throws Exception {
        PhysicsNode node = new PhysicsNode();
        PhysicsNode outNode = new PhysicsNode();

        outNode.setPosition(16, 16);
        outNode.setPhysics(false);
        node.addOutNode(outNode);
        node.setPosition(4, 4);

        node.setPhysics(false);

        // method we are testing
        node.addTensionForce();
        double[] expected = {0.0, 0.0};
        assertArrayEquals(expected, node.getNetForce(), 0.0000000000001);

    }

    /**
     * <p>
     *     Identifier: S4-4a
     * </p>
     * <p>
     *     Path: <52,53,54,55,56,57,58,59,60,61,66,67,53,71,exit>
     * </p>
     * <p>
     */
    @Test
    public void testAddTensionForce4a() throws Exception {
        PhysicsNode node = new PhysicsNode();
        PhysicsNode outNode = new PhysicsNode();

        outNode.setPosition(7, 12);
        outNode.setPhysics(true);
        node.addOutNode(outNode);
        node.setPosition(4, 8);

        node.setPhysics(true);

        // method we are testing
        node.addTensionForce();
        double[] expected = {0.00000009, 0.00000012};
        assertArrayEquals(expected, node.getNetForce(), 0.0000000000001);
    }

    /**
     * <p>
     *     Identifier: S4-4b
     * </p>
     * <p>
     *     Path: <52,53,54,55,56,57,58,59,60,61,66,67,53,71,exit>
     * </p>
     * <p>
     */
    @Test
    public void testAddTensionForce4b() throws Exception {
        PhysicsNode node = new PhysicsNode();
        PhysicsNode outNode = new PhysicsNode();

        outNode.setPosition(7, 12);
        outNode.setPhysics(true);
        node.addOutNode(outNode);
        node.setPosition(4, 8);

        node.setPhysics(true);

        // method we are testing
        node.addTensionForce();
        double[] expected = {-0.00000009, -0.00000012};
        assertArrayEquals(expected, outNode.getNetForce(), 0.0000000000001);
    }

    /**
     * <p>
     *     Identifier: S4-5a
     * </p>
     * <p>
     *     Path: <52,53,54,55,56,57,58,59,60,61,62,66,67,53,71,exit>
     * </p>
     * <p>
     */
    @Test
    public void testAddTensionForce5a() throws Exception {
        PhysicsNode node = new PhysicsNode();
        PhysicsNode outNode = new PhysicsNode();

        outNode.setPosition(7, 12);
        outNode.setPhysics(true);
        node.addOutNode(outNode);
        node.setPosition(4, 8);
        node.setDepth(10);

        node.setPhysics(true);

        // method we are testing
        node.addTensionForce();
        double[] expected = {-0.00000009, -0.00000012};
        assertArrayEquals(expected, node.getNetForce(), 0.0000000000001);
    }

    /**
     * <p>
     *     Identifier: S4-5b
     * </p>
     * <p>
     *     Path: <52,53,54,55,56,57,58,59,60,61,62,66,67,53,71,exit>
     * </p>
     * <p>
     */
    @Test
    public void testAddTensionForce5b() throws Exception {
        PhysicsNode node = new PhysicsNode();
        PhysicsNode outNode = new PhysicsNode();

        outNode.setPosition(7, 12);
        outNode.setPhysics(true);
        node.addOutNode(outNode);
        node.setPosition(4, 8);
        node.setDepth(10);

        node.setPhysics(true);

        // method we are testing
        node.addTensionForce();
        double[] expected = {0.00000009, 0.00000012};
        assertArrayEquals(expected, outNode.getNetForce(), 0.0000000000001);
    }
}
