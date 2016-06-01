package com.sepgroup.sep.controller;

import com.sepgroup.sep.model.ProjectModel;

/**
 * Created by jeremybrown on 2016-06-01.
 */
public class ProjectViewerController extends AbstractController {

    ProjectModel model;

    public ProjectViewerController(ProjectModel p) {
        model = p;
        setFxmlPath("/views/projectviewer.fxml");
        setCssPath("/style/stylesheet.css");
    }

    @Override
    public void update() {
        // TODO
    }
}
