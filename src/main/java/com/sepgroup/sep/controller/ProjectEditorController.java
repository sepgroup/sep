package com.sepgroup.sep.controller;

import java.text.ParseException;
import java.util.Date;

import com.sepgroup.sep.model.InvalidInputException;
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
	public TextField editManagerField;
	@FXML
	public Label projectNameLabel;
    @FXML
    public Label budgetValueLabel;
    @FXML
    public Label startDateValueLabel;
    @FXML
    public Label deadlineValueLabel;
    @FXML
    public Label managerValueLabel;
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
            // TODO use name instead of user ID
            if (!editManagerField.getText().equals("")) {
                int managerID;
                try {
                    managerID = Integer.parseInt(editManagerField.getText());
                } catch (NumberFormatException e) {
                    DialogCreator.showErrorDialog("Invalid user ID", "Enter a valid manager user ID.");
                    return;
                }
                try {
                    model.setManagerUserId(managerID);
                } catch (InvalidInputException e) {
                    DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                    return;
                }
            }

            // Description
            if (!projectDescriptionTextArea.getText().equals("")) {
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
            projectNameLabel.setText(model.getName());
            projectDescriptionTextArea.setText(model.getProjectDescription());
            startDateValueLabel.setText(model.getStartDateString());
            deadlineValueLabel.setText(model.getDeadlineString());
            budgetValueLabel.setText(Double.toString(model.getBudget()));

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
                    // TODO handle
                } catch (InvalidInputException e) {
                    // TODO handle
                }
            }

            // TODO fix if we want manager name
            managerValueLabel.setText(Integer.toString(managerUserID));
        }
    }
}

