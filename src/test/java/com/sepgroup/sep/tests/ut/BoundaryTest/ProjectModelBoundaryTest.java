package com.sepgroup.sep.tests.ut.BoundaryTest;

import com.sepgroup.sep.model.DBManager;
import com.sepgroup.sep.model.InvalidInputException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
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
    @Test
    public void budgetAtCompletionBoundaryTestMaxMax() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(ProjectModel.MaxBudgetProject);
        createdProject.persistData();
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(TaskModel.MaxBudgetTast);
            t1.persistData();
        }
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(ProjectModel.MaxBudgetProject));
    }
    @Test
    public void budgetAtCompletionBoundaryTestMaxMaxMinusOne() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(ProjectModel.MaxBudgetProject);
        createdProject.persistData();
        double expectedResult=ProjectModel.MaxBudgetProject;
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget((TaskModel.MaxBudgetTast)-1);
            t1.persistData();
            expectedResult--;
        }
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(expectedResult));
    }
    @Test
    public void budgetAtCompletionBoundaryTestMaxMin() throws Exception{
        double minBudgetForTask=0;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(ProjectModel.MaxBudgetProject);
        createdProject.persistData();
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(minBudgetForTask);
            t1.persistData();
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(minBudgetForTask));
    }
    @Test
    public void budgetAtCompletionBoundaryTestMaxMinPlusOne() throws Exception{
        double minPulsOneBudgetForTask=1;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(ProjectModel.MaxBudgetProject);
        createdProject.persistData();
        TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
        t1.setBudget(minPulsOneBudgetForTask);
        t1.persistData();
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(minPulsOneBudgetForTask));
    }
    @Test
    public void budgetAtCompletionBoundaryTestMaxMed() throws Exception{
        double mediumBudgetForTask=5000;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(ProjectModel.MaxBudgetProject);
        createdProject.persistData();
        double expectedResult=0;
        for(int i=0; i<10; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(mediumBudgetForTask);
            t1.persistData();
            expectedResult+=mediumBudgetForTask;
        }
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(mediumBudgetForTask));
    }

    @Test (expected = Exception.class)
    public void budgetAtCompletionBoundaryTestMaxMinusOneMax() throws Exception{
        double expectedResult=ProjectModel.MaxBudgetProject-1;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(expectedResult);
        createdProject.persistData();
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(TaskModel.MaxBudgetTast);
            t1.persistData();
        }
    }
    @Test
    public void budgetAtCompletionBoundaryTestMaxMinusOneMaxMinusOne() throws Exception{
        double expectedResultProject=ProjectModel.MaxBudgetProject-1;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(expectedResultProject);
        createdProject.persistData();
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(TaskModel.MaxBudgetTast-1);
            t1.persistData();
        }
        assertThat(createdProject.getBudgetAtCompletion()+99, equalTo(expectedResultProject));
    }


}
