package com.sepgroup.sep.controller;

import com.sepgroup.sep.model.TaskModel;

/**
 * Created by jeremybrown on 2016-06-01.
 */
public class TaskEditorController extends AbstractController {

    private TaskModel model;

    public TaskEditorController() {
        setFxmlPath("/views/welcome.fxml");
        setCssPath("/style/stylesheet.css");
    }

    public void setModel(TaskModel t) {
        this.model = t;
        update();
    }

    @Override
    public void update() {
        // None needed for this controller
    }
}
