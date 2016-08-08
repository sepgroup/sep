package com.sepgroup.sep.analysis.GraphDisplay;

import com.sepgroup.sep.analysis.Graph;
import com.sepgroup.sep.analysis.GraphDisplay.PhysicsNode;
import com.sepgroup.sep.analysis.GraphFactory;
import com.sepgroup.sep.analysis.Node;


import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Demo on 8/3/2016.
 */
public class PhysicsGraph extends Graph implements Physics,Drawable {
    public boolean depthSections = false;

    public PhysicsGraph(int projectID){
        super();
        GraphFactory.makeGraph(projectID,this);
        for(Node n : nodes){
            PhysicsNode t = (PhysicsNode)n;
            setPositions(t);
        }
        update();
    }

    void setPositions(PhysicsNode n){
        if(n!=root && n!=terminal) {
            int flip = Math.random() > 0 ? 1 : -1;
            int flip2 = Math.random() > 0 ? 1 : -1;
            n.setPosition(600 + 10 * Math.random() * flip, 400+10 * Math.random() * flip2);
        }
        else {
            int y = 400;

            if (n == root)
                n.setPosition(100, y);
            else
                n.setPosition(1100, y);
        }
    }

    protected void pushPositions(Node currentNode)
    {
        for(Node n: currentNode.getOutNodes()) {
            double diffX = ((PhysicsNode)n).position[0] - ((PhysicsNode)currentNode).position[0];
//            double diffY = ((PhysicsNode)n).position[1] - ((PhysicsNode)currentNode).position[1];

            if(diffX < 1000)
                ((PhysicsNode)n).position[0] = ((PhysicsNode)currentNode).position[0] + 1000;

 //           if(currentNode.getOutNodes().size() > 1) {
 //               if (Math.abs(diffY) < 1000 && Math.abs(diffY) > 10)
 //                   ((PhysicsNode) n).position[1] = ((PhysicsNode) currentNode).position[1] + 1000 * Math.signum(diffY);
 //           }


            pushPositions(n);
        }
    }

    public void shiftCursor(double x, double y){
        if(cursor!=null){
            PhysicsNode t = (PhysicsNode)cursor;
            t.shift(x,y);
        }

    }
    public void lockCursor(){
        if(cursor!=null){
            ((PhysicsNode)cursor).setAnchor(!((PhysicsNode)cursor).getAnchor());
        }
    }
    public void shiftAll(double x, double y){
        for(Node n : nodes){
            PhysicsNode t = (PhysicsNode)n;
            t.shift(x,y);
        }
    }
    public void setStateProperties(){
        for(Node n : nodes){
            PhysicsNode p = (PhysicsNode)n;
            p.findColor();
            if(p.getState() == Node.STATES.ISOLATED) {
                p.setAnchor(true);
                p.setPhysics(false);
            }
            else if(p.getState() == Node.STATES.ROOT || p.getState() == Node.STATES.TERMINAL){
                p.setAnchor(true);
                p.setPhysics(true);
            }
            else
            {
                p.setAnchor(false);
                p.setPhysics(true);
            }
        }
    }
    public void setCharges(){
        for(Node n : nodes){
            PhysicsNode t = (PhysicsNode)n;
            t.setCharge();
        }
    }

    public void jiggle(double heat){
        for(Node n : nodes){
            PhysicsNode t = (PhysicsNode)n;
            t.jiggle(heat);
        }
    }
    public void moveToSections(){
        int separation = terminal.getDepth()/1200;
        for(Node n : nodes){
            PhysicsNode t = (PhysicsNode)n;
            t.setPosition(t.getDepth()*separation,t.position[1]);
        }

    }
    public void step(){
        if(!depthSections) {
            for (int i = 0; i < nodes.size(); i++) {
                ((PhysicsNode) nodes.get(i)).addTensionForce();
                for (int j = i + 1; j < nodes.size(); j++) {
                    ((PhysicsNode) nodes.get(i)).addStaticForce(((PhysicsNode) nodes.get(j)));
                }
            }

            for (Node n : nodes)
                ((PhysicsNode) n).step();
        }
    }

    public void moveToEdge(){
        double minX = 100000, minY = 1000000;
        for(Node n : nodes){
            PhysicsNode t = (PhysicsNode)n;
            if(t.position[0]<minX)
                minX = t.position[0];
            if(t.position[1]<minY)
                minY = t.position[1];
        }
            shiftAll(-minX,-minY);
    }
    public void setRelativePosition(){
        double maxX = 0, maxY = 0;
        for(Node n : nodes){
            PhysicsNode t = (PhysicsNode)n;
            if(t.position[0]>maxX)
                maxX = t.position[0];
            if(t.position[1]>maxY)
                maxY = t.position[1];
        }
        for(Node n : nodes){
            PhysicsNode t = (PhysicsNode)n;
            t.setRelativePosition(maxX,maxY);
        }

    }

    public void draw(Graphics2D g){
        for(Node n : nodes){
            ((PhysicsNode)n).draw(g);
        }
        g.setColor(new Color(230,150,100));
        if(cursor!=null) {
            PhysicsNode t = (PhysicsNode) cursor;
            int offset = -t.radius / 2;

            g.fillOval((int) t.position[0] + offset, (int) t.position[1] + offset, t.radius, t.radius);
        }
    }

    public void update(){
        root.restDepth();
        findAndSetAllStates();
        setStateProperties();
        setCriticalNodes();
        setCharges();
        if(depthSections)
            moveToSections();
    }

}
