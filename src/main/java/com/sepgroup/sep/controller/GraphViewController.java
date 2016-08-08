package com.sepgroup.sep.controller;

import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Andres Gonzalez on 2016-08-07.
 */
public class GraphViewController extends AbstractController{

    private static Logger logger = LoggerFactory.getLogger(TaskViewerController.class);
    private static final String fxmlPath = "/views/graphview.fxml";
    private ProjectModel project;

    @FXML
    public Label label;

    public GraphViewController() {
        setCssPath("/style/stylesheet.css");
    }

    public static String getFxmlPath() {
        return fxmlPath;
    }

    public void setReturnProject(ProjectModel p) {
        project = p;
    }

    public void update() {

    }
}
