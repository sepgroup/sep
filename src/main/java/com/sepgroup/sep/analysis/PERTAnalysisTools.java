package com.sepgroup.sep.analysis;

import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres Gonzalez on 2016-08-03.
 */
public final class PERTAnalysisTools {
    private PERTAnalysisTools(){};

    public static double pertAnalysis(TaskModel currentTask, int days) throws ModelNotFoundException
    {
        int projectId = currentTask.getProjectId();
        ProjectModel project;

        project = ProjectModel.getById(projectId);

        Graph newGraph = new Graph();
        GraphFactory.makeGraph(projectId, newGraph);

        setPasses(newGraph, days);

        Node currentNode = newGraph.getNodeByID(currentTask.getTaskId());
        ArrayList<ArrayList<Node>> criticalPaths = getCriticalPath(newGraph, currentNode);
        return calculateProbability(criticalPaths, currentNode, days);
    }

    public static void setPasses(Graph newGraph, int days) throws ModelNotFoundException
    {
        forwardPass(newGraph.getRoot(), 0);
        backwardPass(newGraph.getTerminal(), (int)newGraph.getTerminal().getData().earliestFinish);
    }

    public static double calculateProbability(ArrayList<ArrayList<Node>> criticalPaths, Node currentNode, int days)
    {
        double minVariance = Double.MAX_VALUE;
        ArrayList<Node> minSlackCriticalPath = null;

        for(int i = 0; i < criticalPaths.size(); i++)
        {
            double totalVariance = 0;
            for (int j = 0; j < criticalPaths.get(i).size(); j++) {
                double taskVariance = taskVariance(criticalPaths.get(i).get(j).getData().task);
                totalVariance += taskVariance;
            }
            if(totalVariance < minVariance)
            {
                minVariance = totalVariance;
                minSlackCriticalPath =  criticalPaths.get(i);
            }
        }

        double expectedTime = (double)(currentNode.getData().earliestFinish + currentNode.getData().latestFinish) / 2;

        double sum = 0;
        for(double i = 0; i <= days; i += 0.001) {
            double prob = probability(i - expectedTime, Math.sqrt(minVariance));
            prob *= 0.001;
            sum += prob;
        }
        return sum;
    }

    private static void forwardPass(Node currentNode, int timeSoFar)
    {
        timeSoFar += currentNode.getData().getExpectedDuration();

        if(timeSoFar >= currentNode.getData().earliestFinish)
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

        if(timeSoFar <= currentNode.getData().latestFinish)
            currentNode.getData().latestFinish = timeSoFar;
        else
            return;

        List<Node> nextNodes = currentNode.getInNodes();
        for(Node nextNode: nextNodes)
            backwardPass(nextNode, timeSoFar);
    }

    public static ArrayList<ArrayList<Node>> getCriticalPath(Graph graph, Node targetNode)
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
            node.setCritical(true);
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

    private static double probability(double timeDifference, double standardDeviation)
    {
        return (standardDeviation == 0 ? 0 : Math.exp(- Math.pow(timeDifference / standardDeviation, 2) / 2) / (standardDeviation * Math.sqrt(2 * 3.14159)));
    }
}