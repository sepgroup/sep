package com.sepgroup.sep.analysis;


import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.TaskModel;

import java.util.List;

/**
 * Created by Demo on 7/29/2016.
 */

public class GraphFactory {
    static boolean debugMode = true;
    private static Graph graph;

    /* this is the function we call to build graph,
     * we pass the id of the project we want to build
     */
    public static void makeGraph(int projectID,Graph g){
        long t1 = System.currentTimeMillis();
        graph = g;
        pullTasks(projectID);
        setAdjacents(projectID);
        long t2 = System.currentTimeMillis();
        System.out.println("Time to make graph: "+(t2-t1));
    }

    // pull each task related to the project and assign it to a node
    private static void pullTasks(int projectID){
        List<TaskModel> tasks = null;
        try {
             tasks = TaskModel.getAllByProject(projectID);

            if(debugMode)
                System.out.println("Got project 1, # OF TASKS: "+tasks.size());
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

                if(debugMode)
                    System.out.println("added Node to graph");
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
