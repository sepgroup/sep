package com.sepgroup.sep.project;

import java.io.IOException;

import com.sepgroup.sep.AbstractController;
import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.task.TaskController;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Created by Charles on 2016-05-22.
 */
public class ProjectCreator extends AbstractController {

	public ProjectCreator() {
        setFxmlPath("/views/projectcreator.fxml");
    }
	
	/**
	 * TextField for project creation.
	 */
	@FXML
	public TextField nameField;
	@FXML
	public TextField budgetField;
	@FXML
	public TextField startDateField;
	@FXML
	public TextField deadlineField;
	
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
                	
                  Main.setPrimaryScene(new TaskController());
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
                	
                  Main.setPrimaryScene(new ProjectController());
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
		if (startDateField.getText() != ""){
			startDateFromField = startDateField.getText();
        }
		
		if (deadlineField.getText() != ""){
			deadlineFromField = deadlineField.getText();
        }
		
		if (budgetField.getText() != ""){
			budgetFromField = Integer.parseInt(budgetField.getText());
        }
		
		ProjectModel createdProject = new ProjectModel(nameFromField, startDateFromField, deadlineFromField, budgetFromField, false);
		
		try{
		createdProject.persistData();
		}
		catch (DBException e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		System.out.println(createdProject.toString());
		
		 try {
         	
             Main.setPrimaryScene(new ProjectController());
             
		 } catch (IOException e) {
           e.printStackTrace();
       }
		
    }
	
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}


}
