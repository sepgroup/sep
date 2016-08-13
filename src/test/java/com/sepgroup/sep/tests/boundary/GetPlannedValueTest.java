package com.sepgroup.sep.tests.boundary;

import com.sepgroup.sep.model.*;
import org.junit.AfterClass;
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test getPlannedValue() from TaskModel.java.
 * One (1) input variables, 5^1 = 5 test cases.
 * <p>
 * 4 tests for basis path coverage.
 * Created by jeremybrown on 2016-08-13.
 */
@RunWith(Parameterized.class)
public class GetPlannedValueTest {

    @Parameter(value = 0)
    public Date deadline;

    @Parameter(value = 1)
    public double expectedPlannedValue;

    private TaskModel createdTask;
    private static Date defaultStartDate = new Date(0);
    private static double taskBudget = 6003.43;

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
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project 1");
        createdProject.setBudget(100000);
        createdProject.setStartDate(defaultStartDate);
        createdProject.setDeadline(new Date(16756752729000L));
        createdProject.persistData();

        createdTask = new TaskModel("T1", "Description of T1", createdProject.getProjectId(), taskBudget,
                defaultStartDate, null, false, null, 8, 9, 7, defaultStartDate, null);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new Date(0L), taskBudget}, // "1970-01-01"
                {new Date(163929000L), taskBudget}, // "1970-01-02"
                {new Date(1473539529000L), 0.0}, // "2016-09-10"
                {new Date(10445232729000L), 0.0}, // "2300-12-30"
                {new Date(10445319129000L), 0.0}, // "2300-12-31"
        });
    }

    @Test
    public void testGetPlannedValue() throws Exception {
        createdTask.setDeadline(deadline);
        assertThat(createdTask.getPlannedValue(), equalTo(expectedPlannedValue));
    }
}
