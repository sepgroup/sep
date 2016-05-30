package com.sepgroup.sep.project;

import java.io.IOException;

import com.sepgroup.sep.AbstractController;
import com.sepgroup.sep.Main;

import javafx.fxml.FXML;

/**
 * Created by jeremybrown on 2016-05-17.
 */
public class ProjectController extends AbstractController {

    public ProjectController() {
        setFxmlPath("/views/projectview.fxml");
    }
    /**
     * Moves to createproject
     */
    @FXML
    public void onCreateNewProjectClicked() {
              
            try {
                	
                  Main.setPrimaryScene(new ProjectCreator());
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
                	
                  Main.setPrimaryScene(new ProjectEditor());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    
    @Override
    public void update() {

    }
}
