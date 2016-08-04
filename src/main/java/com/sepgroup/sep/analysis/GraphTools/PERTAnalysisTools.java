package com.sepgroup.sep.analysis.GraphTools;

import com.sepgroup.sep.analysis.*;

/**
 * Created by Andres Gonzalez on 2016-08-03.
 */
public final class PERTAnalysisTools {
    private PERTAnalysisTools(){};

    public static void pertAnalysis(Node currentTask) {
        int projectId = currentTask.getData().task.getProjectId();
        Graph newGraph = new Graph();
        GraphFactory.makeGraph(projectId, newGraph);


    }

}