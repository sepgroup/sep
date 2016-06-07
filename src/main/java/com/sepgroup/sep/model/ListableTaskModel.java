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

    @Override
    public String toString() {
        return "[" + model.getTaskId() + "] " + model.getName();
    }
}
