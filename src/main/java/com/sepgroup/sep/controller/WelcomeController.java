package com.sepgroup.sep.controller;

import java.io.IOException;
import java.util.List;

import com.sepgroup.sep.Main;

import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.collections.FXCollections;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jeremybrown on 2016-05-17.
 */
public class WelcomeController extends AbstractController {

    private static Logger logger = LoggerFactory.getLogger(WelcomeController.class);

    private ProjectModel selectedProject;

    @FXML
    public ListView<ProjectModel> existingProjectsList;

    @FXML
    public Button createNewProjectButton;

    @FXML
    public Button openSelectedProjectButton;


    public WelcomeController() {
        setFxmlPath("/views/welcome.fxml");
        setCssPath("/style/stylesheet.css");
    }

    @FXML
    public void initialize(){
        // Will be called by FXMLLoader
        populateExistingProjects();
    }

    private void populateExistingProjects() {
        try {
            List<ProjectModel> projects = ProjectModel.getAll();
            ObservableList<ProjectModel> observableProjectsList = FXCollections.observableList(projects);
            existingProjectsList.setItems(observableProjectsList);
        }
        catch (ModelNotFoundException e) {
            existingProjectsList.setDisable(true);
        }

    }

    /**
     * Moves to createproject
     */
    @FXML
    public void onCreateNewProjectClicked() {
        Main.setPrimaryScene(new ProjectCreatorController());
    }

    @FXML
    public void onProjectListSelected() {
        selectedProject = existingProjectsList.getSelectionModel().getSelectedItem();
        if (selectedProject != null) {
            openSelectedProjectButton.setDisable(false);
        }
        else {
            openSelectedProjectButton.setDisable(true);
        }
    }

    /**
     * Moves to editview 
     */
    @FXML
    public void onOpenSelectedProjectClicked() {
        if (selectedProject != null) {
            ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(new ProjectViewerController());
            pvc.setModel(selectedProject);
        }
    }

    @Override
    public void update() {
        // None needed for this controller
    }
}
