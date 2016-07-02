package com.sepgroup.sep.controller;

import java.util.List;

import com.sepgroup.sep.Main;

import com.sepgroup.sep.model.InvalidInputException;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jeremybrown on 2016-05-17.
 */
public class WelcomeController extends AbstractController {

    private static Logger logger = LoggerFactory.getLogger(WelcomeController.class);
    private static final String fxmlPath = "/views/welcome.fxml";
    private ProjectModel selectedProject;

    @FXML
    public ListView<ProjectModel> existingProjectsList;
    @FXML
    public Button createNewProjectButton;
    @FXML
    public Button openSelectedProjectButton;

    public WelcomeController() {
        setCssPath("/style/stylesheet.css");
    }

    public static String getFxmlPath() {
        return fxmlPath;
    }

    @FXML
    public void initialize() {
        // Will be called by FXMLLoader
        populateExistingProjects();
    }

    private void populateExistingProjects() {
        try {
            List<ProjectModel> projects = ProjectModel.getAll();
            ObservableList<ProjectModel> observableProjectsList = FXCollections.observableList(projects);
            logger.debug("Populating existing projects list");
            existingProjectsList.setItems(observableProjectsList);
        }
        catch (ModelNotFoundException e) {
            logger.debug("No projects found to populate existing projects list");
            existingProjectsList.setDisable(true);
        }
        catch (InvalidInputException e) {
            logger.error("Invalid data in DB");
        }

    }

    /**
     * Moves to project creator
     */
    @FXML
    public void onCreateNewProjectClicked() {
        Main.setPrimaryScene(ProjectCreatorController.getFxmlPath());
    }

    /**
     * Handle click on project list
     * @param e the MouseEvent
     */
    @FXML
    public void onProjectListSelected(MouseEvent e) {
        selectedProject = existingProjectsList.getSelectionModel().getSelectedItem();
        if (selectedProject != null) {
            openSelectedProjectButton.setDisable(false);
            if (e.getClickCount() == 2) {
                openProjectViewer(selectedProject);
            }
        }
        else {
            openSelectedProjectButton.setDisable(true);
        }
    }

    /**
     * Moves to project viewer
     */
    @FXML
    public void onOpenSelectedProjectClicked() {
        if (selectedProject != null) {
            openProjectViewer(selectedProject);
        }
    }

    /**
     * Open project viewer with specified project
     * @param project the project to open
     */
    private void openProjectViewer(ProjectModel project) {
        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
        pvc.setModel(project);
    }

    @Override
    public void update() {
        // None needed for this controller
    }
}
