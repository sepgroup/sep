package com.sepgroup.sep.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.sepgroup.sep.model.InvalidInputException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.UserModel;
import com.sepgroup.sep.utils.DateUtils;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Created by Charles on 2016-05-22.
 */
public class ProjectEditorController extends AbstractController {

    private static Logger logger = LoggerFactory.getLogger(ProjectEditorController.class);
    private static final String fxmlPath = "/views/projecteditor.fxml";
	private ProjectModel model;

	@FXML
	public TextField editNameField;
	@FXML
	public TextField editBudgetField;
	@FXML
	public DatePicker editStartDatePicker;
	@FXML
	public DatePicker editDeadlinePicker;
	@FXML
	public ComboBox<UserModel> managerComboBox;
    @FXML
    public Text projectNameText;
    @FXML
    public TextArea projectDescriptionTextArea;

	public ProjectEditorController() {
        setCssPath("/style/stylesheet.css");
    }

    public static String getFxmlPath() {
        return fxmlPath;
    }

    @FXML
    public void initialize() {
        // TODO doesn't actually focus?
        editNameField.requestFocus();
    }

	/**
	 * Returns to projectview
	 */
	@FXML
    public void onEditCancelClicked() {
        try {
            model.refreshData();
        } catch (ModelNotFoundException e) {
            logger.warn("Tried to refresh existing model that did not exist, this should never happen.");
        } catch (InvalidInputException e) {
            logger.error("Invalid data in DB.");
        }
        // Return to project viewer
        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
	    pvc.setModel(model);
    }
	
	@FXML
    public void onDeleteClicked() {
        String title = "Warning!";
        String header = "This will delete the project named " + model.getName() + ". This action cannot be undone";
        String content = "Are you sure?";

		if (DialogCreator.showConfirmationDialog(title, header, content).get() == ButtonType.OK) {
            try {
                model.deleteData();
                // Go to welcome screen
                Main.setPrimaryScene(WelcomeController.getFxmlPath());
            } catch (DBException e) {
                logger.error("Unable to delete project from DB");
                DialogCreator.showErrorDialog("Unable to delete project from DB", e.getLocalizedMessage());
                return;
            }
		}
	}
	
		@FXML
	    public void onEditUpdateClicked()  {
            // Name
            if (editNameField.getText() != null && editNameField.getText().length() > 0) {
                try {
                    model.setName(editNameField.getText());
                } catch (InvalidInputException e) {
                    DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                    return;
                }
            }

            // Start date
            if (editStartDatePicker.getValue() != null) {
                Date startDate;
                try {
                    startDate = DateUtils.castStringToDate(editStartDatePicker.getValue().toString());
                } catch (ParseException e) {
                    String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                    logger.error(errorContent);
                    DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                    return;
                }
                try {
                    model.setStartDate(startDate);
                } catch (InvalidInputException e) {
                    DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                    return;
                }
            }

			// Deadline
            if (editDeadlinePicker.getValue() != null) {
                Date deadline;
                try {
                    deadline = DateUtils.castStringToDate(editDeadlinePicker.getValue().toString());
                } catch (ParseException e) {
                    String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                    logger.error(errorContent);
                    DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                    return;
                }
                try {
                    model.setDeadline(deadline);
                } catch (InvalidInputException e) {
                    DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                    return;
                }
            }

            // Budget
            if (!editBudgetField.getText().equals("")) {
                double budgetValue = 0;
                try {
                    budgetValue = Double.parseDouble(editBudgetField.getText());
                } catch (NumberFormatException e) {
                    logger.info("User entered invalid budget value", e);
                    DialogCreator.showErrorDialog("Budget field invalid", "Invalid budget, please enter a valid number.");
                    return;
                }
                try {
                    model.setBudget(budgetValue);
                } catch (InvalidInputException e) {
                    DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                    return;
                }
            }

            // Manager
            UserModel selectedManager = managerComboBox.getSelectionModel().getSelectedItem();
            if (selectedManager == null || selectedManager == UserModel.getEmptyUser()) {
                model.removeManager();
            }
            else {
                try {
                    model.setManager(selectedManager);
                } catch (InvalidInputException e) {
                    logger.error("User being assigned to project is invalid", e);
                    DialogCreator.showExceptionDialog(e);
                }
            }

            // Description
            if (projectDescriptionTextArea.getText() != null) {
                model.setProjectDescription(projectDescriptionTextArea.getText());
            }

            // Persist updated model
            try {
                model.persistData();
            } catch (DBException e) {
                logger.error("Unable to persist model", e);
                DialogCreator.showExceptionDialog(e);
                return;
            }

            // Return to project viewer
            ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
            pvc.setModel(model);
        }

    /**
     *
     * @param model
     */
    public void setModel(ProjectModel model) {
        this.model = model;
        update();
    }

    @Override
	public void update() {
        if (this.model != null) {
            projectNameText.setText(model.getName());
            editNameField.setText(model.getName());
            projectDescriptionTextArea.setText(model.getProjectDescription());
            try {
                if (model.getStartDate() != null) {
                    LocalDate startDate = LocalDate.parse(model.getStartDateString());
                    editStartDatePicker.setValue(startDate);
                }

                if (model.getDeadline() != null) {
                    LocalDate deadline = LocalDate.parse(model.getDeadlineString());
                    editDeadlinePicker.setValue(deadline);
                }
            } catch (DateTimeParseException e) {
                DialogCreator.showExceptionDialog(e);
            }

            editBudgetField.setText(Double.toString(model.getBudget()));

            // Populate manager list
            List<UserModel> userList;
            try {
                userList = UserModel.getAll();
            } catch (ModelNotFoundException e) {
                logger.debug("No users found.");
                userList = new LinkedList<>();
            }
            userList.add(0, UserModel.getEmptyUser());
            ObservableList<UserModel> observableUserList = FXCollections.observableList(userList);
            managerComboBox.setItems(observableUserList);
            if (model.getManager() != null) managerComboBox.getSelectionModel().select(model.getManager());
        }
    }
}

