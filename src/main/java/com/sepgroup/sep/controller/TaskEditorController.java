package com.sepgroup.sep.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.ListableTaskModel;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.utils.DateUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public ListView<ListableTaskModel> dependenciesTaskList;
    @FXML
    public CheckBox completeCheckBox;
    @FXML
    public ComboBox<ListableTaskModel> dependenciesComboBox;
    @FXML
    public Button addSelectedDependencyButton;
    @FXML
    public Button deleteSelectedDependencyButton;

    private ListableTaskModel selectedDependency;
    private ListableTaskModel selectedPotentialDependency;

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
            logger.warn("Tried to refresh existing model that did not exist", e);
        }
        // Return to project viewer
        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
        pvc.setModel(project);
    }

    @FXML
    public void onEditTaskUpdateClicked() {
        // Name
        if (!editTaskNameField.getText().equals("")) {
            String editTaskNameFromField = editTaskNameField.getText();
            model.setName(editTaskNameFromField);
        }

        // Start date
        if (editStartDateTaskField.getValue() != null) {
            try {
                Date startTaskDate = DateUtils.castStringToDate(editStartDateTaskField.getValue().toString());
                model.setStartDate(startTaskDate);
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                logger.error(errorContent, e);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
        }

        // Deadline
        if (editDeadlineTaskField.getValue() != null) {
            try {
                Date taskDeadline = DateUtils.castStringToDate(editDeadlineTaskField.getValue().toString());
                model.setDeadline(taskDeadline);
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                logger.error(errorContent, e);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
        }

        // Budget
        if (!editTaskBudgetField.getText().equals("")) {
            double editTaskBudgetFromField;
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
            String editAssigneeFromField = assigneeTaskField.getText();
            model.setAssigneeUserId(Integer.parseInt(editAssigneeFromField));
        }

        if (!taskDescriptionArea.getText().equals("")) {
            String editTaskDescriptionFromField = taskDescriptionArea.getText();
            model.setDescription(editTaskDescriptionFromField);
        }

        // Complete
        model.setDone(completeCheckBox.isSelected());

        try {
            model.persistData();
        } catch (DBException e) {
            logger.error("DB error", e);
            DialogCreator.showErrorDialog("Database error", e.getLocalizedMessage());
            return;
        }

        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
        pvc.setModel(project);
    }

    /**
     *
     * @param t
     */
    public void setModel(TaskModel t) {
        this.model = t;
        update();
        editTaskNameField.requestFocus();  // set focus to name field
    }

    /**
     *
     */
    public void onDeleteTaskClicked() {
        String title = "Warning!";
        String header = "This will delete the task " + model.getName() + ". This action cannot be undone";
        String content = "Are you sure?";

        Optional<ButtonType> dialogResult = DialogCreator.showConfirmationDialog(title, header, content);
        if (dialogResult.isPresent() && dialogResult.get() == ButtonType.OK) {
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

    /**
     * Handle click on dependency list
     */
    @FXML
    public void onDependencyTaskListSelected() {
        selectedDependency = dependenciesTaskList.getSelectionModel().getSelectedItem();
        if (selectedDependency != null) {
            deleteSelectedDependencyButton.setDisable(false);
        }
        else {
            deleteSelectedDependencyButton.setDisable(true);
        }
    }

    /**
     * Handle selection of potential dependency
     */
    public void onDependenciesComboBoxSelected() {
        selectedPotentialDependency = dependenciesComboBox.getSelectionModel().getSelectedItem();
        if (selectedPotentialDependency != null) {
            addSelectedDependencyButton.setDisable(false);
        }
        else {
            addSelectedDependencyButton.setDisable(true);
        }
    }

    /**
     * Handle click on add dependency button
     */
    public void onAddDependencyClicked() {
        if (selectedPotentialDependency != null) {
            model.addDependency(selectedPotentialDependency.getModel());
            refreshCurrentDependenciesList();
            refreshPotentialDependenciesList();
            addSelectedDependencyButton.setDisable(true);
        }
    }

    /**
     * Handle click on delete dependency button
     */
    public void onDeleteSelectedDependencyClicked() {
        if (selectedDependency != null) {
            model.removeDependency(selectedDependency.getModel());
            refreshCurrentDependenciesList();
            refreshPotentialDependenciesList();
            deleteSelectedDependencyButton.setDisable(true);
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

            refreshCurrentDependenciesList();
            refreshPotentialDependenciesList();
        }
    }

    private void refreshCurrentDependenciesList() {
        List<ListableTaskModel> dependenciesObservableList = new LinkedList<>();
        dependenciesObservableList.addAll(model.getDependencies().stream()
                .map(ListableTaskModel::new)
                .collect(Collectors.toList()));
        ObservableList<ListableTaskModel> dependencies = FXCollections.observableList(dependenciesObservableList);
        dependenciesTaskList.setItems(dependencies);
    }

    private void refreshPotentialDependenciesList() {
        try {
            logger.debug("Populating tasks list");
            List<ListableTaskModel> allTasksList = TaskModel.getAllByProject(model.getProjectId()).stream()
                    // compare ID instead of using equals b/c newly added dependency isn't persisted yet
                    .filter(taskModel -> taskModel.getTaskId() != model.getTaskId())
                    .filter(taskModel -> !model.getDependencies().contains(taskModel))
                    .map(ListableTaskModel::new)
                    .collect(Collectors.toList());

            ObservableList<ListableTaskModel> observableTaskList = FXCollections.observableList(allTasksList);
            dependenciesComboBox.setItems(observableTaskList);
        } catch (ModelNotFoundException e) {
            logger.debug("No tasks found for project " + model.toString());
        }
    }
}
