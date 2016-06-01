package com.sepgroup.sep.project;


import java.io.IOException;

import com.sepgroup.sep.AbstractController;
import com.sepgroup.sep.Main;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Created by Charles on 2016-05-22.
 */
public class ProjectEditor extends AbstractController {

	public ProjectEditor() {
        setFxmlPath("/views/projecteditor.fxml");
        setCssPath("/style/stylesheet.css");

    }
	
	@FXML
	public TextField editNameField;
	@FXML
	public TextField editBudgetField;
	@FXML
	public TextField editStartDateField;
	@FXML
	public TextField editDeadlineField;
	
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
                	
                  Main.setPrimaryScene(new ProjectController());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
	
	@FXML
    public void onDeleteClicked() {
              
		if (editNameField.getText() != ""){
			editNameFromField = editNameField.getText();
     
	}
		
		
		
    }
	
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}


}