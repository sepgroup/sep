package com.sepgroup.sep.controller;


import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.model.ProjectModel;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Created by Charles on 2016-05-22.
 */
public class ProjectEditorController extends AbstractController {

	private ProjectModel model;

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
	
	public ProjectEditorController() {
        setFxmlPath("/views/projecteditor.fxml");
        setCssPath("/style/stylesheet.css");
    }

	
	public String editNameFromField = " ";
	public int editBudgetFromField = 0;
	public String editDescription;
	
	/**
	 * Returns to projectview
	 */
	@FXML
    public void onEditCancelClicked() {
        Main.getPrimaryStage().setScene(getPreviousScene());
    }
	
	@FXML
    public void onDeleteClicked() {
		if (editNameField.getText() != ""){
			editNameFromField = editNameField.getText();
		}
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
    	if (this.model != null){
    		editNameField.appendText(model.getName());
    		editDescText.setText(model.getProjectDescription());
    		editBudgetField.setText(Double.toString(model.getBudget()));
    		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    		editStartDatePicker.setValue(LocalDate.parse(model.getStartDateString(),formatter));
    		editDeadlinePicker.setValue(LocalDate.parse(model.getDeadlineString(),formatter));
    		
	}
    }

}
