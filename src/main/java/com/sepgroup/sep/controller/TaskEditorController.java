package com.sepgroup.sep.controller;

import java.text.ParseException;
import java.util.Date;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.utils.DateUtils;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jeremybrown on 2016-06-01.
 */
public class TaskEditorController extends AbstractController {

    private static Logger logger = LoggerFactory.getLogger(TaskEditorController.class);

    private TaskModel model;
    private ProjectModel project;

    @FXML
	public TextField editTaskNameField;
    @FXML
  	public Label taskNameLabel;
    @FXML
  	public Label taskIdLabel;
	@FXML
	public TextField editTaskBudgetField;
	@FXML
	public Label taskBudgetValueLabel;
	@FXML
	public DatePicker editStartDateTaskField;
	@FXML
	public Label taskStartDateValueLabel;
	@FXML
	public DatePicker editDeadlineTaskField;
	@FXML
	public Label taskDeadlineValueLabel;
	@FXML
	public TextField assigneeTaskField;
	@FXML
	public Label assigneeTaskLabel;
	@FXML
	public TextArea taskDescriptionArea;
    @FXML
    public Label taskManagerLabel;
    @FXML
    public ListView dependenciesTaskList;

    private String editTaskNameFromField = "";
    private double editTaskBudgetFromField = 0;
    private String editTaskDescriptionFromField = "";
    private String editAssigneeFromField = "";

    public TaskEditorController() {
        setFxmlPath("/views/taskeditor.fxml");
        setCssPath("/style/stylesheet.css");
    }

    public void setReturnProject(ProjectModel p) {
        project = p;
    }
    
    @FXML
    public void onEditTaskCancelClicked() {
        // Ignore all changes made to task
        try {
            model.refreshData();
        } catch (ModelNotFoundException e) {
            logger.warn("Tried to refresh existing model that did not exist", e);
        }
        // Return to project viewer
        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(new ProjectViewerController());
        pvc.setModel(project);
    }
    
    @FXML
    public void onEditTaskUpdateClicked() {
        // Name
        if (!editTaskNameField.getText().equals("")) {
            editTaskNameFromField = editTaskNameField.getText();
            model.setName(editTaskNameFromField);
        }

        // Start date
        Date startTaskDate = null;
        if (editStartDateTaskField.getValue() != null) {
            try {
                startTaskDate = DateUtils.castStringToDate(editStartDateTaskField.getValue().toString());
                model.setStartDate(startTaskDate);;
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                logger.error(errorContent, e);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
        }

        // Deadline
        Date taskDeadline = null;
        if (editDeadlineTaskField.getValue() != null) {
            try {
                taskDeadline = DateUtils.castStringToDate(editDeadlineTaskField.getValue().toString());
                model.setStartDate(taskDeadline);
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                logger.error(errorContent, e);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
        }

        // Budget
        if (!editTaskBudgetField.getText().equals("")) {
            try {
                editTaskBudgetFromField = Integer.parseInt(editTaskBudgetField.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid budget value", e);
                DialogCreator.showErrorDialog("Budget field invalid", "Invalid budget, please enter a valid number.");
                return;
            }
            model.setBudget(editTaskBudgetFromField);
        }

        if (!assigneeTaskField.getText().equals("")) {
            // TODO user name instead of ID
            editAssigneeFromField = assigneeTaskField.getText();
            model.setAssigneeUserId(Integer.parseInt(editAssigneeFromField));
        }

        if (!taskDescriptionArea.getText().equals("")) {
            editTaskDescriptionFromField = taskDescriptionArea.getText();
            model.setDescription( editTaskDescriptionFromField);
        }

        try {
            model.persistData();
        } catch (DBException e) {
            logger.error("DB error", e);
            DialogCreator.showErrorDialog("Database error", e.getLocalizedMessage());
            return;
        }

        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(new ProjectViewerController());
        pvc.setModel(project);
    }

    public void setModel(TaskModel t) {
        this.model = t;
        update();
    }

    @Override
    public void update() {
   	    if (this.model != null) {
   		    taskIdLabel.setText(String.valueOf(model.getTaskId()));
   		    if (model.getName() != null) taskNameLabel.setText(model.getName());
   		    taskBudgetValueLabel.setText(String.valueOf(model.getBudget()));
   		    if (model.getStartDate() != null) taskStartDateValueLabel.setText(String.valueOf(model.getStartDate()));
            if (model.getDeadline() != null) taskDeadlineValueLabel.setText(String.valueOf(model.getDeadline()));
   		    assigneeTaskLabel.setText(String.valueOf(model.getAssigneeUserId()));
            // TODO complete check mark
        }
    }
}
