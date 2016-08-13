package com.sepgroup.sep.tests.ut.BoundaryTest;
import com.sepgroup.sep.model.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Date;
/**
 * Created by Ali on 8/10/2016.
 */
public class TaskModelBoundaryTest {
    private ProjectModel createdProject;
    private Date createdProjectStartDate = new Date(System.currentTimeMillis() - 5 * 9999*9999);
    private Date createdProjectDeadline = new Date(System.currentTimeMillis() + 5 * 9999*9999);
    private static Date defaultStartDate = new Date();
    private static Date defaultDeadline = new Date(System.currentTimeMillis() + 2* 9999*9999);
    private UserModel createdUser;
    private TaskModel createdTask;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DBManager.createDBTablesIfNotExisting();
    }
    @Before
    public void setUp() throws Exception {
        createdProject = new ProjectModel();
        createdProject.setName("Project 1");
        createdProject.setBudget(100000);
        createdProject.setStartDate(createdProjectStartDate);
        createdProject.setDeadline(createdProjectDeadline);
        createdProject.persistData();
        createdUser = new UserModel("FIRST", "LAST", 22.00);
        createdUser.persistData();
        try {
            createdTask = new TaskModel("T1", "Description of T1", createdProject.getProjectId(), 10000,
                    defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        } catch (Exception e) {

        }
    }
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DBManager.dropAllDBTables();
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
