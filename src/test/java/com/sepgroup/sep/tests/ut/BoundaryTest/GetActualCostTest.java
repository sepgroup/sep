package com.sepgroup.sep.tests.ut.BoundaryTest;

import com.sepgroup.sep.model.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test getActualCose() from TaskModel.java.
 * Two (2) input variables, 5^2 = 25 test cases.
 * <p>
 * 4 tests for basis path coverage.
 * Created by jeremybrown on 2016-08-12.
 */
@RunWith(Parameterized.class)
public class GetActualCostTest {

    /**
     * Min value: 0
     * Med value: 500
     * Max value: 1000000
     */
    @Parameter(value = 0)
    public double assigneeSalaryPerHour;

    /**
     * Min value: 0
     * Med value: 25
     * Max value: 5000
     */
    @Parameter(value = 1)
    public int actualDurationInDays;

    @Parameter(value = 2)
    public double expectedActualCost;

    private ProjectModel createdProject;
    private static Date defaultStartDate = new Date();
    private static Date defaultDeadline = new Date(System.currentTimeMillis() + 2 * 9999 * 9999);
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
        createdProject.setStartDate(new Date(System.currentTimeMillis() - 5 * 9999 * 9999));
        createdProject.setDeadline(new Date(System.currentTimeMillis() + 5 * 9999 * 9999));
        createdProject.persistData();
        createdUser = new UserModel("User", "#1", 22.00);
        createdUser.persistData();
        createdTask = new TaskModel("T1", "Description of T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
    }

    @Parameters(name = "{index}: testActualCost({0} salary & {1} days) = {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 0, 0},
                {0, 1, 0},
                {0, 25, 0},
                {0, 4999, 0},
                {0, 5000, 0},
                {1, 0, 0},
                {1, 1, 8},
                {1, 25, 200},
                {1, 4999, 39992},
                {1, 5000, 40000},
                {500, 0, 0},
                {500, 1, 4000},
                {500, 25, 100000},
                {500, 4999, 19996000},
                {500, 5000, 20000000},
                {999999, 0, 0},
                {999999, 1, 7999992},
                {999999, 25, 199999800},
                {999999, 4999, 39991960008L},
                {999999, 5000, 39999960000L},
                {1000000, 0, 0},
                {1000000, 1, 8000000},
                {1000000, 25, 200000000},
                {1000000, 4999, 39992000000L},
                {1000000, 5000, 40000000000L}
        });
    }

    /**
     * Minimum salaryPerHour, minimum actualDuringInDays.
     */
    @Test
    public void testGetActualCost() throws InvalidInputException {
        createdUser.setSalaryPerHour(assigneeSalaryPerHour);
        createdTask.setActualStartDate(defaultStartDate);
        long extraMillis = TimeUnit.MILLISECONDS.convert(actualDurationInDays, TimeUnit.DAYS);
        Date actualEndDate = new Date(defaultStartDate.getTime() + extraMillis);
        createdTask.setActualEndDate(actualEndDate);

        assertThat(createdTask.getActualCost(), equalTo(expectedActualCost));
    }
}