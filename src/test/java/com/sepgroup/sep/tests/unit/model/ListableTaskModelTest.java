package com.sepgroup.sep.tests.unit.model;

import com.sepgroup.sep.model.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by jeremybrown on 2016-08-10.
 */
public class ListableTaskModelTest {

    private static TaskModel testTask;
    private static String testTaskName = "T1";
    private static String testTaskDescription = "Description of T1";
    private static double testTaskBudget = 5000;
    private static boolean testTaskCompleted = false;
    private static int testTaskMostLikely = 8;
    private static int testTaskOptimistic = 7;
    private static int testTaskPessimistic = 9;

    private static UserModel createdUser;

    private static Date createdProjectStartDate = new Date(System.currentTimeMillis() - 5 * 9999*9999);
    private static Date createdProjectDeadline = new Date(System.currentTimeMillis() + 5 * 9999*9999);
    private static Date defaultStartDate = new Date();
    private static Date defaultDeadline = new Date(System.currentTimeMillis() + 2* 9999*9999);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DBManager.createDBTablesIfNotExisting();

        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project 1");
        createdProject.setBudget(100000);
        createdProject.setStartDate(createdProjectStartDate);
        createdProject.setDeadline(createdProjectDeadline);
        createdProject.persistData();

        createdUser = new UserModel("FIRST", "LAST", 22.00);
        createdUser.persistData();

        testTask = new TaskModel(testTaskName, testTaskDescription, createdProject.getProjectId(), testTaskBudget,
                defaultStartDate, defaultDeadline, testTaskCompleted, createdUser, testTaskMostLikely, testTaskPessimistic,
                testTaskOptimistic, null, null);
        testTask.persistData();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DBManager.dropAllDBTables();
    }

    @Test
    public void testGetProperties() {
        ListableTaskModel t = new ListableTaskModel(testTask);

        assertThat(t.getModel(), equalTo(testTask));
        assertThat(t.taskIdProperty().get(), equalTo(testTask.getTaskId()));
        assertThat(t.taskNameProperty().get(), equalTo(testTaskName));
        assertThat(t.taskBudgetProperty().get(), equalTo(testTaskBudget));
        assertThat(t.startDateProperty().get(), equalTo(testTask.getStartDateString()));
        assertThat(t.deadlineProperty().get(), equalTo(testTask.getDeadlineString()));
        assertThat(t.completedProperty().get(), equalTo(testTaskCompleted));
        assertThat(t.taskMostLikelyProperty().get(), equalTo(testTaskMostLikely));
        assertThat(t.taskPessimisticTimeProperty().get(), equalTo(testTaskPessimistic));
        assertThat(t.taskOptimisticTimeProperty().get(), equalTo(testTaskOptimistic));
        assertThat(t.assigneeProperty().get(), equalTo(createdUser.getFullName()));
    }

    @Test
    public void testToString() {
        ListableTaskModel t = new ListableTaskModel(testTask);
        String expected = "[1] T1";
        String actual = t.toString();

        assertThat(expected, equalTo(actual));
    }
}
