package com.sepgroup.sep.analysis.GraphDisplay;

import java.util.Collection;

import com.sepgroup.sep.analysis.CriticalPath;
import com.sepgroup.sep.analysis.TaskNodePath;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by Demo on 8/4/2016.
 */
public class PhysicsGraphController implements KeyInputController{
    private Display display;
    private PhysicsGraph graph;
    private ArrayList<Integer> keyDown = new ArrayList<Integer>(10);

    public PhysicsGraphController(boolean display,int projectID){

        graph = new PhysicsGraph(projectID);
        if(display) {
            setDisplay(new Display());
            addRenderObject(graph);
        }

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
        if(e.getKeyCode() == KeyEvent.VK_P)
            ((PhysicsNode)graph.getCursor()).setPhysics(!((PhysicsNode)graph.getCursor()).getPhysics());

        if(e.getKeyCode() == KeyEvent.VK_T){
            if(graph.getCursor().getOutNodes().contains(graph.getTerminal()))
                graph.getCursor().removeOutNode(graph.getTerminal());
            else
                graph.addDirectedEdge(graph.getCursor(),graph.getTerminal());
        }
        if(e.getKeyCode() == KeyEvent.VK_R){
            if(graph.getCursor().getInNodes().contains(graph.getRoot()))
                graph.getCursor().removeInNode(graph.getRoot());
            else
                graph.addDirectedEdge(graph.getRoot(),graph.getCursor());
        }
    }
    public void releaseEvent(KeyEvent e){

        keyDown.remove(new Integer(e.getKeyCode()));
    }

    private void setDisplay(Display d){
        display = d;
        d.keyController = this;
    }
    private  void setGraph(PhysicsGraph g){
        graph = g;
    }
    private void addRenderObject(Drawable draw){
        if(display!=null)
            display.addRenderObject(draw);
    }


    private void update(){
        doKeyActions();
        if(display!=null)
            display.repaint();
        graph.step();
    }

    private void moveToEdge(){
        graph.moveToEdge();
    }

    public void positionNodes(){
        graph.update();
        for(int i = 0; i<1000000;i++){
       // while(true){
            update();
        }
        moveToEdge();
        graph.setRelativePosition();

    }
    private void setCriticalNodes(){
        graph.setCriticalNodes();
    }
    public PhysicsGraph getGraph(){
        return graph;
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
