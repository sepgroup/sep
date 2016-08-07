package com.sepgroup.sep.controller;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by jeremybrown on 2016-06-01.
 */
public class ProjectViewerController extends AbstractController {

    private static Logger logger = LoggerFactory.getLogger(ProjectViewerController.class);
    private static final String fxmlPath = "/views/projectviewer.fxml";
    private static ProjectModel model;
    private List<ListableTaskModel> tasksList;

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
    public Label pvValueLabel;
    @FXML
    public Label evValueLabel;
    @FXML
    public Label bacValueLabel;
    @FXML
    public Label pscValueLabel;
    @FXML
    public Label acValueLabel;
    @FXML
    public Label pcValueLabel;
    @FXML
    public Label cvValueLabel;
    @FXML
    public Label svValueLabel;
    @FXML
    public Label cpiValueLabel;
    @FXML
    public Label spiValueLabel;
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
    public TableColumn<ListableTaskModel, String> assigneeColumn;
    @FXML
    public TableColumn<ListableTaskModel, Integer> mostLikelyColumn;
    @FXML
    public TableColumn<ListableTaskModel, Integer> pessimisticColumn;
    @FXML
    public TableColumn<ListableTaskModel, Integer> optimisticColumn;

    @FXML
    public ComboBox<UserModel> userFilterComboBox;
    @FXML
	public Button createGanttChartButton;


    public ProjectViewerController() {
        setCssPath("/style/stylesheet.css");
    }

    public static String getFxmlPath() {
        return fxmlPath;
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

            // Populate earned value analysis box
            pvValueLabel.setText(Double.toString(model.getPlannedValue()));
            evValueLabel.setText(Double.toString(model.getEarnedValue()));
            bacValueLabel.setText(Double.toString(model.getBudgetAtCompletion()));
            pscValueLabel.setText(Double.toString(model.getPercentScheduledCompletion()));
            acValueLabel.setText(Double.toString(model.getActualCost()));
            pcValueLabel.setText(Double.toString(model.getPercentComplete()));
            cvValueLabel.setText(Double.toString(model.getCostVariance()));
            svValueLabel.setText(Double.toString(model.getScheduleVariance()));
            cpiValueLabel.setText(Double.toString(model.getCostPerformanceIndex()));
            spiValueLabel.setText(Double.toString(model.getSchedulePerformanceIndex()));

            // Populate manager
            String managerName;
            if (model.getManager() == null) {
                managerName = "[ none ]";
            }
            else {
                managerName = model.getManager().getFullName();
            }
            managerValueLabel.setText(managerName);

            // Populate tasks list
            try {
                logger.debug("Populating tasks list");
                tasksList = TaskModel.getAllByProject(model.getProjectId()).stream()
                        .map(ListableTaskModel::new).collect(Collectors.toList());
                ObservableList<ListableTaskModel> observableTaskList = FXCollections.observableList(tasksList);
                taskTableView.setItems(observableTaskList);
            } catch (ModelNotFoundException e) {
                logger.debug("No tasks found for project " + model.toString());
                tasksList = new LinkedList<>();
            }
            taskIdColumn.setCellValueFactory(cellData -> cellData.getValue().taskIdProperty().asObject());
            taskNameColumn.setCellValueFactory(cellData -> cellData.getValue().taskNameProperty());
            taskBudgetColumn.setCellValueFactory(cellData -> cellData.getValue().taskBudgetProperty().asObject());
            startDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
            deadlineColumn.setCellValueFactory(cellData -> cellData.getValue().deadlineProperty());
            taskCompleteColumn.setCellValueFactory(cellData -> cellData.getValue().completedProperty());
            assigneeColumn.setCellValueFactory(cellData -> cellData.getValue().assigneeProperty());
            mostLikelyColumn.setCellValueFactory(cellData -> cellData.getValue().taskMostLikelyProperty().asObject());
            pessimisticColumn.setCellValueFactory(cellData -> cellData.getValue().taskPessimisticTimeProperty().asObject());
            optimisticColumn.setCellValueFactory(cellData -> cellData.getValue().taskOptimisticTimeProperty().asObject());

            if (taskTableView.getItems().isEmpty()) {
				createGanttChartButton.setDisable(true);
			}

            // Populate user filter combo box
            List<UserModel> userList;
            try {
                userList = UserModel.getAll();
            } catch (ModelNotFoundException e) {
                logger.debug("No users found.");
                userList = new LinkedList<>();
            }
            userList.add(0, UserModel.getEmptyUser());
            ObservableList<UserModel> observableUserList = FXCollections.observableList(userList);
            userFilterComboBox.setItems(observableUserList);
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
    
    public void onTaskItemClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            ListableTaskModel selectedListableTask = taskTableView.getSelectionModel().getSelectedItem();
            if (selectedListableTask != null) {
                TaskEditorController tec = (TaskEditorController) Main.setPrimaryScene(TaskEditorController.getFxmlPath());
//                TaskViewerController tec = (TaskViewerController) Main.setPrimaryScene(TaskViewerController.getFxmlPath());
                tec.setModel(selectedListableTask.getModel());
                tec.setReturnProject(model);
            }
        }
    }
    
    public void onCreateGanttChartClicked() throws ModelNotFoundException, InvalidInputException {
		final GanttController Gantt = new GanttController("Gantt Chart");
		Gantt.pack();
		RefineryUtilities.centerFrameOnScreen(Gantt);
		Gantt.setVisible(true);


	}       
	public static IntervalCategoryDataset createDataset() throws ModelNotFoundException, InvalidInputException {
			final TaskSeries s1 = new TaskSeries("Scheduled");
			final TaskSeries s2 = new TaskSeries("Actual");
			Date today = new Date();
			
			for(int i = 0; i<model.getTasks().size();i++){
				if (!model.getTasks().get(i).isDone()&&(((model.getTasks().get(i).getActualEndDate()!=(null))&&(model.getTasks().get(i).getActualEndDate()!=(null))))){
				s2.add(new Task(String.valueOf(model.getTasks().get(i).getTaskId()),
						new SimpleTimePeriod (date(model.getTasks().get(i).getActualStartDate()), date(model.getTasks().get(i).getActualEndDate()))));}									
					if ((model.getTasks().get(i).getStartDate()!=(null))&&(model.getTasks().get(i).getDeadline()!=(null))){
				        s1.add(new Task(String.valueOf(model.getTasks().get(i).getTaskId()),
						new SimpleTimePeriod(date( model.getTasks().get(i).getStartDate()), date(model.getTasks().get(i).getDeadline()))));
				}
					else{
						String title = "Info";
			        String header = "Task with id " + model.getTasks().get(i).getTaskId() + " is missing start date/deadline";
					

			       DialogCreator.showInfoDialog(title, header);}}
			
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


    public void onCreateProjectMenuItemClicked() {
        Main.setPrimaryScene(ProjectCreatorController.getFxmlPath());
    }

    public void onOpenProjectMenuItemClicked() {
        Main.setPrimaryScene(WelcomeController.getFxmlPath());
    }

    public void onCloseProjectMenuItemClicked() {
        Main.setPrimaryScene(WelcomeController.getFxmlPath());
    }

    public void onDeleteProjectMenuButtonClicked() {
    	String title = "Warning!";
        String header = "This will delete the project named " + model.getName() + ". This action cannot be undone";
        String content = "Are you sure?";

        Optional<ButtonType> dialogResult = DialogCreator.showConfirmationDialog(title, header, content);
        if (dialogResult.isPresent() && dialogResult.get() == ButtonType.OK) {
            try {
                model.deleteData();
                // Go to welcome screen
                Main.setPrimaryScene(WelcomeController.getFxmlPath());
            } catch (DBException e) {
                logger.error("Unable to delete project from DB");
                DialogCreator.showErrorDialog("Unable to delete project from DB", e.getLocalizedMessage());
            }
        }
    }

    public void onUserFilterComboBoxClicked() {
        UserModel selectedUser = userFilterComboBox.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            ObservableList<ListableTaskModel> newTaskList;
            if (selectedUser.equals(UserModel.getEmptyUser())) {
                // "Empty" user selected, remove assignee filter
                newTaskList = FXCollections.observableList(tasksList);
            }
            else {
                newTaskList = FXCollections.observableList(tasksList)
                        .filtered(t -> selectedUser.equals(t.getModel().getAssignee()));
            }
            taskTableView.setItems(newTaskList);
        }
    }

    public void onAddUserMenuItemClicked() {
        UserCreatorController ucc = (UserCreatorController) Main.setPrimaryScene(UserCreatorController.getFxmlPath());
        ucc.setReturnProject(model);
    }
    
    public void showInfo() {
        String headerText = "SEP Version 0.1.";
		String contentText = "Team Members: \nJeremy Brown \nAli Zoghi \nCharles Tondreau-Alin \nNicola Polesana"
				+ "\nAndres Gonzales \nDemo Kioussis \nJustin Watley \nMark Chmilar \nVince Fugnitto"
				+ "\nMichael Deom";

        DialogCreator.showInfoDialog(headerText, contentText);

}
}