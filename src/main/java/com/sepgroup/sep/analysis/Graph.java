package com.sepgroup.sep.analysis;

import com.sepgroup.sep.analysis.GraphTools.NodeIterator;

import java.util.*;

/**
 * Created by Demo on 7/29/2016.
 */
public class Graph {


    public NodeIterator iterator = new NodeIterator();
    public ArrayList<Node> nodes = new ArrayList<Node>();
    private int cursorPosition = -1;
    protected Node cursor;
    protected Node root;
    protected Node terminal;


    public Graph(){
    }
    public Graph(int projectID){
        GraphFactory.makeGraph(projectID,this);
    }
    public void addNode(Node n){
        nodes.add(n);
    }
    public void removeNode(int id){
        for(Node n : nodes){
            if(n.getID()==id) {
                removeNode(n);
                break;
            }
        }

    }
    public void removeNode(Node n){
        n.isolate();
        nodes.remove(n);
    }
    public void addDirectedEdge(Node a, Node b){
        a.addOutNode(b);
    }
    public void addNonDirectedEdge(Node a, Node b){
        addDirectedEdge(a,b);
        addDirectedEdge(b,a);
    }
    public void dfs(Node n){
        iterator.clear();
        Node.incrementCounter();
        depthFirstSearch(n);
    }

    public void depthFirstSearch(Node n){
        n.setVisited();
        iterator.add(n);
        ArrayList<Node> list = n.getOutNodes();
        for(Node node : list){
            if(!node.wasVisited()){
                depthFirstSearch(node);
            }
        }
    }

    public void bfs(Node n){
        iterator.clear();
        Node.incrementCounter();
        ArrayDeque<Node> q = new ArrayDeque<>();
        n.setVisited();
        breadthFirstSearch(q,n);
    }
    public void breadthFirstSearch(ArrayDeque<Node> q, Node n) {
        iterator.add(n);
        ArrayList<Node> list = n.getOutNodes();
        for(Node node : list){
            if(!node.wasVisited()){
                node.setVisited();
                q.add(node);
            }
        }
        if(q.size()>0) {
            breadthFirstSearch(q, q.remove());
        }
    }
    public void findAndSetAllStates(){

        for(Node n : nodes){
            findAndSetState(n);
        }
    }
    public void findAndSetState(Node n){
        if(n.getState()!=Node.STATES.ROOT && n.getState()!=Node.STATES.TERMINAL ) {

            if (n.getInNodes().size() == 0 && n.getOutNodes().size() == 0)
                n.setStatus(Node.STATES.ISOLATED);
            else if (n != root && n.getInNodes().size() == 0)
                n.setStatus(Node.STATES.ORPHAN);
            else if (n != terminal && n.getOutNodes().size() == 0)
                n.setStatus(Node.STATES.DEAD);
            else if (hasCircularDependency(n))
                n.setStatus(Node.STATES.CIRCULAR);
            else
                n.setStatus(Node.STATES.OK);

        }
    }
    public void setStateProperties(){};
    public boolean hasCircularDependency(Node n){
        dfs(n);
        while(iterator.hasNext()){
            if(iterator.next().getOutNodes().contains(n)) {
                n.setStatus(Node.STATES.CIRCULAR);
                return true;
            }
        }
        return false;
    }

    public void setDone(){
        for(Node n : nodes)
            n.setDone();
    }

    public void moveCursor(){
        if(cursorPosition<nodes.size()-1)
            cursorPosition++;
        else
            cursorPosition = 0;
        cursor = nodes.get(cursorPosition);
    }

    public Node getNodeByID(int id)
    {
        for (Node n : nodes) {
            if (n.getID() == id)
                return n;
        }
        return null;
    }


    public Node getRoot(){
        return root;
    }
    public Node getTerminal(){
        return terminal;
    }
    public Node getCursor(){
        return cursor;
    }

    public boolean isConnected()
    {
        dfs(getRoot());

        for(int i = 0; i < nodes.size(); i++)
            if(nodes.get(i).wasVisited())
                return false;

        return true;
    }
    
    public void printInfo(){
       System.out.println("\n# OF NODES: "+nodes.size()+"\n");
        for(Node n: nodes){
            System.out.println("NODE: "+n.getID()+"\n\tDATA: "+ n.getData().task.getTaskId()+"\n\tSTATE: "+n.getState());
            System.out.println("\tDEPTH:  "+n.getDepth());
                for(Node m : n.getInNodes()){
                    System.out.println("\tPARENT NODE: "+m.getID());
                }
                for(Node m : n.getOutNodes()){
                    System.out.println("\tCHILD NODE: "+m.getID());
                }
        }

    }
    public void update(){
        root.restDepth();
        findAndSetAllStates();
        setDone();
    }
}


