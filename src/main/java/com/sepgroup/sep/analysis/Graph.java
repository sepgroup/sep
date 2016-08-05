package com.sepgroup.sep.analysis;

import com.sepgroup.sep.analysis.GraphTools.NodeIterator;
import java.util.ArrayList;

/**
 * Created by Demo on 7/29/2016.
 */
public class Graph {


    NodeIterator iterator = new NodeIterator();
    public ArrayList<Node> nodes = new ArrayList<Node>();
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
   //     binaryInsertion(n, 0, nodes.size() - 1);
        nodes.add(n);
    }
    public void removeNode(int id){
    //    removeNode(binarySearch(id, 0, nodes.size() - 1));
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
        else if(n!=root && n.getInNodes().size()==0)
            n.setStatus(Node.STATES.ORPHAN);
        else if(n!=terminal && n.getOutNodes().size()==0)
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

    public Node getNodeByID(int id)
    {
     //   if(nodes.size() == 0)
      //      return null;
      //  else
        //    return binarySearch(id, 0, nodes.size() - 1);
        for (Node n : nodes) {
            if (n.getID() == id)
                return n;
        }
        return null;
    }

    public Node binarySearch(int id, int min, int max)
    {
        int span = max - min;
        int midPoint = (max + min) >> 1;

        if(nodes.get(midPoint).getID() == id)
        {
            return nodes.get(midPoint);
        }
        else if(span == 0)
        {
            return null;
        }
        else if(nodes.get(midPoint).getID() < id)
        {
            return binarySearch(id, min, midPoint);
        }
        else
        {
            return binarySearch(id, midPoint, max);
        }
    }

    public void binaryInsertion(Node newNode, int min, int max)
    {
        int midPoint = (max + min) >> 1;

        if(newNode.getID() > nodes.get(max).getID())
        {
            nodes.add(max, newNode);
        }
        else if(newNode.getID() < nodes.get(min).getID())
        {
            nodes.add(min, newNode);
        }
        else if(newNode.getID() > nodes.get(min).getID() && newNode.getID() < nodes.get(midPoint).getID())
        {
            binaryInsertion(newNode, min, midPoint);
        }
        else if(newNode.getID() > nodes.get(midPoint).getID() && newNode.getID() < nodes.get(max).getID())
        {
            binaryInsertion(newNode, midPoint, max);
        }
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

    public void sort(){
        mergeSort(nodes);
    }

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


