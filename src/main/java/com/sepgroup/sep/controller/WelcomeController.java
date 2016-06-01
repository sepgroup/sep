package com.sepgroup.sep.controller;

import java.io.IOException;

import com.sepgroup.sep.Main;

import javafx.fxml.FXML;

/**
 * Created by jeremybrown on 2016-05-17.
 */
public class WelcomeController extends AbstractController {

    public WelcomeController() {
        setFxmlPath("/views/welcome.fxml");
        setCssPath("/style/stylesheet.css");
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
        }
    }

    /**
     * Moves to editview 
     */
    @FXML
    public void onEditProjectClicked() {
        try {
            Main.setPrimaryScene(new ProjectEditorController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        // None needed for this controller
    }
}
