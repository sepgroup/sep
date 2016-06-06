package com.sepgroup.sep.controller;

import java.util.Date;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.model.UserModel;
import com.sepgroup.sep.utils.DateUtils;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Created by jeremybrown on 2016-06-01.
 */
public class TaskEditorController extends AbstractController {

    private TaskModel model;

    public TaskEditorController() {
        setFxmlPath("/views/welcome.fxml");
        setCssPath("/style/stylesheet.css");
    }
    @FXML
	public TextField editTaskNameField;
    @FXML
  	public Label taskNameLabel;   
    
    @FXML
  	public Label taskIdLabel;  
    
	@FXML
	public TextField editTaskBudgetField;
	
	@FXML
	public Label taskBudgetValueLabel;
	
	
	@FXML
	public DatePicker editStartDateTaskField;
	
	@FXML
	public Label taskStartDateValueLabel;
	
	@FXML
	public DatePicker editDeadlineTaskField;
	
	@FXML
	public Label taskDeadlineValueLabel;
	
	@FXML
	public TextField assigneeTaskField;
	
	@FXML
	public Label assigneeTaskLabel;
	

	@FXML
	public TextArea editDescText;
	@FXML
	public Label projectNameLabel;
    @FXML
    public Label budgetValueLabel;
    @FXML
    public Label startDateValueLabel;
    @FXML
    public Label deadlineValueLabel;
    @FXML
    public Label taskManagerLabel;
    @FXML
    public Label ManagerTaskField;

    @FXML
    public TextArea projectDescriptionTextArea;
    
    @FXML
    public ListView dependenciesTaskList;
    
    
	public String editTaskNameFromField = " ";
	public double editTaskBudgetFromField = 0;
	public String editTaskDescriptionFromField;
	public String editAssigneeFromField = " ";
	public int editTaskManagerFromField;

    
    @FXML
    public void onEditTaskCancelClicked() {

		  ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(new ProjectViewerController());
	    
    }
    
    @FXML
    public void onEditTaskUpdateClicked() {

    	try{
			if (editTaskNameField.getText() != ""){
	            editTaskNameFromField = editTaskNameField.getText();
	            model.setName(editTaskNameFromField);
			}
}
catch(Exception n){
	//Catch null
}

			Date startTaskDate = new Date();
			Date taskDeadline = new Date();
			try{
			if (editStartDateTaskField.getValue() != null){
				try{
				startTaskDate = DateUtils.castStringToDate(editStartDateTaskField.getValue().toString());
				}catch(Exception e){
					e.getMessage();
				}}
	
				model.setStartDate(startTaskDate);;
				}
				catch(Exception n){
					//Catch null
				}
			try{
				if (editDeadlineTaskField.getValue() != null){
					try{
					taskDeadline = DateUtils.castStringToDate(editDeadlineTaskField.getValue().toString());
					}catch(Exception e){
						e.getMessage();
					}
				model.setStartDate(taskDeadline);
				
	       }
			}
				catch(Exception n){
					//Catch null
				}

				try{
			if (editTaskBudgetField.getText() != ""){
				editTaskBudgetFromField = Integer.parseInt(editTaskBudgetField.getText());
				model.setBudget(editTaskBudgetFromField);
	        }
				}
				catch(Exception n){
					//Catch null
				}

				try{
					if(assigneeTaskField.getText() != ""){
						editAssigneeFromField = assigneeTaskField.getText();
						model.setAssigneeUserId(Integer.parseInt(editAssigneeFromField));
            	 
					}
				}
				catch(Exception n){
					//Catch null
				}
				
				try{
					if (ManagerTaskField.getText() != ""){
						editTaskManagerFromField = Integer.parseInt(editTaskBudgetField.getText());
					
			        }
						}
						catch(Exception n){
							//Catch null
						}

				try {
					model.persistData();
				} catch (DBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
}
			  ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(new ProjectViewerController());
		       
    }
	
    
    
    
    
    
    
    public void setModel(TaskModel t) {
        this.model = t;
        update();
    }

    @Override
    public void update() {
   	 if (this.model != null) {
   		taskIdLabel.setText(String.valueOf(model.getTaskId()));
   		taskNameLabel.setText(model.getName());
   		taskBudgetValueLabel.setText(String.valueOf(model.getBudget()));
   		taskStartDateValueLabel.setText(String.valueOf(model.getStartDate()));
   		taskDeadlineValueLabel.setText(String.valueOf(model.getDeadline()));
   		assigneeTaskLabel.setText(String.valueOf(model.getAssigneeUserId()));
   		
        }
    }}
