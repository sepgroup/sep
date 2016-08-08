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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.time.ZoneId;
import java.time.Period;
import com.sepgroup.sep.analysis.*;

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
    public Label assigneeLabel;
    @FXML
    public Label assignee;
    @FXML
    public Pane pertInfo;
    @FXML
    public DatePicker pertDate;
    @FXML
    public ListView<ListableTaskModel> dependenciesList;
    @FXML
    public ListView<ListableTaskModel> dependentsList;
    @FXML
    public Button editButton;

    private ListableTaskModel selectedTask;
    private ListableTaskModel selectedPotentialTask;

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
        if(pertDate.getValue() != null)
        {
            if(!model.shouldBeDone())
            {
                LocalDate projectStartDate = project.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate expectedFinishingDate = pertDate.getValue();

                Period diff = Period.between(projectStartDate, expectedFinishingDate);
                double dayDifference = diff.getDays() + diff.getMonths() * 30 + diff.getYears() * 365;
                System.out.println(dayDifference);

                try {
                    pertPercentage.setText(String.format("%.2f", PERTAnalysisTools.pertAnalysis(model, (int)dayDifference) * 100)
                            + "% chance of finishing this task within " + dayDifference + " days of start of project");
                }
                catch(Exception e)
                {
                    DialogCreator.showExceptionDialog(e);
                }
            }

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
        if(model.getAssignee() != null)
            assignee.setText(String.valueOf(model.getAssignee()));

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

        if(model.getTaskId() < 0)
            editButton.setDisable(true);

        refreshCurrentDependenciesList();
        refreshCurrentDependentList();
    }

    @FXML
    public void onDependenciesListSelected(MouseEvent e) {
        selectedTask = dependenciesList.getSelectionModel().getSelectedItem();
        if (e.getClickCount() == 2) {
            if (selectedTask != null) {
                TaskViewerController tvc = (TaskViewerController) Main.setPrimaryScene(TaskViewerController.getFxmlPath());
                tvc.setModel(selectedTask.getModel());
                tvc.setReturnProject(project);
            }
        }
    }

    @FXML
    public void onDependentsListSelected(MouseEvent e) {
        selectedTask = dependentsList.getSelectionModel().getSelectedItem();
        if (e.getClickCount() == 2) {
            if (selectedTask != null) {
                TaskViewerController tvc = (TaskViewerController) Main.setPrimaryScene(TaskViewerController.getFxmlPath());
                tvc.setModel(selectedTask.getModel());
                tvc.setReturnProject(project);
            }
        }
    }

    private void refreshCurrentDependenciesList() {
        List<ListableTaskModel> dependenciesObservableList = new LinkedList<>();
        dependenciesObservableList.addAll(model.getDependencies().stream()
                .map(ListableTaskModel::new)
                .collect(Collectors.toList()));
        ObservableList<ListableTaskModel> dependencies = FXCollections.observableList(dependenciesObservableList);
        dependenciesList.setItems(dependencies);
    }

    private void refreshCurrentDependentList() {
        List<ListableTaskModel> dependentsObservableList = new LinkedList<>();
        Graph newGraph = new Graph();
        GraphFactory.makeGraph(model.getProjectId(), newGraph);

        ArrayList<Node> outNodes = newGraph.getNodeByID(model.getTaskId()).getOutNodes();
        ArrayList<TaskModel> outTasks = new ArrayList<TaskModel>();

        for (Node n : outNodes)
            outTasks.add(n.getData().task);

        dependentsObservableList.addAll(outTasks.stream().map(ListableTaskModel::new).collect(Collectors.toList()));
        ObservableList<ListableTaskModel> dependents = FXCollections.observableList(dependentsObservableList);
        dependentsList.setItems(dependents);
    }
}
