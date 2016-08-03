package com.sepgroup.sep.analysis;


/**
 * Created by Demo on 7/29/2016.
 */

public class GraphFactory {
    private static Graph graph;

    /* this is the function we call to build graph,
     * we pass the id of the project we want to build
     */
    public static Graph makeGraph(int projectID){
        graph = new Graph();
        pullTasks(projectID);
        setAdjacents(projectID);
        return graph;
    }
    private static void instantiate(){
        graph = new Graph();
    }

    // pull each task related to the project and assign it to a node
    private static void pullTasks(int projectID){

    }

    // set adjacencies between each task using nodes
    private static void setAdjacents(int projectID){

    }
}
