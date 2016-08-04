package com.sepgroup.sep.analysis.GraphTools;

import com.sepgroup.sep.analysis.Node;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by HP on 8/3/2016.
 */
public class NodeIterator implements Iterator<Node>{
    ArrayList<Node> list;
    public NodeIterator(){};
    int index = 0;
    public NodeIterator(ArrayList<Node> list){
        this.list = list;
    }
    public void add(Node n){
        list.add(n);
    }
    public boolean hasNext(){
        return list.size()>index;
    }
    public Node next(){
        if(hasNext())
            return list.get(index++);
        else
            return null;
    }
    public void clear(){
        list.clear();
    }

}
