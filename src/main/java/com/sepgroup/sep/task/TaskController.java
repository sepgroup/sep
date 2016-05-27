package com.sepgroup.sep.task;

import java.io.IOException;

import com.sepgroup.sep.AbstractController;
import com.sepgroup.sep.Main;
import com.sepgroup.sep.project.ProjectController;
import com.sepgroup.sep.project.ProjectCreator;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Created by Charles on 2016-05-22.
 */
public class TaskController extends AbstractController {

	public TaskController() {
        setFxmlPath("/views/taskview.fxml");
    }
	
	@FXML
	public TextField taskNameField;
	@FXML
	public TextField taskBudgetField;
	@FXML
	public TextField taskStartDateField;
	@FXML
	public TextField taskDeadlineField;
	
	/**
	 * Returns to createproject
	 */
	@FXML
    public void onTaskCancelClicked() {
              
            try {
                	
                  Main.setPrimaryScene(new ProjectCreator());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
	

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}


}
