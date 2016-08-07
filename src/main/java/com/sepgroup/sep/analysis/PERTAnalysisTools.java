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

    public static void pertAnalysis(Node currentTask, int days) {
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
        backwardPass(newGraph.getTerminal(), (int)newGraph.getTerminal().getData().earliestFinish);

        ArrayList<ArrayList<Node>> criticalPaths = new ArrayList<ArrayList<Node>>();
        criticalPaths.add(new ArrayList<Node>());

        backTrack(criticalPaths, criticalPaths.get(criticalPaths.size() - 1), currentTask);

        double minVariance = Double.MAX_VALUE;
        ArrayList<Node> minSlackCriticalPath = null;

        for(int i = 0; i < criticalPaths.size(); i++)
        {
            double totalVariance = 0;
            for (int j = 0; j < criticalPaths.get(i).size(); j++) {
                totalVariance += taskVariance(criticalPaths.get(i).get(j).getData().task);
            }

            if(totalVariance < minVariance)
            {
                minVariance = totalVariance;
                minSlackCriticalPath =  criticalPaths.get(i);
            }
        }

        System.out.println(minSlackCriticalPath);
        System.out.println(zScore(days - (currentTask.getData().earliestFinish + currentTask.getData().latestFinish) / 2, minVariance));
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
            int slack = (int)(nextNode.getData().latestFinish - nextNode.getData().earliestFinish);
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