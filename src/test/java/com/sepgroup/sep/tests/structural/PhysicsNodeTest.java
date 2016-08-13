package com.sepgroup.sep.tests.structural;

import com.sepgroup.sep.analysis.GraphDisplay.PhysicsNode;
import org.junit.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mark on 8/13/16.
 */
public class PhysicsNodeTest {


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
