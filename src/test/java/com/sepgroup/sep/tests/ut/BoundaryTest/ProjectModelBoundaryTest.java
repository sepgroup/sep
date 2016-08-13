package com.sepgroup.sep.tests.ut.BoundaryTest;

import com.sepgroup.sep.model.DBManager;
import com.sepgroup.sep.model.InvalidInputException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import jdk.nashorn.internal.ir.annotations.Ignore;
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
@org.junit.Ignore
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
        double minPlusOneBudgetForTask=1;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(ProjectModel.MaxBudgetProject);
        createdProject.persistData();
        TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
        t1.setBudget(minPlusOneBudgetForTask);
        t1.persistData();
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(minPlusOneBudgetForTask));
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
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(expectedResult));
    }

    @Test (expected = Exception.class)
    public void budgetAtCompletionBoundaryTestMaxMinusOneMax() throws Exception{
        double setBudgetForProject=ProjectModel.MaxBudgetProject-1;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(TaskModel.MaxBudgetTast);
            t1.persistData();
        }
    }
    @Test
    public void budgetAtCompletionBoundaryTestMaxMinusOneMaxMinusOne() throws Exception{
        double setBudgetForProject=ProjectModel.MaxBudgetProject-1;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        double sumBudgetForTask=0;
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(TaskModel.MaxBudgetTast-1);
            t1.persistData();
            sumBudgetForTask+=TaskModel.MaxBudgetTast-1;
        }
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(sumBudgetForTask));
    }

    @Test
    public void budgetAtCompletionBoundaryTestMaxMinusOneMin() throws Exception{
        double setBudgetForProject=ProjectModel.MaxBudgetProject-1;
        double minBudgetForTask=0;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
        t1.setBudget(minBudgetForTask);
        t1.persistData();
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(minBudgetForTask));
    }

    @Test
    public void budgetAtCompletionBoundaryTestMaxMinusOneMinPlusOne() throws Exception{
        double setBudgetForProject=ProjectModel.MaxBudgetProject-1;
        double minPlusOneBudgetForTask=5000;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
        t1.setBudget(minPlusOneBudgetForTask);
        t1.persistData();
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(minPlusOneBudgetForTask));
    }

    @Test
    public void budgetAtCompletionBoundaryTestMaxMinusOneMed() throws Exception{
        double setBudgetForProject=ProjectModel.MaxBudgetProject-1;
        double mediumBudgetForTask=5000;
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        double expectedResult=0;
        for(int i=0; i<10; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(mediumBudgetForTask);
            t1.persistData();
            expectedResult+=mediumBudgetForTask;
        }
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(expectedResult));
    }

    @Test (expected = Exception.class)
    public void budgetAtCompletionBoundaryTestMedMax() throws Exception{
        double setBudgetForProject=7500000;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(TaskModel.MaxBudgetTast);
            t1.persistData();
        }
    }
    @Test (expected = Exception.class)
    public void budgetAtCompletionBoundaryTestMedMaxMinusOne() throws Exception{
        double setBudgetForProject=7500000;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        double sumBudgetForTask=0;
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(TaskModel.MaxBudgetTast-1);
            t1.persistData();
            sumBudgetForTask+=TaskModel.MaxBudgetTast-1;
        }
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(sumBudgetForTask));
    }

    @Test
    public void budgetAtCompletionBoundaryTestMedMin() throws Exception{
        double setBudgetForProject=7500000;
        double minBudgetForTask=0;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
        t1.setBudget(minBudgetForTask);
        t1.persistData();
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(minBudgetForTask));
    }

    @Test
    public void budgetAtCompletionBoundaryTestMedMinPlusOne() throws Exception{
        double setBudgetForProject=7500000;
        double minPlusOneBudgetForTask=1;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
        t1.setBudget(minPlusOneBudgetForTask);
        t1.persistData();
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(minPlusOneBudgetForTask));
    }

    @Test
    public void budgetAtCompletionBoundaryTestMedMed() throws Exception{
        double setBudgetForProject=7500000;
        double mediumBudgetForTask=5000;
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        double expectedResult=0;
        for(int i=0; i<10; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(mediumBudgetForTask);
            t1.persistData();
            expectedResult+=mediumBudgetForTask;
        }
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(expectedResult));
    }

    @Test (expected = Exception.class)
    public void budgetAtCompletionBoundaryTestMinMax() throws Exception{
        double setBudgetForProject=0;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(TaskModel.MaxBudgetTast);
            t1.persistData();
        }
    }
    @Test (expected = Exception.class)
    public void budgetAtCompletionBoundaryMinMaxMinusOne() throws Exception{
        double setBudgetForProject=0;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        double sumBudgetForTask=0;
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(TaskModel.MaxBudgetTast-1);
            t1.persistData();
            sumBudgetForTask+=TaskModel.MaxBudgetTast-1;
        }
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(sumBudgetForTask));
    }

    @Test
    public void budgetAtCompletionBoundaryTestMinMin() throws Exception{
        double setBudgetForProject=0;
        double minBudgetForTask=0;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
        t1.setBudget(minBudgetForTask);
        t1.persistData();
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(minBudgetForTask));
    }

    @Test(expected = Exception.class)
    public void budgetAtCompletionBoundaryTestMinMinPlusOne() throws Exception{
        double setBudgetForProject=0;
        double minPlusOneBudgetForTask=1;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
        t1.setBudget(minPlusOneBudgetForTask);
        t1.persistData();
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(minPlusOneBudgetForTask));
    }

    @Test(expected = Exception.class)
    public void budgetAtCompletionBoundaryTestMinMed() throws Exception{
        double setBudgetForProject=0;
        double mediumBudgetForTask=5000;
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        double expectedResult=0;
        for(int i=0; i<10; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(mediumBudgetForTask);
            t1.persistData();
            expectedResult+=mediumBudgetForTask;
        }
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(expectedResult));
    }
    @Test (expected = Exception.class)
    public void budgetAtCompletionBoundaryTestMinPlusOneMax() throws Exception{
        double setBudgetForProject=1;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(TaskModel.MaxBudgetTast);
            t1.persistData();
        }
    }
    @Test (expected = Exception.class)
    public void budgetAtCompletionBoundaryMinPlusOneMaxMinusOne() throws Exception{
        double setBudgetForProject=1;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        double sumBudgetForTask=0;
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(TaskModel.MaxBudgetTast-1);
            t1.persistData();
            sumBudgetForTask+=TaskModel.MaxBudgetTast-1;
        }
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(sumBudgetForTask));
    }

    @Test
    public void budgetAtCompletionBoundaryTestMinPlusOneMin() throws Exception{
        double setBudgetForProject=1;
        double minBudgetForTask=0;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
        t1.setBudget(minBudgetForTask);
        t1.persistData();
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(minBudgetForTask));
    }

    @Test
    public void budgetAtCompletionBoundaryTestMinPlusOneMinPlusOne() throws Exception{
        double setBudgetForProject=1;
        double minPlusOneBudgetForTask=1;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
        t1.setBudget(minPlusOneBudgetForTask);
        t1.persistData();
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(minPlusOneBudgetForTask));
    }

    @Test(expected = Exception.class)
    public void budgetAtCompletionBoundaryTestMinPlusOneMed() throws Exception{
        double setBudgetForProject=1;
        double mediumBudgetForTask=5000;
        createdProject.setName("Project Y");
        createdProject.setBudget(setBudgetForProject);
        createdProject.persistData();
        double expectedResult=0;
        for(int i=0; i<10; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(mediumBudgetForTask);
            t1.persistData();
            expectedResult+=mediumBudgetForTask;
        }
        assertThat(createdProject.getBudgetAtCompletion(), equalTo(expectedResult));
    }

}
