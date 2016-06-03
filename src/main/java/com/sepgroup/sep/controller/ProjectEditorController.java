package com.sepgroup.sep.controller;


import java.io.IOException;
import java.util.Optional;

import com.sepgroup.sep.Main;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;

/**
 * Created by Charles on 2016-05-22.
 */
public class ProjectEditorController extends AbstractController {

	public ProjectEditorController() {
        setFxmlPath("/views/projecteditor.fxml");
        setCssPath("/style/stylesheet.css");
    }
	
	@FXML
	public TextField editNameField;
	@FXML
	public TextField editBudgetField;
	@FXML
	public DatePicker editStartDateField;
	@FXML
	public DatePicker editDeadlineField;
	@FXML
	public TextField editManagerField;
	
	public String editNameFromField = " ";
	public int editBudgetFromField = 0;
	public String editStartDateFromField = "0000-00-00 ";
	public String editDeadlineFromField = "0000-00-00 ";
	
	/**
	 * Returns to projectview
	 */
	@FXML
    public void onEditCancelClicked() {
		try {
			Main.setPrimaryScene(new ProjectViewerController(null));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	@FXML
    public void onDeleteClicked() {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Warning");
		alert.setHeaderText("This will delete the current project. Action cannot be undone");
		alert.setContentText("Are you sure?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    /*
		     * Here goes the code for deleting the project.
		     * 
		     */
			
			try {
				Main.setPrimaryScene(new WelcomeController());
			} catch (IOException e) {
				e.printStackTrace();
			}
	    
		} else {
		    // ... CANCEL PRESSED, goes back to the same screen
			
			try {
				Main.setPrimaryScene(new ProjectEditorController());
			} catch (IOException e) {
				e.printStackTrace();
			}}
	 	
		}
	
		@FXML
	    public void onUpdateClicked() {
		
			/*
			 * 
			 * Here goes the code for updating the project info
			 * 
			 */
    }

	@Override
	public void update() {
		// None needed for this controller
	}
}
