package com.sepgroup.sep.controller;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.*;
import com.sepgroup.sep.utils.DateUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
  	public Label taskIdLabel;
	@FXML
	public TextField editTaskBudgetField;
	@FXML
	public DatePicker editExpectedStartDateTaskField;
	@FXML
	public DatePicker editExpectedDeadlineTaskField;
    @FXML
    public DatePicker editActualStartDateTaskField;
    @FXML
    public DatePicker editActualEndDateTaskField;
	@FXML
	public ComboBox<UserModel> assigneeComboBox;
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
    @FXML
    public TextField editMostLikelyTime;
    @FXML
    public TextField editPessimisticTime;
    @FXML
    public TextField editOptimisticTime;

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
    public void onCancelClicked() {
        // Ignore all changes made to task
        try {
            model.refreshData();
        } catch (ModelNotFoundException e) {
            logger.warn("Tried to refresh existing model that did not exist, this shouldn't happen.");
        }
        // Return to task viewer
        TaskViewerController tvc = (TaskViewerController) Main.setPrimaryScene(TaskViewerController.getFxmlPath());
        tvc.setModel(model);
        tvc.setReturnProject(project);
    }

    @FXML
    public void onUpdateClicked() {
        // Name
        if (editTaskNameField.getText() != null && editTaskNameField.getText().length() > 0) {
            try {
                model.setName(editTaskNameField.getText());
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }

        // Expected Start date
        if (editExpectedStartDateTaskField.getValue() != null) {
            Date expectedStartTaskDate;
            try {
                expectedStartTaskDate = DateUtils.castStringToDate(editExpectedStartDateTaskField.getValue().toString());
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen.";
                logger.error(errorContent);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
            try {
                model.setStartDate(expectedStartTaskDate);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        } else {
            model.removeStartDate();
        }

        // Expected Deadline
        if (editExpectedDeadlineTaskField.getValue() != null) {
            Date expectedTaskDeadline;
            try {
                expectedTaskDeadline = DateUtils.castStringToDate(editExpectedDeadlineTaskField.getValue().toString());
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen.";
                logger.error(errorContent);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
            try {
                model.setDeadline(expectedTaskDeadline);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }
        else {
            model.removeDeadline();
        }

        // Actual Start date
        if (editActualStartDateTaskField.getValue() != null) {
            Date actualStartTaskDate;
            try {
                actualStartTaskDate = DateUtils.castStringToDate(editActualStartDateTaskField.getValue().toString());
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen.";
                logger.error(errorContent);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
            try {
                model.setActualStartDate(actualStartTaskDate);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        } else {
            model.removeActualStartDate();
        }

        // Actual end date
        if (editActualEndDateTaskField.getValue() != null) {
            Date actualTaskDeadline;
            try {
                actualTaskDeadline = DateUtils.castStringToDate(editActualEndDateTaskField.getValue().toString());
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen.";
                logger.error(errorContent);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
            try {
                model.setActualEndDate(actualTaskDeadline);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }
        else {
            model.removeActualEndDate();
        }

        // Budget
        if (!editTaskBudgetField.getText().equals("")) {
            double editTaskBudgetFromField;
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
        } else {
            try {
                model.setBudget(0);
            } catch (InvalidInputException e) {
                logger.error("Error setting task budget to 0 when user left field blank", e);
                DialogCreator.showExceptionDialog(e);
                return;
            }
        }
        //Most Likely Time
        if (!editMostLikelyTime.getText().equals("")) {
            int editMostLikelyFromField;
            try {
                editMostLikelyFromField = Integer.parseInt(editMostLikelyTime.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid integer value");
                DialogCreator.showErrorDialog("Most likely time to finish field invalid", "Invalid Most likely time to finish, please enter a valid number.");
                return;
            }
            try {
                model.setMostLikelyTimeToFinish(editMostLikelyFromField);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }else {
            try {
                model.setMostLikelyTimeToFinish(0);
            } catch (InvalidInputException e) {
                logger.error("Error setting most likely time to finish to 0 when user left field blank", e);
                DialogCreator.showExceptionDialog(e);
                return;
            }
        }
        //Pessimistic Time
        if (!editPessimisticTime.getText().equals("")) {
            int editPessimisticFromField;
            try {
                editPessimisticFromField = Integer.parseInt(editPessimisticTime.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid integer value");
                DialogCreator.showErrorDialog("Pessimistic time to finish field invalid", "Invalid Pessimistic time to finish, please enter a valid number.");
                return;
            }
            try {
                model.setPessimisticTimeToFinish(editPessimisticFromField);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }else {
            try {
                model.setPessimisticTimeToFinish(0);
            } catch (InvalidInputException e) {
                logger.error("Error setting pessimistic time to finish to 0 when user left field blank", e);
                DialogCreator.showExceptionDialog(e);
                return;
            }
        }
        //Optimistic Time
        if (!editOptimisticTime.getText().equals("")) {
            int editOptimisticFromField = 0;
            try {
                editOptimisticFromField = Integer.parseInt(editOptimisticTime.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid integer value");
                DialogCreator.showErrorDialog("Optimistic time to finish field invalid", "Invalid Optimistic time to finish, please enter a valid number.");
                return;
            }
            try {
                model.setOptimisticTimeToFinish(editOptimisticFromField);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }else {
            try {
                model.setOptimisticTimeToFinish(0);
            } catch (InvalidInputException e) {
                logger.error("Error setting optimistic time to finish to 0 when user left field blank", e);
                DialogCreator.showExceptionDialog(e);
                return;
            }
        }

        // Assignee
        UserModel selectedAssignee = assigneeComboBox.getSelectionModel().getSelectedItem();
        if (selectedAssignee == null || selectedAssignee == UserModel.getEmptyUser()) {
            model.removeAssignee();
        }
        else {
            try {
                model.setAssignee(selectedAssignee);
            } catch (InvalidInputException e) {
                logger.error("User being assigned to task is invalid", e);
                DialogCreator.showExceptionDialog(e);
            }
        }

        // Description
        model.setDescription(taskDescriptionArea.getText());

        // Complete
        try {
            model.setDone(completeCheckBox.isSelected());
        } catch (final InvalidInputException e) {
            logger.error(e.getMessage(), e);
            DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
            return;
        }


        // Persist updated model
        try {
            model.persistData();
        } catch (DBException e) {
            logger.error("DB error", e);
            DialogCreator.showErrorDialog("Database error", e.getLocalizedMessage());
            return;
        }

        // Return to task viewer
        TaskViewerController tvc = (TaskViewerController) Main.setPrimaryScene(TaskViewerController.getFxmlPath());
        tvc.setModel(model);
        tvc.setReturnProject(project);
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
            try {
                model.addDependency(selectedPotentialDependency.getModel());
            } catch (TaskDependencyException e) {
                DialogCreator.showErrorDialog("Dependency Error", e.getLocalizedMessage());
            }
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
   		    if (model.getName() != null) editTaskNameField.setText(model.getName());
            if (model.getDescription() != null) taskDescriptionArea.setText(String.valueOf(model.getDescription()));
            editTaskBudgetField.setText(String.valueOf(model.getBudget()));
            editMostLikelyTime.setText(String.valueOf(model.getMostLikelyTimeToFinish()));
            editPessimisticTime.setText(String.valueOf(model.getPesimisticTimeToFinish()));
            editOptimisticTime.setText(String.valueOf(model.getOptimisticTimeToFinish()));
   		    if (model.getStartDate() != null) {
                try {
                    LocalDate startDate = LocalDate.parse(model.getStartDateString());
                    editExpectedStartDateTaskField.setValue(startDate);
                } catch (DateTimeParseException e) {
                    DialogCreator.showExceptionDialog(e);
                }
            }
            if (model.getDeadline() != null) {
                try {
                    LocalDate deadline = LocalDate.parse(model.getDeadlineString());
                    editExpectedDeadlineTaskField.setValue(deadline);
                } catch (DateTimeParseException e) {
                    DialogCreator.showExceptionDialog(e);
                }
            }
            if (model.getActualStartDate() != null) {
                try {
                    LocalDate actualStartDate = LocalDate.parse(model.getActualStartDateString());
                    editActualStartDateTaskField.setValue(actualStartDate);
                } catch (DateTimeParseException e) {
                    DialogCreator.showExceptionDialog(e);
                }
            }
            if (model.getActualEndDate() != null) {
                try {
                    LocalDate actualDeadline = LocalDate.parse(model.getActualEndDateString());
                    editActualEndDateTaskField.setValue(actualDeadline);
                } catch (DateTimeParseException e) {
                    DialogCreator.showExceptionDialog(e);
                }
            }

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
   		    if (model.getAssignee() != null) assigneeComboBox.getSelectionModel().select(model.getAssignee());

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
