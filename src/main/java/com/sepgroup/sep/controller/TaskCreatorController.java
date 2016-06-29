package com.sepgroup.sep.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.InvalidInputException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.sepgroup.sep.utils.DateUtils;
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
	public TextField taskAssigneeNumber; 	
	@FXML
	public TextArea taskDescriptionArea;

    public TaskCreatorController() {
        setCssPath("/style/stylesheet.css");
    }

    @FXML
    public void initialize() {
        // TODO doesn't actually focus?
        taskNameField.requestFocus();
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
        // TODO get user ID field
        if (!taskAssigneeNumber.getText().equals("")) {
            int assigneeID;
            try {
                assigneeID = Integer.parseInt(taskAssigneeNumber.getText());
            } catch (NumberFormatException e) {
                DialogCreator.showErrorDialog("Invalid user ID", "Enter a valid manager user ID.");
                return;
            }
            try {
                createdTask.setAssigneeUserId(assigneeID);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }

        // Description
		if (!taskDescriptionArea.getText().equals("")) {
            createdTask.setDescription(taskDescriptionArea.getText());
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
		// None needed for this controller
	}
}
