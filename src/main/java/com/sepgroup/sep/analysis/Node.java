package com.sepgroup.sep.analysis;

/**
 * Created by Demo on 7/29/2016.
 */
import java.util.ArrayList;
public class Node {
    protected static int nextID = 0;
    protected int nodeID;
    protected int visitedCounter = 0;

    protected ArrayList<Node> outNodes = new ArrayList<Node>();
    protected ArrayList<Node> inNodes = new ArrayList<Node>();

    protected Data data;


    public Node(){
        nodeID = nextID++;
    }
    public Node(Node n){
        nodeID = nextID++;
        addInNode(n);
    }
    public Node(Data d){
        nodeID = nextID++;
        setData(d);
    }
    public Node(Node n, Data d){
        nodeID = nextID++;
        addInNode(n);
        setData(d);
    }

    void addInNode(Node n){
        addNode(inNodes,n);
    }
    void addOutNode(Node n){
        addNode(outNodes,n);
    }
    void addNode(ArrayList<Node> list, Node n){
        if(!list.contains(n)) {
            list.add(n);
        }

    }
    void removeInNode(Node n){
        removeNode(inNodes,n);
    }
    void removeInNode(int id){
        removeNode(inNodes, id);
    }
    void removeOutNode(Node n){
        removeNode(outNodes,n);
    }
    void removeOutNode(int id){
        removeNode(outNodes, id);
    }

    void removeNode(ArrayList<Node> list, Node n){
        if(list.contains(n))
            list.remove(n);
    }
    void removeNode(ArrayList<Node> list,int id){
        for(Node n : list)
            if(n.nodeID == id) {
                list.remove(n);
                break;
            }
    }


    void setData(Data d){
        data = d;
    }
    int getID(){
        return nodeID;
    }
    Data getData(){
        return data;
    }
    int getVisited(){
        return visitedCounter;
    }
    void setVisited(int v){
        visitedCounter = v;
    }


    ArrayList<Node> getInNodes(){
        return inNodes;
    }
    ArrayList<Node> getOutNodes(){
        return outNodes;
    }


}
