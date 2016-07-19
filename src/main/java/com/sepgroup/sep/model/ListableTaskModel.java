package com.sepgroup.sep.model;

import com.sepgroup.sep.Observer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by jeremybrown on 6/6/16.
 */
public class ListableTaskModel implements Observer {

    private TaskModel model;

    private SimpleIntegerProperty taskId;
    private SimpleStringProperty taskName;
    private SimpleDoubleProperty taskBudget;
    private SimpleStringProperty startDate;
    private SimpleStringProperty deadline;
    private SimpleBooleanProperty completed;
    private SimpleStringProperty assignee;
    private SimpleIntegerProperty mostLikelyTime;
    private SimpleIntegerProperty pessimisticTime;
    private SimpleIntegerProperty optimisticTime;

    public ListableTaskModel(TaskModel m) {
        this.model = m;
        this.model.registerObserver(this);
        taskId = new SimpleIntegerProperty();
        taskName = new SimpleStringProperty();
        taskBudget = new SimpleDoubleProperty();
        startDate = new SimpleStringProperty();
        deadline = new SimpleStringProperty();
        completed = new SimpleBooleanProperty();
        assignee = new SimpleStringProperty();
        mostLikelyTime=new SimpleIntegerProperty();
        pessimisticTime=new SimpleIntegerProperty();
        optimisticTime=new SimpleIntegerProperty();
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

    public SimpleIntegerProperty taskMostLikelyProperty(){ return mostLikelyTime; }

    public SimpleIntegerProperty taskPessimisticTimeProperty(){ return pessimisticTime; }

    public SimpleIntegerProperty taskOptimisticTimeProperty(){ return optimisticTime; }

    public SimpleStringProperty assigneeProperty() {
        return assignee;
    }

    @Override
    public void update() {
        if (model != null) {
            taskId.set(model.getTaskId());
            taskName.set(model.getName());
            taskBudget.set(model.getBudget());
            startDate.set(model.getStartDateString());
            deadline.set(model.getDeadlineString());
            completed.set(model.isDone());
            assignee.set(model.getAssignee() != null ? model.getAssignee().getFullName() : "");
            mostLikelyTime.set(model.getMostLikelyTimeToFinish());
            pessimisticTime.set(model.getPesimisticTimeToFinish());
            optimisticTime.set(model.getOptimisticTimeToFinish());
        }
    }

    @Override
    public String toString() {
        return "[" + model.getTaskId() + "] " + model.getName();
    }
}
