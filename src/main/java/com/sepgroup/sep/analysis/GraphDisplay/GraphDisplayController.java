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
        graph.findAndSetAllStates();
        graph.setStateProperties();
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
        int velX = 0, velY = 0;
        if(keyDown.contains(KeyEvent.VK_UP)){
            velY--;
        }
        if(keyDown.contains(KeyEvent.VK_DOWN)){
            velY++;
        }
        if(keyDown.contains(KeyEvent.VK_LEFT)){
            velX--;
        }
        if(keyDown.contains(KeyEvent.VK_RIGHT)){
            velX++;
        }
        if(velX!=0 || velY!=0)
            graph.shiftAll(velX,velY);
    }
}
