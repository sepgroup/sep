package com.sepgroup.sep.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.*;
import com.sepgroup.sep.utils.DateUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Andres Gonzalez on 2016-08-06.
 */
public class TaskViewerController extends AbstractController {

    private static Logger logger = LoggerFactory.getLogger(TaskViewerController.class);
    private static final String fxmlPath = "/views/taskviewer.fxml";
    private TaskModel model;
    private ProjectModel project;

    @FXML
    public Label taskIdLabel;
    @FXML
    public Label nameLabel;
    @FXML
    public Label description;
    @FXML
    public Label status;
    @FXML
    public Label expectedDuration;
    @FXML
    public Label budget;
    @FXML
    public Label pertPercentage;
    @FXML
    public Pane pertInfo;
    @FXML
    public DatePicker pertDate;

    public TaskViewerController() {
        setCssPath("/style/stylesheet.css");
    }

    public static String getFxmlPath() {
        return fxmlPath;
    }

    public void setReturnProject(ProjectModel p) {
        project = p;
    }

    @FXML
    public void onCancelClicked() {
        // Return to project viewer
        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
        pvc.setModel(project);
    }

    @FXML
    public void onPERTClicked() {
        System.out.println(pertDate.getValue());
        if(pertDate.getValue() != null)
        {
            pertPercentage.setStyle("-fx-text-inner-color: red;");
            pertPercentage.setVisible(true);
            pertPercentage.setManaged(true);
        }
    }

    @FXML
    public void onEditClicked() {
        TaskEditorController tec = (TaskEditorController) Main.setPrimaryScene(TaskEditorController.getFxmlPath());
        tec.setModel(model);
        tec.setReturnProject(project);
    }

    public void setModel(TaskModel t) {
        this.model = t;
        update();
    }

    public void update() {
        taskIdLabel.setText(String.valueOf(model.getTaskId()));
        nameLabel.setText(String.valueOf(model.getName()));
        budget.setText("$" + String.valueOf(String.format("%.2f", model.getBudget())));
        expectedDuration.setText(String.valueOf(String.format("%.2f", model.getExpectedDuration())) + " day" + (model.getExpectedDuration() > 1 ? "s": ""));

        if(model.getDescription() != null && !model.getDescription().equals(""))
            description.setText(model.getDescription());
        else {
            description.setVisible(false);
            description.setManaged(false);
        }

        status.setText("In Progress");
        if(!model.isDone() && !model.shouldBeDone()) {
            pertInfo.setVisible(true);
            pertInfo.setManaged(true);
        }
        else if(model.isDone())
            status.setText("Completed");
        else
            status.setText("In Progress - Behind Schedule");
    }
}
