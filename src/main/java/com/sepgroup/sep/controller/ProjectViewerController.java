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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private ProjectModel model;
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
    public ComboBox<UserModel> userFilterComboBox;

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

            // Populate manager
            String managerName;
            int managerUserID = model.getManagerUserId();
            if (managerUserID == 0) {
                managerName = "[ none ]";
            } else {
                try {
                    UserModel manager = UserModel.getById(managerUserID);
                    managerName = manager.getFirstName() + " " + manager.getLastName();
                } catch (ModelNotFoundException e) {
                    logger.error("Error finding user with ID " + managerUserID);
                    managerName = "[ none ]";
                }
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
