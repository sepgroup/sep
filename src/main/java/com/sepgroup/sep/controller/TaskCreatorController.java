package com.sepgroup.sep.controller;

import java.io.IOException;

import com.sepgroup.sep.Main;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Created by Charles on 2016-05-22.
 */
public class TaskCreatorController extends AbstractController {

	public TaskCreatorController() {
        setFxmlPath("/views/taskcreator.fxml");
        setCssPath("/style/stylesheet.css");
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
			Main.setPrimaryScene(new ProjectCreatorController());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	@Override
	public void update() {
		// None needed for this controller
	}
}
