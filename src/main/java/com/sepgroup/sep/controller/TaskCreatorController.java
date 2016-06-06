package com.sepgroup.sep.controller;

import java.io.IOException;
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

/**
 * Created by Charles on 2016-05-22.
 */
public class TaskCreatorController extends AbstractController {

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
	
	public String taskNameFromField = " ";
	public int taskBudgetFromField = 0;
	public String taskDescription;
	public int taskAssigneeNumberFromField;
	public int taskProjectId = 0;
	
	/**
	 * Returns to createproject
	 */
	@FXML
    public void onTaskCancelClicked() throws IOException {
		Main.setPrimaryScene(new ProjectEditorController());
    }

	@FXML
	public void onCreateTaskClicked() throws IOException{
		
		if (taskNameField.getText() != ""){
            taskNameFromField = taskNameField.getText();
		}
		Date taskStartDate = null;
		Date taskDeadline = null;
		if (taskStartDatePicker.getValue() != null && taskDeadlinePicker.getValue() != null) {
			try {
			    taskStartDate = DateUtils.castStringToDate(taskStartDatePicker.getValue().toString());
			    taskDeadline = DateUtils.castStringToDate(taskDeadlinePicker.getValue().toString());
			} catch(Exception e) {
				e.getMessage();
			}
        }

		if (taskBudgetField.getText() != "") {
			taskBudgetFromField = Integer.parseInt(taskBudgetField.getText());
        }
		
		if (taskAssigneeNumber.getText() != "") {
			taskAssigneeNumberFromField = Integer.parseInt(taskAssigneeNumber.getText());
        }
		
		if (taskDescriptionArea.getText()!="") {
			taskDescription=taskDescriptionArea.getText();
		}

		// TODO FIX PROJECT ID
		TaskModel createdTask = new TaskModel(taskNameFromField, taskDescription, taskProjectId, taskBudgetFromField, taskStartDate, taskDeadline,
	            false, taskAssigneeNumberFromField);
		
		try {
		    createdTask.persistData();
		}
		catch (DBException e) {
            // TODO handle
            // show popup or something
            System.out.println(e.getLocalizedMessage());
		}

        Main.setPrimaryScene(new ProjectEditorController());
    }
	
	@Override
	public void update() {
		// None needed for this controller
	}
}