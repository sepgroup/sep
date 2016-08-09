package com.sepgroup.sep.analysis.GraphDisplay;

import java.util.Collection;

import com.sepgroup.sep.analysis.CriticalPath;
import com.sepgroup.sep.analysis.TaskNodePath;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import com.sepgroup.sep.analysis.Node;


/**
 * Created by Demo on 8/4/2016.
 */
public class PhysicsGraphController implements KeyInputController{
    private Display display;
    private PhysicsGraph graph;
    private ArrayList<Integer> keyDown = new ArrayList<Integer>(10);
    private boolean reset = false;

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
            reset = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_P)
            ((PhysicsNode)graph.getCursor()).setPhysics(!((PhysicsNode)graph.getCursor()).getPhysics());

        if(e.getKeyCode() == KeyEvent.VK_T){
            linkUnlinkNodeToTerminal(graph.getCursor());
        }
        if(e.getKeyCode() == KeyEvent.VK_R){
            linkUnlinkRootToNode(graph.getCursor());
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
        loadSequentially();
        for(int i = 0; i<10000;i++){
       // while(true){
            update();
          //  if(reset){
          //      loadSequentially();
          //      reset = false;
          //  }
        }
        moveToEdge();
        graph.setRelativePosition();
    }
    private void loadSequentially(){
        graph.disableAllPhysics();
        graph.positionAllNodes( ((PhysicsNode)graph.getTerminal()).position[0]-100, ((PhysicsNode)graph.getTerminal()).position[1]+1);
        graph.bfs(graph.getRoot());
        while(graph.iterator.hasNext()) {
            Node n = graph.iterator.next();
            if(!n.equals(graph.getRoot()) && !n.equals(graph.getTerminal())) {
                loadNode(n);
                for (int i = 0; i < 100000; i++)
                    update();
            }
        }

    }
    private void loadNode(Node n){
        ((PhysicsNode)n).setPhysics(true);
        ((PhysicsNode)n).setAnchor(false);

        ArrayList<Node> inNodes = n.getInNodes();
        for(Node node : inNodes)
            if(node.getOutNodes().contains(graph.getTerminal()))
                linkUnlinkNodeToTerminal(node);
        if(!n.getOutNodes().contains(graph.getTerminal())){
            linkUnlinkNodeToTerminal(n);
        }


    }
    private void linkUnlinkRootToNode(Node n){
        if(n.getInNodes().contains(graph.getRoot()))
           n.getInNodes().remove(graph.getRoot());
        else
            graph.addDirectedEdge(graph.getRoot(),n);
    }
    private void linkUnlinkNodeToTerminal(Node n){
        if(n.getOutNodes().contains(graph.getTerminal()))
            n.getOutNodes().remove(graph.getTerminal());
        else
            graph.addDirectedEdge(n,graph.getTerminal());
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
