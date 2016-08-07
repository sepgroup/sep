package com.sepgroup.sep.analysis.GraphDisplay;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by HP on 8/4/2016.
 */
public class GraphDisplayController implements KeyInputController{
    public Display display;
    public boolean animate = true;
    public boolean needsGraphUpdate = true;
    PhysicsGraph graph;
    ArrayList<Integer> keyDown = new ArrayList<Integer>(10);

    public GraphDisplayController(){
        setDisplay(new Display());
    }
    public void pressEvent(KeyEvent e){
        if(!keyDown.contains(e.getKeyCode()))
            keyDown.add(new Integer(e.getKeyCode()));

        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            graph.moveCursor();
        }
        if(e.getKeyCode() == KeyEvent.VK_L){
            graph.lockCursor();
        }
        if(e.getKeyCode() == KeyEvent.VK_I){
            graph.printInfo();
        }
        if(e.getKeyCode() == KeyEvent.VK_D){
            graph.depthSections = !graph.depthSections;
            graph.update();
        }
    }
    public void releaseEvent(KeyEvent e){

        keyDown.remove(new Integer(e.getKeyCode()));
    }

    public void setDisplay(Display d){
        display = d;
        d.keyController = this;
    }
   public  void setGraph(PhysicsGraph g){
        graph = g;
    }
    public void addRenderObject(Drawable draw){
        display.addRenderObject(draw);
    }

    public void graphUpdate(){
     //   graph.findAndSetAllStates();
     //   graph.setStateProperties();
        needsGraphUpdate = false;
    }

    public void PhysicsUpdate(){
        doKeyActions();
        display.repaint();
        graph.step();
    }
    public void update(){
       if(needsGraphUpdate)
            graphUpdate();
        if(animate)
            PhysicsUpdate();
    }

    void doKeyActions(){
        double velX = 0, velY = 0;
        double speed = 0.001;
        double heat = 0.0001;
        if(keyDown.contains(KeyEvent.VK_UP)){
            velY-=speed;
        }
        if(keyDown.contains(KeyEvent.VK_DOWN)){
            velY+=speed;
        }
        if(keyDown.contains(KeyEvent.VK_LEFT)){
            velX-=speed;
        }
        if(keyDown.contains(KeyEvent.VK_RIGHT)){
            velX+=speed;
        }
        if(velX!=0 || velY!=0) {
            if(keyDown.contains(KeyEvent.VK_SHIFT))
                graph.shiftAll(velX, velY);
            else
                graph.shiftCursor(velX,velY);
        }

        if(keyDown.contains(KeyEvent.VK_H)){
            graph.jiggle(heat*200);
        }
        if(keyDown.contains(KeyEvent.VK_J)){
            graph.jiggle(heat);
        }
    }
}
