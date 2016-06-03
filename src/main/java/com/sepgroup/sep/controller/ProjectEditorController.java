package com.sepgroup.sep.controller;


import java.io.IOException;

import com.sepgroup.sep.Main;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
	public DatePicker editStartDatePicker;
	@FXML
	public DatePicker editDeadlinePicker;
	@FXML
	public TextArea editDescText;
	
	public String editNameFromField = " ";
	public int editBudgetFromField = 0;
	public String editDescription;
	
	/**
	 * Returns to projectview
	 */
	@FXML
    public void onEditCancelClicked() {
		try {
			Main.setPrimaryScene(new WelcomeController());
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
		// None needed for this controller
	}
}
