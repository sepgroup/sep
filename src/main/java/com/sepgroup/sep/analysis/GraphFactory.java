package com.sepgroup.sep.analysis;


import com.sepgroup.sep.analysis.GraphDisplay.PhysicsNode;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.TaskModel;

import java.util.List;

/**
 * Created by Demo on 7/29/2016.
 */

public class GraphFactory {
    static boolean debugMode = false;
    private static Graph graph;

    /* this is the function we call to build graph,
     * we pass the id of the project we want to build
     */
    public static void makeGraph(int projectID,Graph g){
        long t1 = System.currentTimeMillis();
        graph = g;
        try {
            graph.root = new Node(new TaskModel("Start", "Start of project", projectID));
            graph.terminal = new Node(new TaskModel("End", "End of project", projectID));
            graph.addNode(graph.root);

        }
        catch(Exception e)
        {
            return;
        }
        pullTasks(projectID);
        setAdjacents(projectID);
        graph.findAndSetAllStates();
        long t2 = System.currentTimeMillis();
    }

    // pull each task related to the project and assign it to a node
    private static void pullTasks(int projectID){
        List<TaskModel> tasks = null;
        try {
             tasks = TaskModel.getAllByProject(projectID);

        }
        catch(ModelNotFoundException E){
            System.out.println("Project " + projectID + " Not Found");
            return;
        }
        for(TaskModel t : tasks){
            Data d = new Data(t);
            Node n = new PhysicsNode(d);
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
