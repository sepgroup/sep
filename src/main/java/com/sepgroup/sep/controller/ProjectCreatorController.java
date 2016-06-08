package com.sepgroup.sep.controller;

import java.util.Date;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.InvalidInputException;
import com.sepgroup.sep.model.ProjectModel;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Charles on 2016-05-22.
 */
public class ProjectCreatorController extends AbstractController {

    private static Logger logger = LoggerFactory.getLogger(ProjectCreatorController.class);
	private static final String fxmlPath = "/views/projectcreator.fxml";

	/**
	 * TextField for project creation.
	 */
	@FXML
	public TextField nameField;
	@FXML
	public TextField budgetField;
	@FXML
	public DatePicker startDatePicker;
	@FXML
	public DatePicker deadlinePicker;
	@FXML
	public TextArea descText;

	public String nameFromField = "";
	public int budgetFromField = 0;
	public String description = "";

	public ProjectCreatorController() {
		setCssPath("/style/stylesheet.css");
	}

    public static String getFxmlPath() {
        return fxmlPath;
    }

    /**
	 * Returns back to projectview.
	 */
	@FXML
    public void onCreateCancelClicked() {
        Main.setPrimaryScene(WelcomeController.getFxmlPath());
    }

	@FXML
    public void onSaveProjectClicked() {
        ProjectModel createdProject = new ProjectModel();

        // Name
        try {
            createdProject.setName(nameField.getText());
        } catch (InvalidInputException e) {
            DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
            return;
        }

        // Start date
		Date startDate = null;
		if (startDatePicker.getValue() != null) {
            try {
                createdProject.setStartDate(startDatePicker.getValue().toString());
            } catch (InvalidInputException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                logger.error(errorContent);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
        }

        // Deadline
        Date deadline = null;
        if (deadlinePicker.getValue() != null) {
            try {
                createdProject.setDeadline(deadlinePicker.getValue().toString());
            } catch (InvalidInputException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                logger.error(errorContent);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
        }

        // Budget
		if (!budgetField.getText().equals("")) {
            double budgetDouble = 0;
            try {
                budgetDouble = Double.parseDouble(budgetField.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid budget value");
                DialogCreator.showErrorDialog("Budget field invalid", "Invalid budget, please enter a valid number.");
                return;
            }
            try {
                createdProject.setBudget(budgetDouble);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }

        // Description
		if (!descText.getText().equals("")) {
            createdProject.setProjectDescription(descText.getText());
		}

        logger.debug("Created project " + createdProject.toString());

        // Persist created Project
		try {
		    createdProject.persistData();
		}
		catch (DBException e) {
            DialogCreator.showErrorDialog("Database error", e.getLocalizedMessage());
			return;
		}

        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
        pvc.setModel(createdProject);
    }

	@Override
	public void update() {
		// None needed for this controller
	}
}
