package com.sepgroup.sep.controller;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.InvalidInputException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.UserModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by jeremybrown on 2016-06-28.
 */
public class UserCreatorController extends AbstractController {

    private static Logger logger = LoggerFactory.getLogger(UserCreatorController.class);
    private static final String fxmlPath = "/views/usercreator.fxml";
    private ProjectModel returnProject;

    @FXML
    TextField firstNameField;

    @FXML
    TextField lastNameField;

    @FXML
    TextField salaryPerHourField;

    public UserCreatorController() {
        setCssPath("/style/stylesheet.css");
    }

    @FXML
    public void initialize() {
        // Set initial focus to first name field
        firstNameField.requestFocus();
    }

    public void onUserCancelClicked() throws IOException {
        returnToProject();
    }

    public void onCreateUserClicked() {
        UserModel createdUser = new UserModel();

        // First name
        if (!lastNameField.getText().isEmpty()) {
            String firstName = firstNameField.getText();
            try {
                createdUser.setFirstName(firstName);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }

        // Last name
        if (!lastNameField.getText().isEmpty()) {
            String lastName = lastNameField.getText();
            try {
                createdUser.setLastName(lastName);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }

        // Salary per hour
        if (!salaryPerHourField.getText().equals("")) {
            double salaryDouble = 0;
            try {
                salaryDouble = Double.parseDouble(salaryPerHourField.getText());
            } catch (NumberFormatException e) {
                logger.info("User entered invalid budget value");
                DialogCreator.showErrorDialog("Budget field invalid", "Invalid budget, please enter a valid number.");
                return;
            }
            try {
                createdUser.setSalaryPerHour(salaryDouble);
            } catch (InvalidInputException e) {
                DialogCreator.showErrorDialog("Invalid input", e.getLocalizedMessage());
                return;
            }
        }

        // Persist created User
        try {
            createdUser.persistData();
        } catch (DBException e) {
            DialogCreator.showErrorDialog("Database error", e.getLocalizedMessage());
            return;
        }

        returnToProject();
    }

    public void setReturnProject(ProjectModel p) {
        returnProject = p;
    }

    private void returnToProject() {
        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
        pvc.setModel(returnProject);
    }

    public static String getFxmlPath() {
        return fxmlPath;
    }

    @Override
    public void update() {
        // None for this view / controller
    }
}
