package com.sepgroup.sep.controller;

import java.text.ParseException;
import java.util.Date;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.InvalidInputException;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.utils.DateUtils;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jeremybrown on 2016-06-01.
 */
public class TaskEditorController extends AbstractController {

    private static Logger logger = LoggerFactory.getLogger(TaskEditorController.class);
    private static final String fxmlPath = "/views/taskeditor.fxml";
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
	public Label taskAssigneeLabel;
	@FXML
	public TextArea taskDescriptionArea;
    @FXML
    public Label taskManagerLabel;
    @FXML
    public ListView dependenciesTaskList;
    @FXML
    public CheckBox completeCheckBox;

    private String editTaskNameFromField = "";
    private double editTaskBudgetFromField = 0;
    private String editTaskDescriptionFromField = "";
    private String editAssigneeFromField = "";

    public TaskEditorController() {
        setCssPath("/style/stylesheet.css");
    }

    public static String getFxmlPath() {
        return fxmlPath;
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
            logger.warn("Tried to refresh existing model that did not exist, this should never happen.");
        } catch (InvalidInputException e) {
            logger.error("Invalid data in DB.");
        }
        // Return to project viewer
        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
        pvc.setModel(project);
    }
    
    @FXML
    public void onEditTaskUpdateClicked() {
        // Name
        if (editTaskNameField.getText() != null && editTaskNameField.getText().length() > 0) {
            try {
                model.setName(editTaskNameField.getText());
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }

        // Start date
        if (editStartDateTaskField.getValue() != null) {
            Date startTaskDate;
            try {
                startTaskDate = DateUtils.castStringToDate(editStartDateTaskField.getValue().toString());
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen.";
                logger.error(errorContent);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
            try {
                model.setStartDate(startTaskDate);;
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }

        // Deadline
        if (editDeadlineTaskField.getValue() != null) {
            Date taskDeadline;
            try {
                taskDeadline = DateUtils.castStringToDate(editDeadlineTaskField.getValue().toString());
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen.";
                logger.error(errorContent);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
            try {
                model.setDeadline(taskDeadline);;
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }

        // Budget
        if (!editTaskBudgetField.getText().equals("")) {
            try {
                editTaskBudgetFromField = Double.parseDouble(editTaskBudgetField.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid budget value");
                DialogCreator.showErrorDialog("Budget field invalid", "Invalid budget, please enter a valid number.");
                return;
            }
            try {
                model.setBudget(editTaskBudgetFromField);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }

        // Assignee
        // TODO user name instead of ID
        if (!assigneeTaskField.getText().equals("")) {
            int assigneeId;
            try {
                assigneeId = Integer.parseInt(assigneeTaskField.getText());
            } catch (NumberFormatException e) {
                DialogCreator.showErrorDialog("Invalid user ID", "Enter a valid manager user ID.");
                return;
            }
            try {
                model.setAssigneeUserId(assigneeId);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }

        if (!taskDescriptionArea.getText().equals("")) {
            model.setDescription(taskDescriptionArea.getText());
        }

        // Complete
        model.setDone(completeCheckBox.isSelected());

        // Persist updated model
        try {
            model.persistData();
        } catch (DBException e) {
            logger.error("DB error", e);
            DialogCreator.showErrorDialog("Database error", e.getLocalizedMessage());
            return;
        }

        // Return to project viewer
        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
        pvc.setModel(project);
    }

    public void setModel(TaskModel t) {
        this.model = t;
        update();
    }

    public void onDeleteTaskClicked() {
        String title = "Warning!";
        String header = "This will delete the task " + model.getName() + ". This action cannot be undone";
        String content = "Are you sure?";

        if (DialogCreator.showConfirmationDialog(title, header, content).get() == ButtonType.OK) {
            try {
                model.deleteData();
            } catch (DBException e) {
                logger.error("Unable to delete project from DB");
                DialogCreator.showErrorDialog("Unable to delete project from DB", e.getLocalizedMessage());
                return;
            }
            // Return to project view
            ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
            pvc.setModel(project);
        }
    }

    @Override
    public void update() {
   	    if (this.model != null) {
   		    taskIdLabel.setText(String.valueOf(model.getTaskId()));
   		    if (model.getName() != null) taskNameLabel.setText(model.getName());
   		    taskBudgetValueLabel.setText(String.valueOf(model.getBudget()));
   		    if (model.getStartDate() != null) taskStartDateValueLabel.setText(String.valueOf(model.getStartDate()));
            if (model.getDeadline() != null) taskDeadlineValueLabel.setText(String.valueOf(model.getDeadline()));
   		    taskAssigneeLabel.setText(String.valueOf(model.getAssigneeUserId()));
            completeCheckBox.setSelected(model.isDone());
        }
    }
}
