package com.sepgroup.sep.analysis;

import com.sepgroup.sep.analysis.GraphDisplay.PhysicsGraph;
import com.sepgroup.sep.analysis.GraphDisplay.PhysicsNode;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.TaskModel;

import java.util.List;

/**
 * Created by Demo on 7/29/2016.
 */

public class GraphFactory {
    static boolean debugMode = false;

    /* this is the function we call to build graph,
     * we pass the id of the project we want to build
     */
    public static void makeGraph(int projectID,Graph graph){
        pullTasks(projectID,graph);
        setAdjacents(projectID,graph);
        setStates(graph);
        makeEndpoints(projectID,graph);
        setEndpoints(projectID,graph);
        setStates(graph);
    }
    public static void makeGraph(int projectID,PhysicsGraph graph){
        pullTasks(projectID,graph);
        setAdjacents(projectID,graph);
        setStates(graph);
        makeEndpoints(projectID,graph);
        setEndpoints(projectID,graph);
        setStates(graph);
    }

    // pull each task related to the project and assign it to a node
    private static void pullTasks(int projectID,Graph graph){
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
            Node n = new Node(d);
            n.setNodeID(t.getTaskId());
            graph.addNode(n);
        }
    }
    private static void pullTasks(int projectID,PhysicsGraph graph){
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
    }


    // set adjacencies between each task using nodes
    private static void setAdjacents(int projectID,Graph graph ){
        List<TaskModel> dependencies = null;
            for(Node n : graph.nodes){
                dependencies = n.getData().task.getDependencies();
                for(TaskModel t:dependencies){
                    graph.addDirectedEdge(graph.getNodeByID(t.getTaskId()),n);
                }
            }

    }
    private static void setStates(Graph graph){
        graph.findAndSetAllStates();
        graph.setStateProperties();
    }
    private static void makeEndpoints(int projectID,Graph graph){
        try {
            TaskModel t1 = new TaskModel("Start", "Start of project", projectID);
            TaskModel t2 = new TaskModel("End", "End of project", projectID);
            graph.root = new Node(new Data(t1) );
            graph.terminal = new Node(new Data(t2));
            graph.root.setNodeID(-1);
            graph.terminal.setNodeID(-2);

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    private static void makeEndpoints(int projectID,PhysicsGraph graph){
        try {
            TaskModel t1 = new TaskModel("Start", "Start of project", projectID);
            TaskModel t2 = new TaskModel("End", "End of project", projectID);
            graph.root = new PhysicsNode(new Data(t1) );
            graph.terminal = new PhysicsNode(new Data(t2));
            graph.root.setNodeID(-1);
            graph.terminal.setNodeID(-2);

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    private static void setEndpoints(int projectID,Graph graph){

        graph.root.setStatus(Node.STATES.ROOT);
        graph.terminal.setStatus(Node.STATES.TERMINAL);

        for(Node n : graph.nodes){
            if(n.getState() == Node.STATES.ORPHAN)
                graph.addDirectedEdge(graph.root,n);
            else if(n.getState() == Node.STATES.DEAD)
                graph.addDirectedEdge(n,graph.terminal);
            else if(n.getState() == Node.STATES.ISOLATED){
                graph.addDirectedEdge(graph.root,n);
                graph.addDirectedEdge(n,graph.terminal);
            }

        }

        graph.addNode(graph.root);
        graph.addNode(graph.terminal);
    }
}
