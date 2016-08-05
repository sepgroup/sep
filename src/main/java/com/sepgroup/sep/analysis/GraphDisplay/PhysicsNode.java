package com.sepgroup.sep.analysis.GraphDisplay;
import com.sepgroup.sep.analysis.Node;
import com.sepgroup.sep.analysis.Data;

import java.awt.*;
import java.util.ArrayList;


/**
 * Created by HP on 8/3/2016.
 */
public class PhysicsNode extends Node implements Physics,Drawable {
    static double coulombsConstant = 0.04;
    static double hooksConstant = 0.00000001;
    double mass = 1;
    double charge = 1;
    int radius = 30;
    boolean hasPhysics = true;
    Color color = Color.BLUE;

    double[] position = {0,0};
    double[] netForce = {0, 0};
    double[] acceleration = {0, 0};
    double[] velocity = {0, 0};
    boolean isAnchored = false;


    public PhysicsNode(Data d) {
        super(d);
        int flip = Math.random()>0?1:-1;
        int flip2 = Math.random()>0?1:-1;
        setPosition(300+10*Math.random()*flip,300+10*Math.random()*flip);

    }

    public void enablePhysics(){
        hasPhysics = true;
    }
    public void disablePhysics(){
        hasPhysics = false;
    }
    public boolean getPhysics(){
        return hasPhysics;
    }
    public void setPosition(double x, double y){
        position[0] = x;
        position[1] = y;
    }

    public void addTensionForce() {
        if(hasPhysics){
            for (int i = 0; i < outNodes.size(); i++) {
                PhysicsNode nextNode = (PhysicsNode) outNodes.get(i);
                if (nextNode.getPhysics()) {
                    double direction[] = {nextNode.position[0] - position[0], nextNode.position[1] - position[1]};
                    //double depthLength = 100*(nextNode.getDepth() - getDepth());
                    double length = Math.sqrt(direction[0] * direction[0] + direction[1] * direction[1]);
                    direction[0] /= length;
                    direction[1] /= length;
                    double forceMultiplier = hooksConstant * length;
                    if (length < 5 * radius) { // (depth-children.get(i).depth)*
                        forceMultiplier *= -1;
                    }


                    addToForce(direction[0] * forceMultiplier, direction[1] * forceMultiplier);
                    nextNode.addToForce(-direction[0] * forceMultiplier, -direction[1] * forceMultiplier);
                }
            }
        }
    }

    public void addStaticForce(PhysicsNode node) {
        if(hasPhysics) {
            if(node.getPhysics()) {
                double direction[] = {-node.position[0] - position[0], node.position[1] - position[1]};
                double length = Math.sqrt(direction[0] * direction[0] + direction[1] * direction[1]);
                direction[0] /= length;
                direction[1] /= length;
                double forceMultiplier = coulombsConstant * charge * node.charge / (length * length);
                addToForce(-direction[0] * forceMultiplier, -direction[1] * forceMultiplier);
                node.addToForce(direction[0] * forceMultiplier, direction[1] * forceMultiplier);
            }
        }

    }

    public void addToForce(double x, double y) {
        netForce[0] += x;
        netForce[1] += y;

    }

    private void applyForce() {
        acceleration[0] = netForce[0] / mass;
        acceleration[1] = netForce[1] / mass;

        netForce[0] = 0;
        netForce[1] = 0;
    }

    private void applyAcceleration() {
        velocity[0] += acceleration[0];
        velocity[1] += acceleration[1];

        velocity[0] *= 0.99993;
        velocity[1] *= 0.99993;

    }

    protected void applyVelocity() {
        position[0] += velocity[0];
        position[1] += velocity[1];
    }

    public void step() {
        applyForce();
        applyAcceleration();
        if (!isAnchored) {
            applyVelocity();
        }
    }


    public void jiggle(double heat) {
        int flip = Math.random() > 0.5 ? 1 : -1;
        int flip2 = Math.random() > 0.5 ? 1 : -1;

        velocity[0] += heat * Math.random() * flip;
        velocity[1] += heat * Math.random() * flip2;
    }

    public void killMomentum() {

        velocity[0] = 0;
        velocity[1] = 0;

    }

    public void setAnchor(boolean set) {
        isAnchored = set;
    }

    public void findColor(){
        STATES i = getState();
        switch(i){
            case OK: color = Color.BLUE;
                break;
            case DEAD: color = Color.GRAY;
                break;
            case CIRCULAR: color = Color.ORANGE;
                break;
            case ORPHAN: color = Color.GREEN;
                break;
            case ISOLATED: color = Color.BLACK;
                break;
        }
        System.out.println("State is "+i);
    }

    public void draw(Graphics2D g) {
    //    System.out.println("NODE: "+getID()+"\n\tPOSITION: "+(int)position[0]+","+(int)position[1]);
        g.setColor(color);
        int offset = -radius / 2;

        g.fillOval((int) position[0] + offset, (int) position[1] + offset, radius, radius);
        for (Node n : outNodes) {
            g.drawLine((int) position[0], (int) position[1], (int) ((PhysicsNode) n).position[0], (int) ((PhysicsNode) n).position[1]);
        }
    }
}