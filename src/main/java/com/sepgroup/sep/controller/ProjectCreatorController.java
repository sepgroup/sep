package com.sepgroup.sep.controller;

import java.text.ParseException;
import java.util.Date;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.utils.DateUtils;

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
		setFxmlPath("/views/projectcreator.fxml");
		setCssPath("/style/stylesheet.css");
	}

	/**
	 * Returns back to projectview.
	 */
	@FXML
    public void onCreateCancelClicked() {
        Main.setPrimaryScene(new WelcomeController());
    }

	@FXML
    public void onSaveProjectClicked() {
        // Name
		if (nameField.getText().length() > 0) {
            nameFromField = nameField.getText();
		}

        // Start date
		Date startDate = null;
		if (startDatePicker.getValue() != null) {
            try {
                startDate = DateUtils.castStringToDate(startDatePicker.getValue().toString());
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                logger.error(errorContent, e);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
        }

        // Deadline
        Date deadline = null;
        if (deadlinePicker.getValue() != null) {
            try {
                deadline = DateUtils.castStringToDate(deadlinePicker.getValue().toString());
            } catch (ParseException e) {
                String errorContent = "Unable to parse date from DatePicker, this really shouldn't happen";
                logger.error(errorContent, e);
                DialogCreator.showErrorDialog("Unable to parse date", errorContent);
                return;
            }
        }

        // Budget
		if (!budgetField.getText().equals("")) {
            try {
                budgetFromField = Integer.parseInt(budgetField.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid budget value", e);
                DialogCreator.showErrorDialog("Budget field invalid", "Invalid budget, please enter a valid number.");
                return;
            }
        }
		
		if (!descText.getText().equals("")) {
			description = descText.getText();
		}

		
		ProjectModel createdProject = new ProjectModel(nameFromField, startDate, deadline, budgetFromField, false, 0, description);
        logger.debug("Created project " + createdProject.toString());

		try {
		    createdProject.persistData();
		}
		catch (DBException e) {
            logger.error("DB error", e);
            DialogCreator.showErrorDialog("Database error", e.getLocalizedMessage());
		}

        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(new ProjectViewerController());
        pvc.setModel(createdProject);
    }
	
	@Override
	public void update() {
		// None needed for this controller
	}
}
