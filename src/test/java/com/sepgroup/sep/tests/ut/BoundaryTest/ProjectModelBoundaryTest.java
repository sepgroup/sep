package com.sepgroup.sep.tests.ut.BoundaryTest;

import com.sepgroup.sep.model.DBManager;
import com.sepgroup.sep.model.InvalidInputException;
import com.sepgroup.sep.model.ProjectModel;
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
public class ProjectModelBoundaryTest {

    private ProjectModel createdProject;
    private Date createdProjectStartDate = new Date(System.currentTimeMillis() - 5 * 9999*9999);
    private Date createdProjectDeadline = new Date(System.currentTimeMillis() + 5 * 9999*9999);
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
    }
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DBManager.dropAllDBTables();
    }
    @Test (expected = InvalidInputException.class)
    public void budgetWorstCaseUpper() throws Exception{
        double ActualBudget=10000000.1;
        createdProject.setBudget(ActualBudget);
    }
    @Test
    public void budgetWorstCaseUpperBoundary() throws Exception{
        double ActualBudget=10000000;
        createdProject.setBudget(ActualBudget);
        assertThat(ActualBudget, equalTo(createdProject.getBudget()));
    }
    @Test
    public void budgetWorstCaseMiddle() throws Exception{
        double ActualBudget=7500000;
        createdProject.setBudget(ActualBudget);
        assertThat(ActualBudget, equalTo(createdProject.getBudget()));
    }
    @Test (expected = InvalidInputException.class)
    public void budgetWorstCaseLower() throws Exception{
        double ActualBudget=-0.1;
        createdProject.setBudget(ActualBudget);
    }
    @Test
    public void budgetWorstCaseLowerBoundary() throws Exception{
        double ActualBudget=0;
        createdProject.setBudget(ActualBudget);
        assertThat(ActualBudget, equalTo(createdProject.getBudget()));
    }

}
