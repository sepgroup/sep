package com.sepgroup.sep.tests.ut.model;

import com.sepgroup.sep.SepUserStorage;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.*;
import com.sepgroup.sep.utils.DateUtils;
import org.aeonbits.owner.ConfigFactory;
import org.junit.*;

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
        SepUserStorage.createDBTablesIfNotExisting();
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
        SepUserStorage.dropAllDBTables();
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

            //store variables
            createdProject.persistData();

            //assign manager to the project
            createdProject.setManager(manager);


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
    @Ignore
    @Test
    public void testGetAllByManager() throws Exception {

        // Create projects and assign managers to them
        ProjectModel createdProject1 = new ProjectModel();
        createdProject1.setName("Project A");
        createdProject1.persistData();
        createdProject1.setManager(createdManager);

        ProjectModel createdProject2 = new ProjectModel();
        createdProject2.setName("Project B");
        createdProject2.persistData();
        createdProject2.setManager(createdManager);


        ProjectModel createdProject3 = new ProjectModel();
        createdProject3.setName("Project C");
        createdProject3.persistData();
        createdProject3.setManager(createdManager2);


//        System.out.println(createdManager.getUserId());
//        System.out.println(createdManager2.getUserId());

        //TODO find out why DB query provided no results
        //get projectList associated with manager1
        List<ProjectModel> projectList = ProjectModel.getAllByManager(createdManager);
        assertEquals(2, projectList.size());


        //get projectList associated with manager2
        List<ProjectModel> projectList2 = ProjectModel.getAllByManager(createdManager);
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
    @Ignore
    public void testGetProjectId() throws Exception {
        ProjectModel createdProject1 = generateDummyProject("Project A", 1000, defaultStartDate, defaultDeadline, false, "Some project description for A", createdManager);
        ProjectModel createdProject2 = generateDummyProject("Project B", 2000, defaultStartDate, defaultDeadline, false, "Some project description for B", createdManager2);

        assertEquals(1, createdProject1.getProjectId());
        assertEquals(2, createdProject2.getProjectId());
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

    @Ignore
    @Test
    public void testGetTasks() throws Exception {
        //Creates a new project
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project H");
        createdProject.persistData();

        //Creates tasks and assigns these to the project
        TaskModel t1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdManager, 8, 9, 7);
        TaskModel t2 = new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 20000,
                defaultStartDate, defaultDeadline, false, createdManager, 8, 9, 7);

        //TODO figure out why tasks are not being assigned
        List<TaskModel> taskList = createdProject.getTasks();

        //Tests that there are exactly two projects in the list
        assertThat(taskList.size(), isA(Integer.class));
        assertEquals(2, taskList.size());

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
