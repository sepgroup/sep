package com.sepgroup.sep.tests.boundary;

import com.sepgroup.sep.model.DBManager;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.tests.TestCommons;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <p>
 *     Identifier: F2-1
 * </p>
 * <p>
 *
 * </p>
 * <p>
 *     Created by Ali on 8/10/2016.
 * </p>
 */

public class SetBudgetProjectModelTest {

    private ProjectModel createdProject;

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
    }

    @Test
    public void budgetWorstCaseLessThanUpperBoundary() throws Exception{
        double ActualBudget=9999999;
        createdProject.setBudget(ActualBudget);
        assertThat(ActualBudget, equalTo(createdProject.getBudget()));
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

    @Test
    public void budgetWorstCaseGreaterThanLowerBoundary() throws Exception{
        double ActualBudget=1;
        createdProject.setBudget(ActualBudget);
        assertThat(ActualBudget, equalTo(createdProject.getBudget()));
    }

    @Test
    public void budgetWorstCaseLowerBoundary() throws Exception{
        double ActualBudget=0;
        createdProject.setBudget(ActualBudget);
        assertThat(ActualBudget, equalTo(createdProject.getBudget()));
    }
}
