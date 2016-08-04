package com.sepgroup.sep.analysis;

import com.sepgroup.sep.analysis.GraphTools.NodeIterator;
import java.util.ArrayList;

/**
 * Created by Demo on 7/29/2016.
 */
public class Graph {


    NodeIterator iterator = new NodeIterator();
    ArrayList<Node> nodes = new ArrayList<Node>();
    protected Node cursor;
    protected Node root;
    protected Node terminal;

    int visitedCounter = 0;

    public Graph(){
    }
    public Graph(int projectID){
        GraphFactory.makeGraph(projectID,this);

    }
    public void createNode(){
        Node n = new Node();
    }
    public void creatNode(Data d){
        Node n = new Node(d);
    }
    public void addNode(Node n){
        // ADD BINARY INSERTION
        nodes.add(n);
    }
    public void addDirectedEdge(Node a, Node b){
        a.addOutNode(b);
        b.addInNode(a);
    }
    public void addFreeEdge(Node a, Node b){
        addDirectedEdge(a,b);
        addDirectedEdge(b,a);
    }
    public void dfs(Node n){
        iterator.clear();
        visitedCounter++;
        depthFirstSearch(n);
    }
    public void depthFirstSearch(Node n){
        n.setVisited(visitedCounter);
        iterator.add(n);
        ArrayList<Node> list = n.getOutNodes();
        for(Node node : list){
            if(node.getVisited()!=visitedCounter){
                depthFirstSearch(node);
            }
        }
    }

    public void findAndSetAllStates(){
        for(Node n : nodes){
            findAndSetState(n);
        }
    }
    public void findAndSetState(Node n){
        if(n.getInNodes().size()==0 && n.getOutNodes().size()==0)
            n.setStatus(Node.STATES.ISOLATED);
        else if(root!=null && n!=root && n.getInNodes().size()==0)
            n.setStatus(Node.STATES.ORPHAN);
        else if(terminal!=null && n!=terminal && n.getOutNodes().size()==0)
            n.setStatus(Node.STATES.DEAD);
        else if(hasCircularDependency(n))
            n.setStatus(Node.STATES.CIRCULAR);
        else
            n.setStatus(Node.STATES.OK);

    }
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


    public void moveCursorTo(Node n){
        cursor = n;
    }

    public Node getNodeByID(int id){
        // ADD BINARY SEARCH
        for(Node n : nodes) {
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


    public void findRoot(){

    }
    public void findTerminal(){

    }
    public void sort(){};

    public void printInfo(){
       System.out.println("\n# OF NODES: "+nodes.size()+"\n");
        for(Node n: nodes){
            System.out.println("NODE: "+n.getID()+"\n\tDATA: "+ n.getData().task.getTaskId()+"\t");
                for(Node m : n.getInNodes()){
                    System.out.println("\tPARENT NODE: "+m.getID());
                }
                for(Node m : n.getOutNodes()){
                    System.out.println("\tCHILD NODE: "+m.getID());
                }
        }

    }

    public void update(){}
}


