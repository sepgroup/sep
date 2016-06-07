package com.sepgroup.sep.controller;

import java.text.ParseException;
import java.util.Date;

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

    private String editNameFromField = "";
    private int editBudgetFromField = 0;
    private String editManagerFromField = "";
    private String editDescriptionFromField = "";

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
            logger.error("Unable to refresh model from DB, model could not be found in DB.");
            DialogCreator.showExceptionDialog(e);
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
                editNameFromField = editNameField.getText();
                model.setName(editNameFromField);
            }

			// Deadline
            if (editDeadlinePicker.getValue() != null) {
                try {
                    Date deadline = DateUtils.castStringToDate(editDeadlinePicker.getValue().toString());
                    model.setDeadline(deadline);
                } catch (ParseException e) {
                    String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                    logger.error(errorContent, e);
                    DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                    return;
                }
            }

            if (editStartDatePicker.getValue() != null) {
                try {
                    Date startDate = DateUtils.castStringToDate(editStartDatePicker.getValue().toString());
                    model.setStartDate(startDate);
                } catch (ParseException e) {
                    String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                    logger.error(errorContent, e);
                    DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                    return;
                }
            }

            // Budget
            if (!editBudgetField.getText().equals("")) {
                try {
                    editBudgetFromField = Integer.parseInt(editBudgetField.getText());
                    model.setBudget(editBudgetFromField);
                } catch (NumberFormatException e) {
                    logger.info("User entered invalid budget value", e);
                    DialogCreator.showErrorDialog("Budget field invalid", "Invalid budget, please enter a valid number.");
                    return;
                }
            }

            // Manager
            if (!editManagerField.getText().equals("")) {
                editManagerFromField = editManagerField.getText();
                // TODO use name instead of user ID
                model.setManagerUserId(Integer.parseInt(editManagerFromField));
            }

            // Description
            if (!projectDescriptionTextArea.getText().equals("")) {
                editDescriptionFromField = projectDescriptionTextArea.getText();
                model.setProjectDescription(editDescriptionFromField);
            }

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
                }
            }

            // TODO fix if we want manager name
            managerValueLabel.setText(Integer.toString(managerUserID));
        }
    }
}

