package com.sepgroup.sep.tests.boundary;

import com.sepgroup.sep.model.DBManager;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.tests.TestCommons;
import org.junit.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <p>
 *     Identifier: F3-1
 * </p>
 * <p>
 *     Created by Ali on 8/10/2016.
 * </p>
 */
@Ignore
public class GetBudgetAtCompletionTest {

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
    public void budgetAtCompletionBoundaryTestMaxMax() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(ProjectModel.MaxBudgetProject);
        createdProject.persistData();
        for(int i=0; i<100; i++){
            TaskModel t1=new TaskModel("test", "description of test",createdProject.getProjectId());
            t1.setBudget(TaskModel.MaxBudgetTask);
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
            t1.setBudget((TaskModel.MaxBudgetTask)-1);
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
            t1.setBudget(TaskModel.MaxBudgetTask);
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
            t1.setBudget(TaskModel.MaxBudgetTask -1);
            t1.persistData();
            sumBudgetForTask+=TaskModel.MaxBudgetTask -1;
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
            t1.setBudget(TaskModel.MaxBudgetTask);
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
            t1.setBudget(TaskModel.MaxBudgetTask -1);
            t1.persistData();
            sumBudgetForTask+=TaskModel.MaxBudgetTask -1;
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
            t1.setBudget(TaskModel.MaxBudgetTask);
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
            t1.setBudget(TaskModel.MaxBudgetTask -1);
            t1.persistData();
            sumBudgetForTask+=TaskModel.MaxBudgetTask -1;
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
            t1.setBudget(TaskModel.MaxBudgetTask);
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
            t1.setBudget(TaskModel.MaxBudgetTask -1);
            t1.persistData();
            sumBudgetForTask+=TaskModel.MaxBudgetTask -1;
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
