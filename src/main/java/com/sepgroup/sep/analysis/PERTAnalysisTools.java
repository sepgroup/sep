package com.sepgroup.sep.analysis;

import com.sepgroup.sep.model.*;
import com.sepgroup.sep.analysis.*;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Created by Andres Gonzalez on 2016-08-03.
 */
public final class PERTAnalysisTools {
    private PERTAnalysisTools(){};

    public static void pertAnalysis(Node currentTask) {
        int projectId = currentTask.getData().task.getProjectId();
        ProjectModel project;

        try {
            project = ProjectModel.getById(projectId);
        }
        catch(Exception e)
        {
            return;
        }

        Graph newGraph = new Graph();
        GraphFactory.makeGraph(projectId, newGraph);

        forwardPass(newGraph.getRoot(), 0);
        backwardPass(newGraph.getTerminal(), newGraph.getTerminal().getData().earliestFinish);

        // Calculate the total variance for each path and obtain the smallest (least slack)
        // Then calculate probability with the std dev
    }

    private static void forwardPass(Node currentNode, int timeSoFar)
    {
        timeSoFar += currentNode.getData().getExpectedDuration();

        if(timeSoFar > currentNode.getData().earliestFinish)
            currentNode.getData().earliestFinish = timeSoFar;
        else
            return;

        List<Node> nextNodes = currentNode.getOutNodes();
        for(Node nextNode: nextNodes)
            forwardPass(nextNode, timeSoFar);
    }

    private static void backwardPass(Node currentNode, int timeSoFar)
    {
        timeSoFar -= currentNode.getData().getExpectedDuration();

        if(timeSoFar < currentNode.getData().latestFinish)
            currentNode.getData().latestFinish = timeSoFar;
        else
            return;

        List<Node> nextNodes = currentNode.getInNodes();
        for(Node nextNode: nextNodes)
            forwardPass(nextNode, timeSoFar);
    }

    private static ArrayList<ArrayList<Node>> getCriticalPath(Graph graph, Node targetNode)
    {
        ArrayList<ArrayList<Node>> allPaths = new ArrayList<ArrayList<Node>>();

        allPaths.add(new ArrayList<Node>());

        backTrack(allPaths, allPaths.get(0), targetNode);

        return allPaths;
    }

    private static void backTrack(ArrayList<ArrayList<Node>> paths, ArrayList<Node> currentPath, Node currentNode)
    {
        currentPath.add(0, currentNode);

        ArrayList<Node> nextNodes = currentNode.getInNodes();
        int min = Integer.MAX_VALUE;
        ArrayList<Node> minNodes = new ArrayList<Node>();
        for(Node nextNode: nextNodes)
        {
            int slack = nextNode.getData().latestFinish - nextNode.getData().earliestFinish;
            if(slack < min)
            {
                min = slack;
                minNodes.clear();
                minNodes.add(nextNode);
            }
            else if(slack == min)
                minNodes.add(nextNode);
        }

        int i = 0;
        for(Node node: minNodes)
        {
            if (i > 0)
            {
                paths.add(new ArrayList<Node>(currentPath));
                backTrack(paths, paths.get(paths.size() - 1), node);
            }
            else
                backTrack(paths, currentPath, node);

            i++;
        }
    }

    private static double taskVariance(TaskModel task)
    {
        return Math.pow((task.getPesimisticTimeToFinish() - task.getOptimisticTimeToFinish()) / 6, 2);
    }

    private static double zScore(double timeDifference, double standardDeviation)
    {
        return timeDifference / standardDeviation;
    }
}