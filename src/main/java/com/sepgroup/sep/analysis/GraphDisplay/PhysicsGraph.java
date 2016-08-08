package com.sepgroup.sep.analysis.GraphDisplay;

import com.sepgroup.sep.analysis.Graph;
import com.sepgroup.sep.analysis.GraphDisplay.PhysicsNode;
import com.sepgroup.sep.analysis.GraphFactory;
import com.sepgroup.sep.analysis.Node;


import java.awt.*;
import java.util.ArrayList;

/**
 * Created by HP on 8/3/2016.
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
    public void shiftCursor(double x, double y){
        if(cursor!=null){
            PhysicsNode t = (PhysicsNode)cursor;
            t.setPosition(t.position[0]+x,t.position[1]+y);
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
            t.setPosition(t.position[0]+x,t.position[1]+y);
        }
    }
    public void setStateProperties(){
        for(Node n : nodes){
            PhysicsNode p = (PhysicsNode)n;
            p.findColor();
            if(p.getState() == Node.STATES.ISOLATED) {
                p.setAnchor(true);
                p.disablePhysics();
            }
            else if(p.getState() == Node.STATES.ROOT || p.getState() == Node.STATES.TERMINAL){
                p.setAnchor(true);
                p.enablePhysics();
            }
            else
            {
                p.setAnchor(false);
                p.enablePhysics();
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
    public boolean isDone(){
        for(Node n : nodes){
            PhysicsNode t = (PhysicsNode)n;
            if(t.isMoving())
                return false;
        }
        return true;

    }



    public void draw(Graphics2D g){
        for(Node n : nodes){
            ((PhysicsNode)n).draw(g);
        }
        g.setColor(Color.RED);
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
        setCharges();
        if(depthSections)
            moveToSections();
    }

}
