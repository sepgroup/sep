package com.sepgroup.sep.controller;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Charles on 2016-05-22.
 */
public class TaskCreatorController extends AbstractController {

    private static Logger logger = LoggerFactory.getLogger(TaskCreatorController.class);
    private static final String fxmlPath = "/views/taskcreator.fxml";
    private ProjectModel project;

	@FXML
	public TextField taskNameField;
	@FXML
	public TextField taskBudgetField;
	@FXML
	public DatePicker taskStartDatePicker;
	@FXML
	public DatePicker taskDeadlinePicker;
	@FXML
	public ComboBox<UserModel> assigneeComboBox;
	@FXML
	public TextArea taskDescriptionArea;
    @FXML
    public TextField mostLikelyTime;
    @FXML
    public TextField pessimisticTime;
    @FXML
    public TextField optimisticTime;

    public TaskCreatorController() {
        setCssPath("/style/stylesheet.css");
    }

    @FXML
    public void initialize() {
        // TODO doesn't actually focus?
        taskNameField.requestFocus();
        update();
    }

    public static String getFxmlPath() {
        return fxmlPath;
    }

    public void setReturnProject(ProjectModel p) {
        project = p;
    }

    /**
	 * Returns to createproject
	 */
    public void onTaskCancelClicked() throws IOException {
		ProjectViewerController pec = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
        pec.setModel(project);
    }

	public void onCreateTaskClicked() throws IOException {
        TaskModel createdTask = new TaskModel();

        // Project
        try {
            createdTask.setProjectId(project.getProjectId());
        } catch (InvalidInputException e) {
            logger.error("Trying to set project ID for a project that doesn't exist, this shouldn't happen.");
            return;
        }

        // Name
        try {
            createdTask.setName(taskNameField.getText());
        } catch (InvalidInputException e) {
            DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
            return;
        }

        // Start date
        Date startDate = null;
        if (taskStartDatePicker.getValue() != null) {
            try {
                createdTask.setStartDate(taskStartDatePicker.getValue().toString());
            } catch (InvalidInputException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                logger.error(errorContent);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
        }

        // Deadline
        Date deadline = null;
        if (taskDeadlinePicker.getValue() != null) {
            try {
                createdTask.setDeadline(taskDeadlinePicker.getValue().toString());
            } catch (InvalidInputException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                logger.error(errorContent);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
        }

        // Budget
        if (!taskBudgetField.getText().equals("")) {
            double budgetDouble = 0;
            try {
                budgetDouble = Double.parseDouble(taskBudgetField.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid budget value");
                DialogCreator.showErrorDialog("Budget field invalid", "Invalid budget, please enter a valid number.");
                return;
            }
            try {
                createdTask.setBudget(budgetDouble);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }

        // Assignee
        UserModel selectedAssignee = assigneeComboBox.getSelectionModel().getSelectedItem();
        if (selectedAssignee != null && selectedAssignee != UserModel.getEmptyUser()) {
            try {
                createdTask.setAssignee(selectedAssignee);
            } catch (InvalidInputException e) {
                logger.error("User being assigned to task is invalid", e);
                DialogCreator.showExceptionDialog(e);
            }
        }

        // Description
		if (!taskDescriptionArea.getText().equals("")) {
            createdTask.setDescription(taskDescriptionArea.getText());
		}
        //Most Likely Time
        if (!mostLikelyTime.getText().equals("")) {
            int mostLikely = 0;
            try {
                mostLikely = Integer.parseInt(mostLikelyTime.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid integer value");
                DialogCreator.showErrorDialog("Most likely time to finish field invalid", "Invalid Most likely time to finish, please enter a valid number.");
                return;
            }
            try {
                createdTask.setMostLikelyTimeToFinish(mostLikely);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }
        //Pessimistic Time
        if (!pessimisticTime.getText().equals("")) {
            int pessimistic = 0;
            try {
                pessimistic = Integer.parseInt(pessimisticTime.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid integer value");
                DialogCreator.showErrorDialog("Pessimistic time to finish field invalid", "Invalid Pessimistic time to finish, please enter a valid number.");
                return;
            }
            try {
                createdTask.setMostLikelyTimeToFinish(pessimistic);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }
        //Optimistic Time
        if (!optimisticTime.getText().equals("")) {
            int optimist = 0;
            try {
                optimist = Integer.parseInt(optimisticTime.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid integer value");
                DialogCreator.showErrorDialog("Optimistic time to finish field invalid", "Invalid Optimistic time to finish, please enter a valid number.");
                return;
            }
            try {
                createdTask.setMostLikelyTimeToFinish(optimist);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }
        // Persist created Project
		try {
		    createdTask.persistData();
		} catch (DBException e) {
            DialogCreator.showErrorDialog("Database error", e.getLocalizedMessage());
            return;
		}

        // Return to project viewer
        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
        pvc.setModel(project);
    }
	
	@Override
	public void update() {
        // Populate assignee list
        List<UserModel> userList;
        try {
            userList = UserModel.getAll();
        } catch (ModelNotFoundException e) {
            logger.debug("No users found.");
            userList = new LinkedList<>();
        }
        userList.add(0, UserModel.getEmptyUser());
        ObservableList<UserModel> observableUserList = FXCollections.observableList(userList);
        assigneeComboBox.setItems(observableUserList);
	}
}
