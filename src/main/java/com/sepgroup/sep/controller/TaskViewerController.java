package com.sepgroup.sep.controller;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.model.*;

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
    public TextArea taskDescriptionArea;
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
    public Label mostLikelyDurationLabel;
    @FXML
    public Label optimisticDurationLabel;
    @FXML
    public Label actualStartDateLabel;
    @FXML
    public Label actualEndDateLabel;
    @FXML
    public Label expectedStartDateLabel;
    @FXML
    public Label expectedEndDateLabel;
    @FXML
    public Label pessimisticDurationLabel;
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
                LocalDate projectStartDate = null;
                try {
                    projectStartDate = project.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                }
                catch(NullPointerException e)
                {
                    DialogCreator.showErrorDialog("Project Start Date Not Set", "This project's start date has not been set. P.E.R.T." +
                                                    " analysis cannot be performed without it.");
                    return;
                }
                catch(Exception e)
                {
                    DialogCreator.showExceptionDialog(e);
                    return;
                }
                LocalDate expectedFinishingDate = pertDate.getValue();

                Period diff = Period.between(projectStartDate, expectedFinishingDate);
                double dayDifference = diff.getDays() + diff.getMonths() * 30 + diff.getYears() * 365;

                try {
                    pertPercentage.setText(String.format("%.2f", PERTAnalysisTools.pertAnalysis(model, (int)dayDifference) * 100)
                            + "% chance of finishing this task within " + dayDifference + " days of start of project");
                }
                catch(Exception e)
                {
                    DialogCreator.showExceptionDialog(e);
                    return;
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
        if(model.getAssignee() != null) {
            logger.info("assignee not null");
            assignee.setText(String.valueOf(model.getAssignee()));
        }
        else {
            logger.info("assignee null");
            assignee.setText("[ none ]");
        }

        if(model.getDescription() != null && !model.getDescription().equals(""))
            taskDescriptionArea.setText(model.getDescription());
        else {
            taskDescriptionArea.setVisible(false);
            taskDescriptionArea.setManaged(false);
        }

        // Durations
        pessimisticDurationLabel.setText(Integer.toString(model.getPesimisticTimeToFinish()));
        optimisticDurationLabel.setText(Integer.toString(model.getOptimisticTimeToFinish()));
        mostLikelyDurationLabel.setText(Integer.toString(model.getMostLikelyTimeToFinish()));

        // actual / expected dates
        actualStartDateLabel.setText(model.getActualStartDateString());
        actualEndDateLabel.setText(model.getActualEndDateString());
        expectedStartDateLabel.setText(model.getStartDateString());
        expectedEndDateLabel.setText(model.getDeadlineString());

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
        List<ListableTaskModel> dependentsObservableList = new LinkedList<>();
        Graph newGraph = new Graph();
        GraphFactory.makeGraph(model.getProjectId(), newGraph);

        ArrayList<Node> outNodes = newGraph.getNodeByID(model.getTaskId()).getInNodes();
        ArrayList<TaskModel> inTasks = new ArrayList<TaskModel>();

        for (Node n : outNodes)
            inTasks.add(n.getData().task);

        List<ListableTaskModel> dependenciesObservableList = new LinkedList<>();
        dependenciesObservableList.addAll(inTasks.stream().map(ListableTaskModel::new).collect(Collectors.toList()));
        ObservableList<ListableTaskModel> dependencies = FXCollections.observableList(dependenciesObservableList);
        dependenciesList.setItems(dependencies);
    }

    private void refreshCurrentDependentList() {
        List<ListableTaskModel> dependentsObservableList = new LinkedList<>();
        Graph newGraph = new Graph();
        GraphFactory.makeGraph(model.getProjectId(), newGraph);

        ArrayList<Node> outNodes = newGraph.getNodeByID(model.getTaskId()).getOutNodes();
        ArrayList<TaskModel> outTasks = outNodes.stream().map(n -> n.getData().task).collect(Collectors.toCollection(ArrayList::new));

        dependentsObservableList.addAll(outTasks.stream().map(ListableTaskModel::new).collect(Collectors.toList()));
        ObservableList<ListableTaskModel> dependents = FXCollections.observableList(dependentsObservableList);
        dependentsList.setItems(dependents);
    }
}
