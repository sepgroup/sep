package com.sepgroup.sep.controller;

import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.model.UserModel;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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
	public Label taskDadlineValueLabel;
	
	@FXML
	public TextField assigneeTaskField;
	
	@FXML
	public Label assigneeTaskLabel;
	
	

	
	@FXML
	public DatePicker editDeadlinePicker;
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
    public Label managerValueLabel;
    @FXML
    public Text projectNameText;
    @FXML
    public TextArea projectDescriptionTextArea;

    public void setModel(TaskModel t) {
        this.model = t;
        update();
    }

    @Override
    public void update() {
   	 if (this.model != null) {
   		taskIdLabel.setText(String.valueOf(model.getTaskId()));
   		taskNameLabel.setText(model.getTagsString());
   		taskBudgetValueLabel.setText(String.valueOf(model.getBudget()));
   		taskStartDateValueLabel.setText(String.valueOf(model.getStartDate()));
   		taskDadlineValueLabel.setText(String.valueOf(model.getDeadline()));
   		assigneeTaskLabel.setText(String.valueOf(model.getAssigneeUserId()));
   		
   		
        }
    }}
