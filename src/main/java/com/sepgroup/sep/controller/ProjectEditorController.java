package com.sepgroup.sep.controller;


import java.io.IOException;
import java.util.Optional;

import com.sepgroup.sep.Main;

import com.sepgroup.sep.model.ProjectModel;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;

/**
 * Created by Charles on 2016-05-22.
 */
public class ProjectEditorController extends AbstractController {

	private ProjectModel model;

	public ProjectEditorController() {
        setFxmlPath("/views/projecteditor.fxml");
        setCssPath("/style/stylesheet.css");
    }
	
	@FXML
	public TextField editNameField;
	@FXML
	public TextField editBudgetField;
	@FXML
<<<<<<< HEAD
	public DatePicker editStartDateField;
	@FXML
	public DatePicker editDeadlineField;
	@FXML
	public TextField editManagerField;
=======
	public DatePicker editStartDatePicker;
	@FXML
	public DatePicker editDeadlinePicker;
	@FXML
	public TextArea editDescText;
>>>>>>> master
	
	public String editNameFromField = " ";
	public int editBudgetFromField = 0;
	public String editDescription;
	
	/**
	 * Returns to projectview
	 */
	@FXML
    public void onEditCancelClicked() {
<<<<<<< HEAD
		try {
			Main.setPrimaryScene(new ProjectViewerController(null));
		} catch (IOException e) {
			e.printStackTrace();
		}
=======
        Main.getPrimaryStage().setScene(getPreviousScene());
>>>>>>> master
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

    /**
     *
     * @param model
     */
    public void setModel(ProjectModel model) {
        this.model = model;
    }

    @Override
	public void update() {
		// None needed for this controller
	}
}
