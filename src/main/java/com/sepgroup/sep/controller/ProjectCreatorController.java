package com.sepgroup.sep.controller;

import java.io.IOException;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.utils.DateUtils;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Created by Charles on 2016-05-22.
 */
public class ProjectCreatorController extends AbstractController {

	public ProjectCreatorController() {
        setFxmlPath("/views/projectcreator.fxml");
        setCssPath("/style/stylesheet.css");
    }
	
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
	
	public String nameFromField = " ";
	public int budgetFromField = 0;
	public String description;
	
	
	/**
	 * Moves to taskview
	 */
	@FXML
    public void onAddTaskButtonClicked() {
              
            try {
                	
                  Main.setPrimaryScene(new TaskCreatorController());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
	
	/**
	 * Returns back to projectview.
	 */
	@FXML
    public void onCreateCancelClicked() {
              
            try {
                	
                  Main.setPrimaryScene(new WelcomeController());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
	
	//Test code
	@FXML
    public void onSaveProjectClicked() {
              
		if (nameField.getText() != ""){
            nameFromField = nameField.getText();
		}
		Date startDate = null;
		Date deadline = null;
		if (startDatePicker.getValue() != null && deadlinePicker.getValue() != null){
			try{
			startDate = DateUtils.castStringToDate(startDatePicker.getValue().toString());
			deadline = DateUtils.castStringToDate(deadlinePicker.getValue().toString());
			}catch(Exception e){
				e.getMessage();
			}
       }

		if (budgetField.getText() != ""){
			budgetFromField = Integer.parseInt(budgetField.getText());
        }
		
		if(descText.getText()!=""){
			description=descText.getText();
		}

		
		ProjectModel createdProject = new ProjectModel(nameFromField, startDate, deadline, budgetFromField, false, 0, description);
		
		try {
		    createdProject.persistData();
		}
		catch (DBException e) {
            // TODO handle
            // show popup or something
            System.out.println(e.getLocalizedMessage());
		}
		
		
        try {
            Main.setPrimaryScene(new ProjectViewerController(createdProject));
        } catch (IOException e) {
           e.printStackTrace();
       }
    }
	
	@Override
	public void update() {
		// None needed for this class
	}
}
