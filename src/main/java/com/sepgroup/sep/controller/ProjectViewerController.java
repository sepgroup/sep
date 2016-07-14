package com.sepgroup.sep.controller;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.Observer;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.*;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jeremybrown on 2016-06-01.
 */
public class ProjectViewerController extends AbstractController {

	private static Logger logger = LoggerFactory.getLogger(ProjectViewerController.class);
	private static final String fxmlPath = "/views/projectviewer.fxml";
	private static ProjectModel model;


	@FXML
	public Text projectNameText;
	@FXML
	public TextArea projectDescriptionTextArea;
	@FXML
	public Label startDateValueLabel;
	@FXML
	public Label deadlineValueLabel;
	@FXML
	public Label managerValueLabel;
	@FXML
	public Label budgetValueLabel;
	@FXML
	public Label completeValueLabel;
	@FXML
	public TableView<ListableTaskModel> taskTableView;

	@FXML
	public TableColumn<ListableTaskModel, Integer> taskIdColumn;
	@FXML
	public TableColumn<ListableTaskModel, String> taskNameColumn;
	@FXML
	public TableColumn<ListableTaskModel, Double> taskBudgetColumn;
	@FXML
	public TableColumn<ListableTaskModel, String> startDateColumn;
	@FXML
	public TableColumn<ListableTaskModel, String> deadlineColumn;
	@FXML
	public TableColumn<ListableTaskModel, Boolean> taskCompleteColumn;
	@FXML
	public Button createGanttChartButton;
	

	public ProjectViewerController() {
		setCssPath("/style/stylesheet.css");
	}

	public static String getFxmlPath() {
		return fxmlPath;
	}

	public void onEditClicked() {
		ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectEditorController.getFxmlPath());
		pvc.setModel(model);	
	}

	public void setModel(ProjectModel p) {
		this.model = p;
		update();
	}


	@Override
	public void update() {
		if (this.model != null) {
			projectNameText.setText(model.getName());
			if (model.getProjectDescription() != null) projectDescriptionTextArea.setText(model.getProjectDescription());
			if (model.getStartDate() != null) startDateValueLabel.setText(model.getStartDateString());
			if (model.getDeadline() != null) deadlineValueLabel.setText(model.getDeadlineString());
			budgetValueLabel.setText(Double.toString(model.getBudget()));
			completeValueLabel.setText(model.isDone() ? "Yes" : "No");

			// Populate manager
			String managerName = "";
			int managerUserID = model.getManagerUserId();
			if (managerUserID == 0) {
				managerName = "[ none ]";
			} else {
				try {
					UserModel manager = UserModel.getById(managerUserID);
					managerName = manager.getFirstName() + " " + manager.getLastName();
				} catch (ModelNotFoundException e) {
					logger.error("Error finding user with ID " + managerUserID);
				} catch (InvalidInputException e) {
					logger.error("Errored data in DB");
				}
			}
			managerValueLabel.setText(managerName);

			// Populate tasks list
			try {
				logger.debug("Populating tasks list");
				List<ListableTaskModel> tasksList = TaskModel.getAllByProject(model.getProjectId()).stream()
						.map(taskModel -> new ListableTaskModel(taskModel)).collect(Collectors.toList());
				ObservableList<ListableTaskModel> observableTaskList = FXCollections.observableList(tasksList);
				taskTableView.setItems(observableTaskList);
			} catch (ModelNotFoundException e) {
				logger.debug("No tasks found for project " + model.toString());
			} catch (InvalidInputException e) {
				logger.error("Invalid data in DB");
			}
			taskIdColumn.setCellValueFactory(cellData -> cellData.getValue().taskIdProperty().asObject());
			taskNameColumn.setCellValueFactory(cellData -> cellData.getValue().taskNameProperty());
			taskBudgetColumn.setCellValueFactory(cellData -> cellData.getValue().taskBudgetProperty().asObject());
			startDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
			deadlineColumn.setCellValueFactory(cellData -> cellData.getValue().deadlineProperty());
			taskCompleteColumn.setCellValueFactory(cellData -> cellData.getValue().completedProperty());
			//Disable Chart Button if project has no tasks.
			if (taskTableView.getItems().isEmpty()){
				createGanttChartButton.setDisable(true);
				
			}
		}
	}

	public void onEditProjectClicked() {
		ProjectEditorController pec = (ProjectEditorController) Main.setPrimaryScene(ProjectEditorController.getFxmlPath());
		pec.setModel(model);
	}

	public void onCreateTaskButtonClicked() {
	
		TaskCreatorController tcc = (TaskCreatorController) Main.setPrimaryScene(TaskCreatorController.getFxmlPath());
		tcc.setReturnProject(model);
	}


	public void onCreateGanttChartClicked() throws ModelNotFoundException, InvalidInputException {
		final GanttController Gantt = new GanttController("Gantt Chart");
		Gantt.pack();
		RefineryUtilities.centerFrameOnScreen(Gantt);
		Gantt.setVisible(true);


	}       
	public static IntervalCategoryDataset createDataset() throws ModelNotFoundException, InvalidInputException {
			final TaskSeries s1 = new TaskSeries("Scheduled");
			final TaskSeries s2 = new TaskSeries("Completed");
			
			for(int i = 0; i<model.getTasks().size();i++){
				if (model.getTasks().get(i).isDone()){
				s2.add(new Task(model.getTasks().get(i).getName(),
						new SimpleTimePeriod (date( model.getTasks().get(i).getStartDate()), date(model.getTasks().get(i).getDeadline()))));}

			
				else{						
					
					if ((model.getTasks().get(i).getStartDate()!=(null))&&(model.getTasks().get(i).getDeadline()!=(null))){
				        s1.add(new Task(model.getTasks().get(i).getName(),
						new SimpleTimePeriod(date( model.getTasks().get(i).getStartDate()), date(model.getTasks().get(i).getDeadline()))));
				}}}
			
			final TaskSeriesCollection collection = new TaskSeriesCollection();
			collection.add(s1);
			collection.add(s2);
			return collection;
		}

	private static Date date(final Date date) {

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		final Date result = calendar.getTime();
		return result;

	}


	public void onTaskItemClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			ListableTaskModel selectedListableTask = taskTableView.getSelectionModel().getSelectedItem();
			if (selectedListableTask != null) {
				TaskEditorController tec = (TaskEditorController) Main.setPrimaryScene(TaskEditorController.getFxmlPath());
				tec.setModel(selectedListableTask.getModel());
				tec.setReturnProject(model);
			}
		}
	}

	public void onCreateProjectMenuItemClicked() {
		Main.setPrimaryScene(ProjectCreatorController.getFxmlPath());
	}

	public void onOpenProjectMenuItemClicked() {
		Main.setPrimaryScene(WelcomeController.getFxmlPath());
	}

	public void onCloseProjectMenuItemClicked() {
		Main.setPrimaryScene(WelcomeController.getFxmlPath());
	}

	public void onDeleteProjectMenuButtonClicked(){
		String title = "Warning!";
		String header = "This will delete the project named " + model.getName() + ". This action cannot be undone";
		String content = "Are you sure?";

		if (DialogCreator.showConfirmationDialog(title, header, content).get() == ButtonType.OK) {
			try {
				model.deleteData();
				// Go to welcome screen
				Main.setPrimaryScene(WelcomeController.getFxmlPath());
			} catch (DBException e) {
				logger.error("Unable to delete project from DB");
				DialogCreator.showErrorDialog("Unable to delete project from DB", e.getLocalizedMessage());
				return;
			}
		}
	}

	public void showInfo() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Info");
		alert.setHeaderText("Version 1.0. Team members:");
		alert.setContentText("Jeremy Brown \nAli Zoghi \nCharles Tondreau-Alin \nNicola Polesana"
				+ "\nAndres Gonzales \nDemo Kioussis \nJustin Watley \nMark Chmilar \nVince Fugnitto"
				+ "\nMichael Deom");
		alert.showAndWait();
	}

	static class ListableTaskModel implements Observer {

		private TaskModel model;

		private SimpleIntegerProperty taskId;
		private SimpleStringProperty taskName;
		private SimpleDoubleProperty taskBudget;
		private SimpleStringProperty startDate;
		private SimpleStringProperty deadline;
		private SimpleBooleanProperty completed;

		public ListableTaskModel(TaskModel m) {
			this.model = m;
			this.model.registerObserver(this);
			taskId = new SimpleIntegerProperty();
			taskName = new SimpleStringProperty();
			taskBudget = new SimpleDoubleProperty();
			startDate = new SimpleStringProperty();
			deadline = new SimpleStringProperty();
			completed = new SimpleBooleanProperty();
			update();
		}

		public TaskModel getModel() {
			return model;
		}

		public SimpleIntegerProperty taskIdProperty() {
			return taskId;
		}
		public SimpleStringProperty taskNameProperty() {
			return taskName;
		}
		public SimpleDoubleProperty taskBudgetProperty() {
			return taskBudget;
		}
		public SimpleStringProperty startDateProperty() {
			return startDate;
		}
		public SimpleStringProperty deadlineProperty() {
			return deadline;
		}
		public SimpleBooleanProperty completedProperty() {
			return completed;
		}

		@Override
		public void update() {
			if (model != null) {
				taskId.set(model.getTaskId());
				taskName.set(model.getName());
				completed.set(model.isDone());
				startDate.set(model.getStartDateString());
				deadline.set(model.getDeadlineString());
				taskBudget.set(model.getBudget());
			}
		}
	}
}
