package com.sepgroup.sep.analysis;

/**
 * Created by Demo on 7/29/2016.
 */
import java.util.ArrayList;
public class Node {
    public enum STATES{OK,DEAD,CIRCULAR,ORPHAN,ISOLATED};
    protected int nodeID;
    protected int visitedCounter = 0;

    protected ArrayList<Node> outNodes = new ArrayList<Node>();
    protected ArrayList<Node> inNodes = new ArrayList<Node>();

    protected Data data;
    STATES state;

    public Node(){
    }
    public Node(Node n){
        addInNode(n);
    }
    public Node(Data d){
        setData(d);
    }
    public Node(Node n, Data d){
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
    void isolate(){
        removeNode(inNodes,this);
        removeNode(outNodes,this);
        inNodes.clear();
        outNodes.clear();
    }

    // when removing a node, it's also necessary to remove all the adjacencies to it from other nodes to avoid null references

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
    void setNodeID(int id){
        nodeID = id;
    }
    void setStatus(STATES s){
        state = s;
    }
    public STATES getState(){
        return state;
    }
    public int getID(){
        return nodeID;
    }
    public Data getData(){
        return data;
    }
    public int getVisited(){
        return visitedCounter;
    }
    public void setVisited(int v){
        visitedCounter = v;
    }

    public ArrayList<Node> getInNodes(){
        return inNodes;
    }
    public ArrayList<Node> getOutNodes(){
        return outNodes;
    }


    public void forwardPass() {
        data.earliestFinish =
                (data.earliestStart = latestOfEarliestParentFinishes()) + data.task.getExpectedDuration();
        outNodes.forEach(Node::forwardPass);
    }

    public void backwardsPass() {
        float earliest = earliestOfLatestParentFinishes();
        if (earliest < Float.MAX_VALUE)
            data.latestFinish = earliest;

        data.latestStart = data.latestFinish - data.task.getExpectedDuration();
        inNodes.forEach(Node::backwardsPass);
    }

    private float latestOfEarliestParentFinishes() {
        float max = 0.0f;

        for (final Node parent : inNodes) {
            if (parent.data.earliestFinish > max)
                max = parent.data.earliestFinish;
        }

        return max;
    }

    private float earliestOfLatestParentFinishes() {
        float min = Float.MAX_VALUE;

        for (final Node parent : outNodes) {
            if (parent.data.latestFinish < min)
                min = parent.data.latestFinish;
        }

        return min;
    }

}
