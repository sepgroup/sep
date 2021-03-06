package com.sepgroup.sep.tests.boundary;

import com.sepgroup.sep.model.*;
import com.sepgroup.sep.tests.TestCommons;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Date;

/**
 * <p>
 *     Identifier: F1-1
 * </p>
 * <p>
 *     Created by Ali on 8/10/2016.
 * </p>
 */
public class SetBudgetTaskModelTest {

    private static Date defaultStartDate = new Date();
    private static Date defaultDeadline = new Date(System.currentTimeMillis() + 2* 9999*9999);

    private ProjectModel createdProject;
    private UserModel createdUser;
    private TaskModel createdTask;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DBManager.createDBTablesIfNotExisting();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DBManager.dropAllDBTables();
    }

    @Before
    public void setUp() throws Exception {
        createdProject = TestCommons.createMainProject();
        createdUser = TestCommons.createMainUser();
        try {
            createdTask = new TaskModel("T1", "Description of T1", createdProject.getProjectId(), 10000,
                    defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        } catch (Exception e) {

        }
    }

    @Test
    public void budgetWorstCaseLessThanUpperBoundary() throws Exception{
        double ActualBudget=99999;
        createdTask.setBudget(ActualBudget);
        assertThat(ActualBudget, equalTo(createdTask.getBudget()));
    }

    @Test
    public void budgetWorstCaseUpperBoundary() throws Exception{
        double ActualBudget=100000;
        createdTask.setBudget(ActualBudget);
        assertThat(ActualBudget, equalTo(createdTask.getBudget()));
    }

    @Test
    public void budgetWorstCaseMiddle() throws Exception{
        double ActualBudget=75000;
        createdTask.setBudget(ActualBudget);
        assertThat(ActualBudget, equalTo(createdTask.getBudget()));
    }

    @Test
    public void budgetWorstCaseGreaterThanLowerBoundary() throws Exception{
        double ActualBudget=1;
        createdTask.setBudget(ActualBudget);
        assertThat(ActualBudget, equalTo(createdTask.getBudget()));
    }

    @Test
    public void budgetWorstCaseLowerBoundary() throws Exception{
        double ActualBudget=0;
        createdTask.setBudget(ActualBudget);
        assertThat(ActualBudget, equalTo(createdTask.getBudget()));
    }
}
