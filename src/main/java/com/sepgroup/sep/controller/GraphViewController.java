package com.sepgroup.sep.controller;

import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.analysis.*;
import com.sepgroup.sep.analysis.GraphDisplay.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.layout.Pane;
import javafx.scene.control.Button;

/**
 * Created by Andres Gonzalez on 2016-08-07.
 */
public class GraphViewController extends AbstractController{

    private static Logger logger = LoggerFactory.getLogger(TaskViewerController.class);
    private static final String fxmlPath = "/views/graphview.fxml";
    private ProjectModel project;

    @FXML
    public Pane graphArea;

    public GraphViewController() {
        setCssPath("/style/stylesheet.css");
    }

    public static String getFxmlPath() {
        return fxmlPath;
    }

    public void setReturnProject(ProjectModel p) {
        project = p;
        update();
    }

    public void update() {
        PhysicsGraphController pgc = new PhysicsGraphController(false, project.getProjectId());
        pgc.positionNodes();

        for(Node n: pgc.getGraph().nodes)
        {
            graphArea.getChildren().add(new Button(n.getData().task.getName()));
            System.out.println(((PhysicsNode)n).getRelX());
            graphArea.getChildren().get(graphArea.getChildren().size() - 1).setLayoutX(graphArea.getPrefWidth() * ((PhysicsNode)n).getRelX());
            graphArea.getChildren().get(graphArea.getChildren().size() - 1).setLayoutY(graphArea.getPrefHeight() * ((PhysicsNode)n).getRelY());
        }

    }
}
