package com.sepgroup.sep.analysis.GraphDisplay;

import com.sepgroup.sep.analysis.Graph;
import com.sepgroup.sep.analysis.GraphDisplay.PhysicsNode;
import com.sepgroup.sep.analysis.Node;


import java.awt.*;
import java.util.ArrayList;

/**
 * Created by HP on 8/3/2016.
 */
public class PhysicsGraph extends Graph implements Physics,Drawable {


    public PhysicsGraph(int projectID){
        super(projectID);
    }

    void addNode(PhysicsNode n){
        super.addNode(n);
        int flip = Math.random()>0?1:-1;
        int flip2 = Math.random()>0?1:-1;
        n.setPosition(300+10*Math.random()*flip,300+10*Math.random()*flip);

    }
    public void shiftAll(int x, int y){
        double speed = 0.00003;
        for(Node n : nodes){
            PhysicsNode t = (PhysicsNode)n;
      //      System.out.print("MOVED NODE: "+t.getID()+"\n\tFROM - "+t.position[0]+","+t.position[1]);
            t.setPosition(t.position[0]+x*speed,t.position[1]+y*speed);
      //      System.out.println("\n\tTO - "+t.position[0]+","+t.position[1]);
        }

    }
    public void step(){
        for(int i = 0; i < nodes.size(); i++){
            ((PhysicsNode)nodes.get(i)).addTensionForce();
            for(int j = i+1; j < nodes.size();j++){
                ((PhysicsNode) nodes.get(i)).addStaticForce(((PhysicsNode)nodes.get(j)));
            }
        }

        for(Node n : nodes)
            ((PhysicsNode)n).step();

    }
    public void draw(Graphics2D g){
        for(Node n : nodes)
            ((PhysicsNode)n).draw(g);
    //    g.setColor(Color.RED);
     //   g.fillOval(300,300,20,20);

    }

}
