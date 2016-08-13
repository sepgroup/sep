package com.sepgroup.sep.tests.unit.model;

import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.*;
import com.sepgroup.sep.utils.DateUtils;
import org.junit.*;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * Edited by Justin Whatley on 2016-07-17
 * Created by jeremybrown on 2016-05-22.
 */
public class ProjectModelTest {

    // Current date/time for dummy start date
    private static Date defaultStartDate = new Date();

    // At least one day past defaultStartDate
    private static Date defaultDeadline = new Date(System.currentTimeMillis() + 9999*9999);

    private static UserModel createdManager;
    private static UserModel createdManager2;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DBManager.createDBTablesIfNotExisting();
        try {
            createdManager = new UserModel("Manager", "One", 22.00);
            createdManager.persistData();

            createdManager2 = new UserModel("Manager", "Two", 27.00);
            createdManager2.persistData();
        }
        catch (Exception e){
            System.out.println("There was an error generating UserModel objects for the ProjectModel tests");
            throw e;
        }
    }

    @Before
    public void setUp() throws Exception {

    }

    private TaskModel generateSimpleTestTask(int projectId) {
        TaskModel t1 = null;
        try {
            t1 = new TaskModel("T1", "Description of T1",projectId);
        } catch (Exception e) {

        }
        return t1;
    }

    /**
     * Clears any projects stored to the database during a testcase
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        ProjectModel.cleanData();
    }

    /**
     * Removes and leftover database data
     * @throws Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DBManager.dropAllDBTables();
    }


    //Integration Tests
    /**
     * Helper function to generate a project
     * TODO make this an integration test
     */
    private ProjectModel generateDummyProject(String name, int budget, Date startDate, Date deadline, boolean done, String description, UserModel manager) throws Exception {

        ProjectModel createdProject = new ProjectModel();
        try {
            //set variables
            createdProject.setName(name);
            createdProject.setBudget(budget);
            createdProject.setStartDate(startDate);
            createdProject.setDeadline(deadline);
            createdProject.setDone(done);
            createdProject.setProjectDescription(description);
            createdProject.setManager(manager);

            //store project
            createdProject.persistData();

            return createdProject;
        }
        catch (Exception e)
        {
            System.out.println("An error generating a dummy project: ");
            System.out.println(e);
            throw e;
        }
    }

    //Unit-testing

    /**
     * Positive test to ensure that Persist Data function saves project to database
     */
    @Test
    public void testPersistData() throws Exception {

        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project X");
        createdProject.persistData();
        int pId = createdProject.getProjectId();

        ProjectModel fetchedProject = null;
        try {
            fetchedProject = ProjectModel.getById(pId);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }
        assertThat(fetchedProject, equalTo(createdProject));
    }
    /**
     * Positive test to ensure that Persist Data function update when the id is exists
     */
    @Test
    public void testPersistDataUpdate() throws Exception {
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project X");
        createdProject.persistData();
        int pIdBeforeUpdate = createdProject.getProjectId();
        String updatedName="Project Y";
        createdProject.setName(updatedName);
        createdProject.persistData();
        int pIdAfterUpdate=createdProject.getProjectId();
        ProjectModel fetchedProjectBefore = null;
        ProjectModel fetchedProjectAfter = null;
        try {
            fetchedProjectBefore = ProjectModel.getById(pIdBeforeUpdate);
            fetchedProjectAfter = ProjectModel.getById(pIdAfterUpdate);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }
        assertThat(fetchedProjectBefore, equalTo(fetchedProjectAfter));
    }
    /**
     * Negative test to ensure that Persist Data function will not work when at least a name is not specified
     * TODO catch the exception message or associate it to exception
     */
    @Test(expected = DBException.class)
    public void testPersistDataException() throws Exception {

        ProjectModel createdProject = new ProjectModel();
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        ProjectModel.getById(pId);
    }

    /**
     * Positive test to ensure that RefreshData gets the current project in db
     */
    @Test
    public void testRefreshData() throws Exception {

        // Create project
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.persistData();
        int pId = createdProject.getProjectId();

        // Fetch project
        ProjectModel fetchedProject = null;
        try {
            fetchedProject = ProjectModel.getById(pId);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }

        // Modify & persist project
        fetchedProject.setBudget(10000.0);
        fetchedProject.persistData();

        // Refresh first project instance
        createdProject.refreshData();

        assertThat(createdProject, equalTo(fetchedProject));

    }

    /**
     * the sum of tasks budget would be the same as the one which is calculated in project
     * @throws Exception
     */
    @Test
    public void testGetBudgetAtCompletion() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setBudget(400);
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setBudget(600);
        ts2.persistData();
        double ActualBudget=ts1.getBudget()+ts2.getBudget();
        double ExpectedBudget= createdProject.getBudgetAtCompletion();
        assertThat(ExpectedBudget, equalTo(ActualBudget));
    }

    /**
     * the sum of completed tasks budget would be the same as the one which is calculated in project
     * @throws Exception
     */
    @Test
    public void testGetEarnedValue() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setBudget(400);
        ts1.setDone(false);
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setBudget(600);
        ts2.setDone(true);
        ts2.persistData();
        TaskModel ts3=generateSimpleTestTask(pId);
        ts3.setBudget(500);
        ts3.setDone(true);
        ts3.persistData();
        double ActualBudget=ts2.getBudget()+ts3.getBudget();
        double ExpectedBudget= createdProject.getEarnedValue();
        assertThat(ExpectedBudget, equalTo(ActualBudget));
    }

    @Test
    public void testGetPercentScheduledCompletion() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.setStartDate(new Date(System.currentTimeMillis() - 6 * 9999*9999));
        createdProject.setDeadline(new Date(System.currentTimeMillis() + 5 * 9999*9999));
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setBudget(400);
        ts1.setStartDate(new Date(System.currentTimeMillis() - 4 * 9999*9999));
        ts1.setDeadline(new Date(System.currentTimeMillis() - 2 * 9999*9999));
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setStartDate(new Date(System.currentTimeMillis() - 4 * 9999*9999));
        ts2.setDeadline(new Date(System.currentTimeMillis() - 2 * 9999*9999));
        ts2.setBudget(600);
        ts2.persistData();
        double Actual=(createdProject.getPlannedValue()/createdProject.getBudgetAtCompletion())*100.0;
        double Expected= createdProject.getPercentScheduledCompletion();
        assertThat(Expected, equalTo(Actual));
    }
    @Test
    public void testGetPercentComplete()throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.setStartDate(new Date(System.currentTimeMillis() - 6 * 9999*9999));
        createdProject.setDeadline(new Date(System.currentTimeMillis() + 5 * 9999*9999));
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setBudget(400);
        ts1.setDone(true);
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setBudget(600);
        ts2.setDone(true);
        ts2.persistData();
        double Actual=(createdProject.getEarnedValue()/createdProject.getBudgetAtCompletion())*100.0;
        double Expected= createdProject.getPercentComplete();
        assertThat(Expected, equalTo(Actual));
    }
    @Test
    public void testGetCostVariance() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.setStartDate(new Date(System.currentTimeMillis() - 6 * 9999*9999));
        createdProject.setDeadline(new Date(System.currentTimeMillis() + 5 * 9999*9999));
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setBudget(400);
        ts1.setDone(false);
        ts1.persistData();
        UserModel us1=new UserModel("us1", "test1", 15);
        us1.persistData();
        ts1.setAssignee(us1.getUserId());
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setBudget(600);
        ts2.setDone(true);
        ts2.setActualStartDate(new Date(System.currentTimeMillis() - 3 * 9999*9999));
        ts2.persistData();
        UserModel us2=new UserModel("us2", "test2", 10);
        us2.persistData();
        ts2.setAssignee(us2.getUserId());
        ts2.persistData();
        double Expected=(createdProject.getEarnedValue()-createdProject.getActualCost());
        double Actual= createdProject.getCostVariance();
        assertThat(Expected, equalTo(Actual));
    }
    @Test
    public void testGetScheduleVariance() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.setStartDate(new Date(System.currentTimeMillis() - 6 * 9999*9999));
        createdProject.setDeadline(new Date(System.currentTimeMillis() + 5 * 9999*9999));
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setBudget(400);
        ts1.setActualStartDate(new Date(System.currentTimeMillis() - 5 * 9999*9999));
        ts1.setDone(true);
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setStartDate(new Date(System.currentTimeMillis() - 5 * 9999*9999));
        ts2.setDeadline(new Date(System.currentTimeMillis() - 3 * 9999*9999));
        ts2.setBudget(600);
        ts2.setDone(false);
        ts2.persistData();
        double Expected=(createdProject.getEarnedValue()-createdProject.getPlannedValue());
        double Actual= createdProject.getScheduleVariance();
        assertThat(Expected, equalTo(Actual));
    }
    @Test
    public void testGetCostPerformanceIndex() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.setStartDate(new Date(System.currentTimeMillis() - 6 * 9999*9999));
        createdProject.setDeadline(new Date(System.currentTimeMillis() + 5 * 9999*9999));
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setBudget(400);
        ts1.setDone(false);
        ts1.persistData();
        UserModel us1=new UserModel("us1", "test1", 15);
        us1.persistData();
        ts1.setAssignee(us1.getUserId());
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setBudget(600);
        ts2.setDone(true);
        ts2.setActualStartDate(new Date(System.currentTimeMillis() - 3 * 9999*9999));
        ts2.persistData();
        UserModel us2=new UserModel("us2", "test2", 10);
        us2.persistData();
        ts2.setAssignee(us2.getUserId());
        ts2.persistData();
        double Expected=(createdProject.getEarnedValue()/createdProject.getActualCost());
        double Actual= createdProject.getCostPerformanceIndex();
        assertThat(Expected, equalTo(Actual));
    }
    @Test
    public void testGetSchedulePerformanceIndex() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.setStartDate(new Date(System.currentTimeMillis() - 6 * 9999*9999));
        createdProject.setDeadline(new Date(System.currentTimeMillis() + 5 * 9999*9999));
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setBudget(400);
        ts1.setActualStartDate(new Date(System.currentTimeMillis() - 5 * 9999*9999));
        ts1.setDone(true);
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setStartDate(new Date(System.currentTimeMillis() - 5 * 9999*9999));
        ts2.setDeadline(new Date(System.currentTimeMillis() - 3 * 9999*9999));
        ts2.setBudget(600);
        ts2.setDone(false);
        ts2.persistData();
        double Expected=(createdProject.getEarnedValue()/createdProject.getPlannedValue());
        double Actual= createdProject.getSchedulePerformanceIndex();
        assertThat(Expected, equalTo(Actual));
    }
    @Test
    public void testGetEstimateAtCompletion() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.setStartDate(new Date(System.currentTimeMillis() - 6 * 9999*9999));
        createdProject.setDeadline(new Date(System.currentTimeMillis() + 5 * 9999*9999));
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setBudget(400);
        ts1.setDone(false);
        ts1.persistData();
        UserModel us1=new UserModel("us1", "test1", 15);
        us1.persistData();
        ts1.setAssignee(us1.getUserId());
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setBudget(600);
        ts2.setDone(true);
        ts2.setActualStartDate(new Date(System.currentTimeMillis() - 3 * 9999*9999));
        ts2.persistData();
        UserModel us2=new UserModel("us2", "test2", 10);
        us2.persistData();
        ts2.setAssignee(us2.getUserId());
        ts2.persistData();
        double Expected=(createdProject.getBudgetAtCompletion()/createdProject.getCostPerformanceIndex());
        double Actual= createdProject.getEstimateAtCompletion();
        assertThat(Expected, equalTo(Actual));
    }
    @Test
    public void testGetEstimateToComplete() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.setStartDate(new Date(System.currentTimeMillis() - 6 * 9999*9999));
        createdProject.setDeadline(new Date(System.currentTimeMillis() + 5 * 9999*9999));
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setBudget(400);
        ts1.setDone(false);
        ts1.persistData();
        UserModel us1=new UserModel("us1", "test1", 15);
        us1.persistData();
        ts1.setAssignee(us1.getUserId());
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setBudget(600);
        ts2.setDone(true);
        ts2.setActualStartDate(new Date(System.currentTimeMillis() - 3 * 9999*9999));
        ts2.persistData();
        UserModel us2=new UserModel("us2", "test2", 10);
        us2.persistData();
        ts2.setAssignee(us2.getUserId());
        ts2.persistData();
        double Expected=(createdProject.getEstimateAtCompletion()-createdProject.getActualCost());
        double Actual= createdProject.getEstimateToComplete();
        assertThat(Expected, equalTo(Actual));
    }
    /**
     * sum all actual cost of task (actual cost related to the salary of user and hours of work on the task
     * @throws Exception
     */
    @Test
    public void testGetActualCost() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.setStartDate(new Date(System.currentTimeMillis() - 6 * 9999*9999));
        createdProject.setDeadline(new Date(System.currentTimeMillis() + 5 * 9999*9999));
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setBudget(400);
        ts1.setActualStartDate(new Date(System.currentTimeMillis() - 5 * 9999*9999));
        ts1.setActualEndDate(new Date(System.currentTimeMillis() - 3 * 9999*9999));
        ts1.setDone(true);
        UserModel us1=new UserModel("us1", "test", 20);
        us1.persistData();
        ts1.setAssignee(us1.getUserId());
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setBudget(600);
        ts2.setActualStartDate(new Date(System.currentTimeMillis() - 4 * 9999*9999));
        ts2.setActualEndDate(new Date(System.currentTimeMillis() - 2 * 9999*9999));
        ts2.setDone(true);
        UserModel us2=new UserModel("us2", "test", 15);
        us2.persistData();
        ts2.setAssignee(us2.getUserId());
        ts2.persistData();
        double Actual=(ts1.getActualCost()+ts2.getActualCost());
        double Expected= createdProject.getActualCost();
        assertThat(Expected, equalTo(Actual));
    }
    /**
     * the sum of ShouldbeDone tasks budget would be the same as the one which is calculated in project
     * @throws Exception
     */
    @Test
    public void testGetPlannedValueFirst() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.setStartDate(new Date(System.currentTimeMillis() - 6 * 9999*9999));
        createdProject.setDeadline(new Date(System.currentTimeMillis() + 5 * 9999*9999));
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setStartDate(new Date(System.currentTimeMillis() - 4 * 9999*9999));
        ts1.setDeadline(new Date(System.currentTimeMillis() - 1 * 9999*9999));
        ts1.setBudget(400);
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setStartDate(new Date(System.currentTimeMillis() - 4 * 9999*9999));
        ts2.setDeadline(new Date(System.currentTimeMillis() - 1 * 9999*9999));
        ts2.setBudget(200);
        ts2.persistData();
        double ActualBudget=ts1.getBudget()+ts2.getBudget();
        double ExpectedBudget= createdProject.getPlannedValue();
        assertThat(ExpectedBudget, equalTo(ActualBudget));
    }

    /**
     * the sum of ShuldbeDone tasks budget would be the same as the one which is calculated in project
     * @throws Exception
     */
    @Test
    public void testGetPlannedValueSecond() throws Exception{
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.setBudget(2000);
        createdProject.setStartDate(new Date(System.currentTimeMillis() - 6 * 9999*9999));
        createdProject.setDeadline(new Date(System.currentTimeMillis() + 11 * 9999*9999));
        createdProject.persistData();
        int pId = createdProject.getProjectId();
        TaskModel ts1=generateSimpleTestTask(pId);
        ts1.setStartDate(new Date(System.currentTimeMillis() - 4 * 9999*9999));
        ts1.setDeadline(new Date(System.currentTimeMillis() - 1 * 9999*9999));
        ts1.setBudget(400);
        ts1.persistData();
        TaskModel ts2=generateSimpleTestTask(pId);
        ts2.setStartDate(new Date(System.currentTimeMillis() - 4 * 9999*9999));
        ts2.setDeadline(new Date(System.currentTimeMillis() + 10 * 9999*9999));
        ts2.setBudget(200);
        ts2.persistData();
        double ActualBudget=ts1.getBudget();
        double ExpectedBudget= createdProject.getPlannedValue();
        assertThat(ExpectedBudget, equalTo(ActualBudget));
    }

    /**
     * Negative test to ensure that createdProject is not changed until RefreshData is called
     */
    @Test
    public void testWithoutRefreshData() throws Exception {

        // Create project
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.persistData();
        int pId = createdProject.getProjectId();

        // Fetch project
        ProjectModel fetchedProject = null;
        try {
            fetchedProject = ProjectModel.getById(pId);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }

        // Modify & persist project
        fetchedProject.setBudget(10000.0); //differentiated
        fetchedProject.setName("New name"); //differentiated

        //TODO look into why certain values do not have to be refreshed using the function
//        fetchedProject.setDone(true); //not differentiated
//        fetchedProject.setProjectDescription("Some description"); //not differentiated
//        fetchedProject.setStartDate(defaultDeadline); //not differentiated
//        fetchedProject.setDeadline(defaultDeadline); //causes constraint violation exception

        fetchedProject.persistData();

        // Refresh first project instance
        //createdProject.refreshData();

        //The object characteristics must not be equal because createdProject.refreshData() has not been called
        assertThat("Database values were changed without refreshData() being called", createdProject, not(equalTo(fetchedProject)));

    }

    /**
     * Positive test to ensure that deleteData removes the item from the database
     */
    @Test(expected = ModelNotFoundException.class)
    public void testDeleteData() throws Exception {

        // Create project
        ProjectModel p = new ProjectModel();
        p.setName("Project Y");
        p.persistData();
        int pId = p.getProjectId();

        // Delete project
        p.deleteData();

        // Attempt to fetch project
        ProjectModel.getById(pId);
    }

    /**
     * Positive test to ensure that getById returns the correct ProjectModel object
     */
    @Test
    public void testGetById() throws Exception {

        // Create project
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Z");
        createdProject.persistData();
        int pId = createdProject.getProjectId();

        ProjectModel fetchedProject = ProjectModel.getById(pId);

        assertThat(fetchedProject, equalTo(createdProject));

    }

    /**
     * Tests that getAll is returning an iterable list of ProjectModel objects as it is supposed to
     */
    @Test
    public void testGetAllBasic() throws Exception {
        // Create project
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project D");
        createdProject.persistData();
        List<ProjectModel> projectList = ProjectModel.getAll();

//        System.out.println(projectList.size());
        assertThat(projectList.size(), isA(Integer.class));
        assertEquals(1, projectList.size());
    }

    /**
     * Tests that getAll is returning an iterable list of ProjectModel objects as it is supposed to
     * TODO add more asserts to ensure that the objects were created
     */
    @Test
    public void testGetAllIntegration() throws Exception {
        generateDummyProject("Project A", 1000, defaultStartDate, defaultDeadline, false, "Some project description for A", createdManager);
        generateDummyProject("Project B", 2000, defaultStartDate, defaultDeadline, false, "Some project description for B", createdManager2);

        List<ProjectModel> projectList = ProjectModel.getAll();


        //Tests that there are exactly two projects in the list
        assertThat(projectList.size(), isA(Integer.class));
        assertEquals(2, projectList.size());

        //TODO test that other project variables can be accessed
    }

    /**
     * Tests that getAllByManager fails when no projects are assigned to the manager in question
     */
    @Test
    public void testGetAllByManager() throws Exception {

        // Create projects and assign managers to them
        ProjectModel createdProject1 = new ProjectModel();
        createdProject1.setName("Project A");
        createdProject1.setManager(createdManager);
        createdProject1.persistData();

        ProjectModel createdProject2 = new ProjectModel();
        createdProject2.setName("Project B");
        createdProject2.setManager(createdManager);
        createdProject2.persistData();

        ProjectModel createdProject3 = new ProjectModel();
        createdProject3.setName("Project C");
        createdProject3.setManager(createdManager2);
        createdProject3.persistData();

        //get projectList associated with manager1
        List<ProjectModel> projectList = ProjectModel.getAllByManager(createdManager);
        assertEquals(2, projectList.size());


        //get projectList associated with manager2
        List<ProjectModel> projectList2 = ProjectModel.getAllByManager(createdManager2);
        assertEquals(1, projectList2.size());

    }

    /**
     * Tests that projects are being cleared properly
     * @throws Exception
     */
    @Test(expected = ModelNotFoundException.class)
    public void testCleanData() throws Exception {
        ProjectModel createdProject = generateDummyProject("Project A", 1000, defaultStartDate, defaultDeadline, false, "Some project description for A", createdManager);
        int pId = createdProject.getProjectId();

        try{
            ProjectModel.cleanData();
        }
        catch (DBException e)
        {
            System.out.println("Database error");
            throw e;
        }

        // Attempt to fetch project
        ProjectModel.getById(pId);
    }

    /**
     * Tests that database table is being created
     * TODO see if there is anyway to check the table values from sql (perhaps too much for a unit test)
     * @throws Exception
     */
    @Test
    public void testCreateTable() throws Exception {
        generateDummyProject("Project A", 1000, defaultStartDate, defaultDeadline, false, "Some project description for A", createdManager);
        generateDummyProject("Project B", 2000, defaultStartDate, defaultDeadline, false, "Some project description for B", createdManager2);

        ProjectModel.createTable();

    }

    /**
     * Positive test that getProjectId is working correctly
     * @throws Exception
     */
    @Test
    public void testGetProjectId() throws Exception {
        ProjectModel createdProject1 = generateDummyProject("Project A", 1000, defaultStartDate, defaultDeadline, false, "Some project description for A", createdManager);
        ProjectModel createdProject2 = generateDummyProject("Project B", 2000, defaultStartDate, defaultDeadline, false, "Some project description for B", createdManager2);

        assertEquals(1, createdProject2.getProjectId() - createdProject1.getProjectId());
    }

    /**
     * Positive test that testSetName is working correctly
     * @throws Exception
     */
    @Test
    public void testSetName() throws Exception {

        String testString = "Project Tester";
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName(testString);
        createdProject.persistData();

        assertThat(testString, equalTo(createdProject.getName()));
    }

    /**
     * Negative test that testSetName is working correctly, not accepting more than 50 characters
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public void testSetNameException() throws Exception {

        String testString = "Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester Project Tester ";
        ProjectModel createdProject = new ProjectModel();

        createdProject.setName(testString);
    }

    /**
     * Positive test to ensure budget is set
     * @throws Exception
     */
    @Test
    public void testSetAndGetBudget() throws Exception {
        double budget = 100034.44;
        ProjectModel createdProject = new ProjectModel();
        createdProject.setBudget(budget);

        assertThat(createdProject.getBudget(), equalTo(budget));
    }

    /**
     * Negative test to ensure budget is set properly
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public void testSetNegativeBudget() throws Exception {
        ProjectModel createdProject = new ProjectModel();
        createdProject.setBudget(-100.0);
    }

    /**
     * Positive test to verify startDate setter and accessor
     * @throws Exception
     */
    @Test
    public void testGetAndSetStartDate() throws Exception {
        Date someDate = new Date();
        Date someLaterDate = new Date(System.currentTimeMillis() + 9999*9999);

        ProjectModel createdProject = new ProjectModel();
        createdProject.setStartDate(someDate);
        createdProject.setDeadline(someLaterDate);

        assertThat(createdProject.getStartDate(), equalTo(DateUtils.filterDateToMidnight(someDate)));
    }

    /**
     * Negative test to verify startDate setter and accessor
     * @throws Exception
     */
    @Test
    public void testGetAndSetDateException() throws Exception {
        Date someDate = new Date();
        Date someLaterDate = new Date(System.currentTimeMillis() + 9999*9999);

        ProjectModel createdProject = new ProjectModel();
        createdProject.setStartDate(someDate);
        createdProject.setDeadline(someLaterDate);

        assertThat(createdProject.getStartDate(), equalTo(DateUtils.filterDateToMidnight(someDate)));
        assertThat(createdProject.getDeadline(), equalTo(DateUtils.filterDateToMidnight(someLaterDate)));
    }

    /**
     * Tests deadline setter and accessor
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public void setDeadline() throws Exception {

        Date someDate = new Date();
        Date someLaterDate = new Date(System.currentTimeMillis() + 9999*9999);

        ProjectModel createdProject = new ProjectModel();
        createdProject.setStartDate(someLaterDate);
        createdProject.setDeadline(someDate);

    }

    @Test(expected = InvalidInputException.class)
    public void testSetDeadlineNotBeforeStartDate() throws Exception {
        ProjectModel createdProject = new ProjectModel();
        createdProject.setStartDate(defaultDeadline);
        createdProject.setDeadline(defaultStartDate);
    }

    @Test(expected = InvalidInputException.class)
    public void testSetStartDateNotAfterDeadline() throws Exception {
        ProjectModel createdProject = new ProjectModel();
        createdProject.setDeadline(defaultStartDate);
        createdProject.setStartDate(defaultDeadline);
    }


    @Test
    public void testDone() throws Exception {

        ProjectModel createdProject = new ProjectModel();

        createdProject.setDone(true);
        assertEquals(true, createdProject.isDone());

        createdProject.setDone(false);
        assertEquals(false, createdProject.isDone());
    }


    @Test
    public void testSetAndGetProjectDescription() throws Exception {

        ProjectModel createdProject = new ProjectModel();
        String testDescription = "blahblahblah";
        createdProject.setProjectDescription(testDescription);

        assertEquals(testDescription, createdProject.getProjectDescription());

    }


    @Test
    public void testManager() throws Exception {

        //Creates a new project
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project N");
        createdProject.persistData();

        createdProject.setManager(createdManager);
        assertEquals(createdManager, createdProject.getManager());

        createdProject.removeManager();
        assertEquals(null, createdProject.getManager());

        createdProject.setManager(createdManager2);
        assertEquals(createdManager2, createdProject.getManager());
    }

    @Test
    public void testGetTasks() throws Exception {
        // Creates a new project
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project H");
        createdProject.setBudget(500);
        createdProject.setStartDate(defaultStartDate);
        createdProject.setDeadline(defaultDeadline);
        createdProject.persistData();

        //Creates tasks and assigns these to the project
        TaskModel t1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 100,
                defaultStartDate, defaultDeadline, false, createdManager, 8, 9, 7, defaultStartDate, defaultDeadline);
        t1.persistData();
        TaskModel t2 = new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 200,
                defaultStartDate, defaultDeadline, false, createdManager, 8, 9, 7, defaultStartDate, defaultDeadline);
        t2.persistData();

        List<TaskModel> taskList = createdProject.getTasks();

        // Tests that there are exactly two projects in the list
        assertEquals(2, taskList.size());
        assertTrue(createdProject.getTasks().contains(t1));
        assertTrue(createdProject.getTasks().contains(t2));
    }

    /**
     * Positive and negative test for equality of Projects
     * @throws Exception
     */
    @Test
    public void testEquals() throws Exception {
        // Create two projects with same data
        ProjectModel p1 = new ProjectModel("Proj", defaultStartDate, defaultDeadline, 1000, false, createdManager, "P Desc.");
        ProjectModel p2 = new ProjectModel("Proj", defaultStartDate, defaultDeadline, 1000, false, createdManager, "P Desc.");
        ProjectModel p3 = generateDummyProject("Project A", 1000, defaultStartDate, defaultDeadline, false, "Some project description for A", createdManager);

        assertTrue(p1.equals(p2));
        assertFalse(p2.equals(p3));

    }

    @Test
    public void testSetNameFiltersNewlines() throws Exception {
        ProjectModel p = new ProjectModel();

        p.setName("Hello\nWorld");
        assertThat(p.getName(), equalTo("Hello World"));

        p.setName("Monty\t\rPython");
        assertThat(p.getName(), equalTo("Monty  Python"));
    }

    /**
     * Test that setting a project ID works as expected.
     * Uses reflection to test the private method ProjectModel::setProjectId(int)
     * @throws Exception
     */
    @Test
    public void testSetProjectId() throws Exception {
        ProjectModel p = new ProjectModel();
        Method setProjectId = p.getClass().getDeclaredMethod("setProjectId", int.class);
        setProjectId.setAccessible(true);
        setProjectId.invoke(p, 4);

        assertThat(p.getProjectId(), equalTo(4));
    }

    /**
     * TODO
     * @throws Exception
     */
    @Test
    public void equalsNullable() throws Exception {

    }

    /**
     * TODO
     * @throws Exception
     */
    @Test
    public void registerObserver() throws Exception {

    }

    /**
     * TODO
     * @throws Exception
     */
    @Test
    public void unregisterObserver() throws Exception {

    }

    /**
     * TODO
     * @throws Exception
     */
    @Test
    public void updateObservers() throws Exception {

    }


}
