package com.sepgroup.sep.analysis;


import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Demo on 7/29/2016.
 */
public class Graph {


    ArrayList<Node> iterator;

    ArrayList<Node> nodes = new ArrayList<Node>();
    private Node cursor;
    private Node root;
    private Node terminal;

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

    public void moveCursorTo(Node n){
        cursor = n;
    }

    public Node next(){
        if(iterator.size()>0) {
            return iterator.remove(0);
        }
        return null;
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
    public void sort(){};

    public void printInfo(){
       System.out.println("# OF NODES: "+nodes.size()+"\n");
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
}


