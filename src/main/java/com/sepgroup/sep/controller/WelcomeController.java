package com.sepgroup.sep.controller;

import java.io.IOException;
import java.util.List;

import com.sepgroup.sep.Main;

import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

/**
 * Created by jeremybrown on 2016-05-17.
 */
public class WelcomeController extends AbstractController {

    @FXML
    public ListView<ProjectModel> existingProjectsList;

    @FXML
    public Button createNewProjectButton;

    @FXML
    public Button openSelectedProjectButton;

    ProjectModel selectedProject;


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
            ObservableList<ProjectModel> observableProjectsList = new ImmutableObservableList<>();
            observableProjectsList.addAll(projects);
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
        try {
            Main.setPrimaryScene(new ProjectCreatorController());
        } catch (IOException e) {
            e.printStackTrace();
            // TODO popup or something
        }
    }

    @FXML
    public void onProjectListSelected() {
        selectedProject = existingProjectsList.getSelectionModel().getSelectedItem();
        if (selectedProject != null) openSelectedProjectButton.setDisable(false);
    }

    /**
     * Moves to editview 
     */
    @FXML
    public void onOpenSelectedProjectClicked() {
        ProjectModel selectedProject = existingProjectsList.getSelectionModel().getSelectedItem();
        try {
            Main.setPrimaryScene(new ProjectViewerController(selectedProject));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO popup or something
        }
    }

    @Override
    public void update() {
        // None needed for this controller
    }
}
