package com.sepgroup.sep.controller;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.model.UserModel;
import com.sepgroup.sep.utils.DateUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
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
	public DatePicker editStartDatePicker;
	@FXML
	public DatePicker editDeadlinePicker;
	@FXML
	public TextField editManagerField;
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

	
	public String editNameFromField = " ";
	public int editBudgetFromField = 0;
	public String editDescription;
	public String editManagerFromField = " ";
	
	/**
	 * Returns to projectview
	 */
	@FXML
    public void onEditCancelClicked() {

		  ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(new ProjectViewerController());
	        pvc.setModel(model);
    }
	
	@FXML
    public void onDeleteClicked() {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Warning");
		alert.setHeaderText("This will delete the project named " + model.getName() + ". Action cannot be undone");
		alert.setContentText("Are you sure?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			
			Main.setPrimaryScene(new WelcomeController());
	    
		} else {
		    // ... CANCEL PRESSED, goes back to the same screen
			
			  ProjectEditorController pvc = (ProjectEditorController) Main.setPrimaryScene(new ProjectEditorController());
		        pvc.setModel(model);
		
	 	
		}}
	
		@FXML
	    public void onEditUpdateClicked()  {
			System.out.println("test");


			if (editNameField.getText() != ""){
	            editNameFromField = editNameField.getText();
	            model.setName(editNameFromField);
			}
			System.out.println("test1");

			Date startDate = null;
			Date deadline = null;
			if (editDeadlinePicker.getValue() != null){
				try{
				deadline = DateUtils.castStringToDate(editDeadlinePicker.getValue().toString());
				}catch(Exception e){
					e.getMessage();
				}}
				model.setDeadline(deadline);
				if (editStartDatePicker.getValue() != null){
					try{
					startDate = DateUtils.castStringToDate(editStartDatePicker.getValue().toString());
					}catch(Exception e){
						e.getMessage();
					}
				model.setStartDate(startDate);
	       }
				System.out.println("test2");

			if (editBudgetField.getText() != ""){
				editBudgetFromField = Integer.parseInt(editBudgetField.getText());
				model.setBudget(editBudgetFromField);
	        }
			System.out.println("test3");

	
             if(editManagerField.getText() != ""){
            	 editManagerFromField = editManagerField.getText();
            	 model.setManagerUserId(Integer.parseInt(editManagerFromField));
            	 
             }

 			System.out.println("test4");
try {
	model.persistData();
} catch (DBException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
			  ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(new ProjectViewerController());
		        pvc.setModel(model);
		
			
    }

    /**
     *
     * @param model
     */
    public void setModel(ProjectModel model) {
        this.model = model;
        update();
    }

    @Override
	public void update() {
    	 if (this.model != null) {
             projectNameText.setText(model.getName());
             projectNameLabel.setText(model.getName());
             projectDescriptionTextArea.setText(model.getProjectDescription());
             startDateValueLabel.setText(model.getStartDateString());
             deadlineValueLabel.setText(model.getDeadlineString());
             budgetValueLabel.setText(Double.toString(model.getBudget()));

             // Populate manager
             String managerName = "";
             int managerUserID = model.getManagerUserId();
             if (managerUserID == 0) {
                 managerName = "[ none ]";
             }
             else {
                 try {
                     UserModel manager = UserModel.getById(managerUserID);
                     managerName = manager.getFirstName() + " " + manager.getLastName();
                 } catch (ModelNotFoundException e) {
                 }
             }

             managerValueLabel.setText(managerName);


             }
         }
		// None needed for this controller
	}

