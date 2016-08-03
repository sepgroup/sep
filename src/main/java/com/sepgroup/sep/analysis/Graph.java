package com.sepgroup.sep.analysis;


import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Demo on 7/29/2016.
 */
public class Graph {


    ArrayList<Node> iterator;

    ArrayList<Node> nodes = new ArrayList<Node>();
    Node cursor;
    Node tempNode;

    int visitedCounter = 0;

    public Graph(){
    }
    public Graph(int projectID){
        GraphFactory.makeGraph(projectID);
    }
    public void createNode(){
        Node n = new Node();
        tempNode = n;
    }
    public void creatNode(Data d){
        Node n = new Node(d);
        tempNode = n;
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
    public void sort(){};
    
    public void printInfo(){
       //System.out.println()
        for(Node n: nodes){

        }

    }
}


