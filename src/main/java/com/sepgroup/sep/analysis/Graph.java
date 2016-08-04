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

    public void sort(){
        mergeSort(nodes);
    };

    public ArrayList<Node> mergeSort(ArrayList<Node> list)
    {
        if(list.size() <= 1)
            return list;

        ArrayList<Node> left = new ArrayList<Node>();
        ArrayList<Node> right = new ArrayList<Node>();

        for(int i = 0; i < list.size(); i++)
            if(i % 2 == 1)
                left.add(list.get(i));
            else
                right.add(list.get(i));

        left = mergeSort(left);
        right = mergeSort(right);

        return merge(left, right);
    }

    public ArrayList<Node> merge(ArrayList<Node> left, ArrayList<Node> right)
    {
        ArrayList<Node> result = new ArrayList<Node>();

        while(left.size() > 0 && right.size() > 0)
        {
            if(left.get(0).getID() <= right.get(0).getID())
            {
                result.add(left.get(0));
                left.remove(0);
            }
            else
            {
                result.add(right.get(0));
                right.remove(0);
            }
        }

        while(left.size() > 0)
        {
            result.add(left.get(0));
            left.remove(0);
        }

        while(right.size() > 0)
        {
            result.add(right.get(0));
            right.remove(0);
        }

        return result;
    }
    
    public void printInfo(){
       //System.out.println()
//        for(Node n: nodes){

//        }

    }
}


