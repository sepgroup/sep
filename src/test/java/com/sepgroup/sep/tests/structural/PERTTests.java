package com.sepgroup.sep.tests.structural;

import com.sepgroup.sep.analysis.Graph;
import com.sepgroup.sep.analysis.GraphFactory;
import com.sepgroup.sep.analysis.Node;
import com.sepgroup.sep.analysis.PERTAnalysisTools;
import com.sepgroup.sep.model.*;
import com.sepgroup.sep.model.DBManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ArrayList;

import java.util.ArrayDeque;

import static org.junit.Assert.assertTrue;

/**
 * Created by Andres Gonzalez on 2016-08-14.
 */
public class PERTTests {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DBManager.createDBTablesIfNotExisting();
        ProjectModel project = new ProjectModel("Test", null, null, 100, false, null, "");
        project.persistData();
        TaskModel task = new TaskModel("TestTask", "", 1);
        task.persistData();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DBManager.dropAllDBTables();
    }

    //S5-1
    @Test
    public void testEmptySetAndDaysZero() {
        ArrayList<ArrayList<Node>> paths = new ArrayList<ArrayList<Node>>();
        Node currentNode = new Node();
        try {
            double output = PERTAnalysisTools.calculateProbability(paths, currentNode, 0);
            assert(false);
        }
        catch(Exception e)
        {
            assert(true);
        }
    }

    //S5-2
    @Test
    public void testEmptyPaths() {
        ArrayList<ArrayList<Node>> paths = new ArrayList<ArrayList<Node>>();
        paths.add(new ArrayList<Node>());
        paths.add(new ArrayList<Node>());
        paths.get(1).add(new Node());
        paths.get(1).add(new Node());
        Node currentNode = new Node();
        try {
            double output = PERTAnalysisTools.calculateProbability(paths, currentNode, 50);
            assert(false);
        }
        catch(Exception e)
        {
            assert(true);
        }
    }

    //S5-3
    @Test
    public void testZeroDays() {
        Graph graph = new Graph();
        GraphFactory.makeGraph(1, graph);
        ArrayList<ArrayList<Node>> paths =  PERTAnalysisTools.getCriticalPath(graph, graph.getTerminal());
        try {
            double output = PERTAnalysisTools.calculateProbability(paths, graph.getTerminal(), 0);
            assert(true);
        }
        catch(Exception e)
        {
            assert(false);
        }
    }

    //S5-4
    @Test
    public void testPERT() {
        Graph graph = new Graph();
        GraphFactory.makeGraph(1, graph);
        ArrayList<ArrayList<Node>> paths =  PERTAnalysisTools.getCriticalPath(graph, graph.getTerminal());
        try {
            double output = PERTAnalysisTools.calculateProbability(paths, graph.getTerminal(), 50);
            assert(true);
        }
        catch(Exception e)
        {
            assert(false);
        }
    }
}
