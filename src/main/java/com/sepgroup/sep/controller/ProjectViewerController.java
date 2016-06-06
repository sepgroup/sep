package com.sepgroup.sep.controller;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by jeremybrown on 2016-06-01.
 */
public class ProjectViewerController extends AbstractController {

    private static Logger logger = LoggerFactory.getLogger(ProjectViewerController.class);

    private ProjectModel model;

    @FXML
    public Text projectNameText;
    @FXML
    public TextArea projectDescriptionTextArea;
    @FXML
    public Label startDateValueLabel;
    @FXML
    public Label deadlineValueLabel;
    @FXML
    public Label managerValueLabel;
    @FXML
    public Label budgetValueLabel;
    @FXML
    public Label completeValueLabel;
    @FXML
    public TableView<TaskModel> taskTableView;

    public ProjectViewerController() {
        setFxmlPath("/views/projectviewer.fxml");
        setCssPath("/style/stylesheet.css");
    }
    
    public void onEditClicked() {
        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(new ProjectEditorController());
        pvc.setModel(model);
    }

    public void setModel(ProjectModel p) {
        this.model = p;
        update();
    }
    

    @Override
    public void update() {
        if (this.model != null) {
            projectNameText.setText(model.getName());
            projectDescriptionTextArea.setText(model.getProjectDescription());
            startDateValueLabel.setText(model.getStartDateString());
            deadlineValueLabel.setText(model.getDeadlineString());
            budgetValueLabel.setText(Double.toString(model.getBudget()));
            completeValueLabel.setText(model.isDone() ? "Yes" : "No");

            // Populate manager
            String managerName = "";
            int managerUserID = model.getManagerUserId();
            if (managerUserID == 0) {
                managerName = "[ none ]";
            } else {
                try {
                    UserModel manager = UserModel.getById(managerUserID);
                    managerName = manager.getFirstName() + " " + manager.getLastName();
                } catch (ModelNotFoundException e) {
                    logger.error("Error finding user with ID " + managerUserID);
                }
            }
            managerValueLabel.setText(managerName);

            // Populate tasks list
            List<TaskModel> tasksList = null;
            try {
                tasksList = TaskModel.getAllByProject(model.getProjectId());
                ObservableList<TaskModel> observableTaskList = FXCollections.observableList(tasksList);
                taskTableView.setItems(observableTaskList);
            } catch (ModelNotFoundException e) {
                logger.debug("No tasks found for project " + model.toString());
                // No tasks found
            }
        }
    }

    public void onEditProjectClicked() {
        ProjectEditorController pec = (ProjectEditorController) Main.setPrimaryScene(new ProjectEditorController());
        pec.setModel(model);
    }

    public void onCreateTaskButtonClicked() {
        TaskCreatorController tcc = (TaskCreatorController) Main.setPrimaryScene(new TaskCreatorController());
        tcc.setReturnProject(model);
    }

    public void onTaskItemClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            TaskModel selectedTask = taskTableView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                TaskEditorController tec = (TaskEditorController) Main.setPrimaryScene(new TaskEditorController());
                tec.setModel(selectedTask);
            }
        }
    }

    public void onCreateProjectMenuItemClicked() {
        Main.setPrimaryScene(new ProjectCreatorController());
    }

    public void onOpenProjectMenuItemClicked() {
        Main.setPrimaryScene(new WelcomeController());
    }

    public void onCloseProjectMenuItemClicked() {
        Main.setPrimaryScene(new WelcomeController());
    }

    public void onDeleteProjectMenuButtonClicked(){
    	String title = "Warning!";
        String header = "This will delete the project named " + model.getName() + ". This action cannot be undone";
        String content = "Are you sure?";

        if (DialogCreator.showConfirmationDialog(title, header, content).get() == ButtonType.OK) {
            try {
                model.deleteData();
                // Go to welcome screen
                Main.setPrimaryScene(new WelcomeController());
            } catch (DBException e) {
                logger.error("Unable to delete project from DB");
                DialogCreator.showErrorDialog("Unable to delete project from DB", e.getLocalizedMessage());
                return;
            }
        }
    }
    
    public void showInfo() {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Info");
		alert.setHeaderText("Version 1.0. Team members:");
		alert.setContentText("Jeremy Brown \nAli Zoghi \nCharles Tondreau-Alin \nNicola Polesana"
				+ "\nAndres Gonzales \nDemo Kioussis \nJustin Watley \nMark Chmilar \nVince Fugnitto"
				+ "\nMichael Deom");
		alert.showAndWait();
    }
}
