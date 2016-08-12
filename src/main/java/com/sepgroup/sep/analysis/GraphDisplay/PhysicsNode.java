package com.sepgroup.sep.analysis.GraphDisplay;
import com.sepgroup.sep.analysis.Node;
import com.sepgroup.sep.analysis.Data;

import java.awt.*;
import java.util.ArrayList;


/**
 * Created by HP on 8/3/2016.
 */
public class PhysicsNode extends Node implements Physics,Drawable {
    static double coulombsConstant = 0.01;
    static double hooksConstant = 0.00000003;
    static double jiggleFactor = 0.99993;
    double mass = 1;
    double charge = 1;
    int radius = 30;
    boolean hasPhysics = true;
    Color color;

    double[] position = {0,0};
    double[] netForce = {0, 0};
    double[] acceleration = {0, 0};
    double[] velocity = {0, 0};
    boolean isAnchored = false;

    private double[] relativePosition = {-1,-1};


    public PhysicsNode(Data d) {
        super(d);
        int flip = Math.random()>0?1:-1;
        int flip2 = Math.random()>0?1:-1;
        setPosition(300+10*Math.random()*flip,300+10*Math.random()*flip);

    }

    public void setPhysics(boolean set){
        hasPhysics = set;
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
                   // if (length < 5 * radius) { // (depth-children.get(i).depth)*
                    if(length < 100 *(getDepth() - outNodes.get(i).getDepth())){
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
                double direction[] = {node.position[0] - position[0], node.position[1] - position[1]};
                double length = Math.sqrt(direction[0] * direction[0] + direction[1] * direction[1]);
                direction[0] /= length;
                direction[1] /= length;
                double forceMultiplier = coulombsConstant * charge * node.charge / (length * length);
                addToForce(-direction[0] * forceMultiplier, -direction[1] * forceMultiplier);
                node.addToForce(direction[0] * forceMultiplier, direction[1] * forceMultiplier);
            }
        }

    }
    public void shift(double x, double y){
        position[0]+=x;
        position[1]+=y;
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

        velocity[0] *= jiggleFactor;
        velocity[1] *= jiggleFactor;

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


    public void setCharge(){
        charge = inNodes.size() + outNodes.size();

    }
    public void jiggle(double heat) {
        int flip = Math.random() > 0.5 ? 1 : -1;
        int flip2 = Math.random() > 0.5 ? 1 : -1;

        velocity[0] += heat * Math.random() * flip;
        velocity[1] += heat * Math.random() * flip2;
    }


    public void setRelativePosition(double maxX,double maxY, double offsetX, double offsetY){
        if(maxX == 0)
        {
            offsetX = -position[0] + 0.5;
            maxX = 1;
        }
        if(maxY == 0)
        {
            offsetY = -position[1] + 0.5;
            maxY = 1;
        }
        relativePosition[0] = (position[0] + offsetX) / maxX;
        relativePosition[1] = (position[1] + offsetY) / maxY;
    }
    public double getRelX(){
        return relativePosition[0];
    }
    public double getRelY(){
        return relativePosition[1];
    }

    public void setAnchor(boolean set) {
        isAnchored = set;
    }
    public boolean getAnchor(){
        return isAnchored;
    }
    public void findColor(){
        if(getCritical())
            color = new Color(233,136,146);
       else
            color = new Color(192,229,213);
    }

    // Chinchilla
    public void draw(Graphics2D g) {
        g.setColor(color);
        int offset = -radius / 2;

        g.fillOval((int) position[0] + offset, (int) position[1] + offset, radius, radius);
        for (Node n : outNodes) {
            g.drawLine((int) position[0], (int) position[1], (int) ((PhysicsNode) n).position[0], (int) ((PhysicsNode) n).position[1]);
        }
    }
}