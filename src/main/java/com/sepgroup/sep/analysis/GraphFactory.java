package com.sepgroup.sep.analysis;


import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.TaskModel;

import java.util.List;

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
        List<TaskModel> tasks = null;
        try {
             tasks = TaskModel.getAllByProject(projectID);
        }
        catch(ModelNotFoundException E){
            System.out.println("Project"+ projectID +" Not Found");
            return;
        }
        for(TaskModel t : tasks){
            Data d = new Data(t);
            Node n = new Node(d);
            n.setNodeID(t.getTaskId());
            graph.addNode(n);
        }
        //graph.sort();
    }

    // set adjacencies between each task using nodes
    private static void setAdjacents(int projectID){
        List<TaskModel> dependencies = null;
            for(Node n : graph.nodes){
                dependencies = n.getData().task.getDependencies();
                for(TaskModel t:dependencies){
                    graph.addDirectedEdge(graph.getNodeByID(t.getTaskId()),n);
                }
            }

    }
}
