package com.sepgroup.sep.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
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

    private ProjectModel project;

	public TaskCreatorController() {
        setFxmlPath("/views/taskcreator.fxml");
        setCssPath("/style/stylesheet.css");
    }

    public void setReturnProject(ProjectModel p) {
        project = p;
    }
	
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
	
	private String taskNameFromField = "";
    private int taskBudgetFromField = 0;
    private String taskDescription;
    private int taskAssigneeNumberFromField;
	
	/**
	 * Returns to createproject
	 */
	@FXML
    public void onTaskCancelClicked() throws IOException {
		ProjectEditorController pec = (ProjectEditorController) Main.setPrimaryScene(new ProjectEditorController());
        pec.setModel(project);
    }

	@FXML
	public void onCreateTaskClicked() throws IOException {
        // Name
		if (taskNameField.getText().length() > 0){
            taskNameFromField = taskNameField.getText();
		}

        // Start date
		Date taskStartDate = null;
		if (taskStartDatePicker.getValue() != null) {
            try {
                taskStartDate = DateUtils.castStringToDate(taskStartDatePicker.getValue().toString());
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                logger.error(errorContent, e);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
        }

        // Deadline
        Date taskDeadline = null;
        if (taskDeadlinePicker.getValue() != null) {
            try {
                taskStartDate = DateUtils.castStringToDate(taskDeadlinePicker.getValue().toString());
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                logger.error(errorContent, e);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
        }

        // Budget
        if (!taskBudgetField.getText().equals("")) {
            try {
			    taskBudgetFromField = Integer.parseInt(taskBudgetField.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid budget value", e);
                DialogCreator.showErrorDialog("Budget field invalid", "Invalid budget, please enter a valid number.");
                return;
            }
        }
		
		if (!taskAssigneeNumber.getText().equals("")) {
            // TODO get user ID field
			taskAssigneeNumberFromField = Integer.parseInt(taskAssigneeNumber.getText());
        }
		
		if (!taskDescriptionArea.getText().equals("")) {
			taskDescription = taskDescriptionArea.getText();
		}

		TaskModel createdTask = new TaskModel(taskNameFromField, taskDescription, project.getProjectId(),
                taskBudgetFromField, taskStartDate, taskDeadline, false, taskAssigneeNumberFromField);
		
		try {
		    createdTask.persistData();
		} catch (DBException e) {
            logger.error("DB error", e);
            DialogCreator.showErrorDialog("Database error", e.getLocalizedMessage());
            return;
		}

        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(new ProjectViewerController());
        pvc.setModel(project);
    }
	
	@Override
	public void update() {
		// None needed for this controller
	}
}