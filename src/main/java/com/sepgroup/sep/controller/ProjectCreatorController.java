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
	
	public String nameFromField = " ";
	public int budgetFromField = 0;
	public String startDateFromField = "0000-00-00 ";
	public String deadlineFromField = "0000-00-00 ";
	
	
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

        // TODO Get date from date picker
//        startDatePicker.getValue(). ...
//		if (startDateField.getText() != ""){
//			startDateFromField = startDateField.getText();
//        }
//
//		if (deadlineField.getText() != ""){
//			deadlineFromField = deadlineField.getText();
//        }
		
		if (budgetField.getText() != ""){
			budgetFromField = Integer.parseInt(budgetField.getText());
        }

		Date startDate = null;
		Date deadline = null;
		try {
			startDate = DateUtils.castStringToDate(startDateFromField);
			deadline = DateUtils.castStringToDate(deadlineFromField);
		} catch (ParseException e) {
			// TODO handle
			// show popup or something
		}
		
		ProjectModel createdProject = new ProjectModel(nameFromField, startDate, deadline, budgetFromField, false, 0, "");
		
		try {
		    createdProject.persistData();
		}
		catch (DBException e) {
            // TODO handle
            // show popup or something
            System.out.println(e.getLocalizedMessage());
		}
		
		System.out.println(createdProject.toString());
		
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
