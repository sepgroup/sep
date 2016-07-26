package com.sepgroup.sep.tests.ut.model;

import com.sepgroup.sep.model.*;
import com.sepgroup.sep.utils.DateUtils;
import com.sun.javafx.tk.Toolkit;
import org.junit.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;


/**
 * Created by jeremybrown on 2016-05-22.
 */
public class TaskModelTest {

    private static Date defaultStartDate = new Date();
    private static Date defaultDeadline = new Date(System.currentTimeMillis() + 2* 9999*9999);

    private ProjectModel createdProject,createdProject2;
    private Date createdProjectStartDate = new Date(System.currentTimeMillis() - 5 * 9999*9999);
    private Date createdProjectDeadline = new Date(System.currentTimeMillis() + 5 * 9999*9999);
    private UserModel createdUser;

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

        createdProject2 = new ProjectModel();
        createdProject2.setName("Project 2");
        createdProject2.setBudget(100000);
        createdProject2.setStartDate(createdProjectStartDate);
        createdProject2.setDeadline(createdProjectDeadline);
        createdProject2.persistData();

        createdUser = new UserModel("FIRST", "LAST", 22.00);
        createdUser.persistData();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DBManager.dropAllDBTables();
    }

    private TaskModel generateTestTask() {
        TaskModel t1 = null;
        try {
            t1 = new TaskModel("T1", "Description of T1", createdProject.getProjectId(), 10000,
                    defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        } catch (Exception e) {

        }
        return t1;
    }

    /**
     * Tests that creating two seperate objects with the same values are equivalent
     */
    @Test
    public void testEquals() throws Exception {
        TaskModel t1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        TaskModel t2 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
    }

    /**
     * Tests that creating different objects leads to an inequality
     */
    @Test
    public void testNotEqual() throws Exception {
        TaskModel t1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 20000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        TaskModel t2 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
    }

    @Test
    public void testPersistData() throws Exception {
        TaskModel createdTask = new TaskModel("TX", "Description of\n TX", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        createdTask.persistData();
        int tId = createdTask.getTaskId();

        TaskModel fetchedTask = null;
        try {
            fetchedTask = TaskModel.getById(tId);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }
        assertThat(fetchedTask, equalTo(createdTask));
    }

    @Ignore
    @Test
    public void testPersistDataWithTaskDependencies() throws Exception {
        TaskModel createdTask1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        createdTask1.persistData();

        TaskModel createdTask2 = new TaskModel("T2", "Description of\n TX2", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        createdTask2.addDependency(createdTask1);
        createdTask2.persistData();
        int t2Id = createdTask2.getTaskId();

        TaskModel fetchedTask2 = null;
        try {
            fetchedTask2 = TaskModel.getById(t2Id);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }
        assertThat(fetchedTask2, equalTo(createdTask2));
    }

    public void testGetLastInsertedId() throws Exception {
        // TODO
    }

    //********************************************
    //Auto-generated stubs for function unit-tests
    //********************************************
    @Test
    public void getDependencies() throws Exception {

    }

    @Test
    public void testAddDependency() throws Exception {
        TaskModel t1 = new TaskModel("T1", "D1", createdProject.getProjectId());
        TaskModel t2 = new TaskModel("T2", "D2", createdProject.getProjectId());
        t1.persistData();
        t2.persistData();

        t1.addDependency(t2);

        assertTrue(t1.getDependencies().contains(t2));
    }

    @Test
    public void testGetAll() throws Exception {
        new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline).persistData();
        new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 20000, defaultStartDate,
                defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline).persistData();
        List<TaskModel> taskList = TaskModel.getAll();

        assertThat(taskList.size(), isA(Integer.class));
        assertThat(taskList.size(), not(0));
    }

    @Test
    public void testGetById() throws Exception {
        // Create task
        TaskModel createdTask = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        createdTask.persistData();
        int tId = createdTask.getTaskId();

        // Fetch same task
        TaskModel fetchedTask = TaskModel.getById(tId);

        assertThat(fetchedTask, equalTo(createdTask));
    }

    @Test(expected = TaskDependencyException.class)
    public void testAddSelfAsDependency() throws Exception {
        TaskModel t1 = new TaskModel("T1", "D1", createdProject.getProjectId());
        t1.addDependency(t1);
    }

    @Test(expected = TaskDependencyException.class)
    public void testAddExistingDependency() throws Exception {
        TaskModel t1 = new TaskModel("T1", "D1", createdProject.getProjectId());
        TaskModel t2 = new TaskModel("T2", "D2", createdProject.getProjectId());
        t1.addDependency(t2);
        t1.addDependency(t2);
    }

    @Test
    public void testCreateWithDependencies() throws Exception {
        // Create tasks
        TaskModel t1 = new TaskModel("T1", "D1", createdProject.getProjectId());
        TaskModel t2 = new TaskModel("T2", "D2", createdProject.getProjectId());
        TaskModel t3Created = new TaskModel("T3", "D3", createdProject.getProjectId());
        t3Created.addDependency(t1);
        t3Created.addDependency(t2);
        t1.persistData();
        t2.persistData();
        t3Created.persistData();
        int t3Id = t3Created.getTaskId();

        // Fetch
        TaskModel t3Fetched = TaskModel.getById(t3Id);

        // Check dependencies
        List<TaskModel> t3Dependencies = t3Fetched.getDependencies();
        assertTrue(t3Dependencies.contains(t1));
        assertTrue(t3Dependencies.contains(t2));
    }

    @Test
    public void testUpdateWithDependencies() throws Exception {
        // Create tasks
        TaskModel t1 = new TaskModel("T1", "D1", createdProject.getProjectId());
        TaskModel t2 = new TaskModel("T2", "D2", createdProject.getProjectId());
        TaskModel t3 = new TaskModel("T3", "D3", createdProject.getProjectId());
        TaskModel t4Created = new TaskModel("T4", "D4", createdProject.getProjectId());
        t4Created.addDependency(t1);
        t4Created.addDependency(t2);
        t1.persistData();
        t2.persistData();
        t3.persistData();
        t4Created.persistData();
        int t4Id = t4Created.getTaskId();

        t4Created.addDependency(t3);
        t4Created.removeDependency(t1);
        t4Created.persistData();

        // Fetch
        TaskModel t4Fetched = TaskModel.getById(t4Id);

        // Check dependencies
        List<TaskModel> t4Dependencies = t4Fetched.getDependencies();
        assertTrue(t4Dependencies.contains(t2));
        assertTrue(t4Dependencies.contains(t3));
        assertFalse(t4Dependencies.contains(t1));
    }

    @Test
    public void testDeleteWithDependenciesRefreshData() throws Exception {
        // Create tasks
        TaskModel t1 = new TaskModel("T1", "D1", createdProject.getProjectId());
        TaskModel t2 = new TaskModel("T2", "D2", createdProject.getProjectId());
        TaskModel t3Created = new TaskModel("T3", "D3", createdProject.getProjectId());
        t1.persistData();
        t2.persistData();
        t3Created.persistData();
        int t3Id = t3Created.getTaskId();
        t3Created.addDependency(t1);
        t3Created.addDependency(t2);
        t3Created.persistData();

        // Delete task depended on
        t1.deleteData();

        // Refresh & check
        t3Created.refreshData();
        List<TaskModel> t3Dependencies = t3Created.getDependencies();
        assertTrue(t3Dependencies.contains(t2));
        assertTrue(!t3Dependencies.contains(t1));
    }

    @Test
    public void testDeleteWithDependenciesFetchData() throws Exception {
        // Create tasks
        TaskModel t1 = new TaskModel("T1", "D1", createdProject.getProjectId());
        TaskModel t2 = new TaskModel("T2", "D2", createdProject.getProjectId());
        TaskModel t3Created = new TaskModel("T3", "D3", createdProject.getProjectId());
        t1.persistData();
        t2.persistData();
        t3Created.persistData();
        int t3Id = t3Created.getTaskId();
        t3Created.addDependency(t1);
        t3Created.addDependency(t2);
        t3Created.persistData();

        // Delete task depended on
        t1.deleteData();

        // Fetch & check
        TaskModel t3Fetched = TaskModel.getById(t3Id);
        List<TaskModel> t3Dependencies = t3Fetched.getDependencies();
        assertTrue(t3Dependencies.contains(t2));
        assertTrue(!t3Dependencies.contains(t1));
    }

    //TODO
    @Test
    public void removeDependency() throws Exception {

    }

    @Test
    public void testRefreshData() throws Exception {
        // Create task
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        createdTask.persistData();
        int tId = createdTask.getTaskId();

        // Fetch task
        TaskModel fetchedTask = null;
        try {
            fetchedTask = TaskModel.getById(tId);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }

        // Modify & persist project
        fetchedTask.setName("Task Task D :D");
        fetchedTask.setBudget(0);
        fetchedTask.setDescription(".. budget was cut...");
        fetchedTask.setDone(true);
        fetchedTask.persistData();

        // Refresh first project instance
        createdTask.refreshData();

        assertThat(createdTask, equalTo(fetchedTask));
    }

    @Test(expected = TaskDependencyException.class)
    public void testDetectDependencyCycleActiveModels() throws Exception {
        TaskModel t1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        TaskModel t2 = new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        TaskModel t3 = new TaskModel("T3", "Description of\n T3", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        t1.persistData();
        t2.persistData();
        t3.persistData();

        t1.addDependency(t2);
        t1.persistData();

        t2.addDependency(t3);
        t2.persistData();

        t3.addDependency(t1);
    }

    @Test(expected = TaskDependencyException.class)
    public void testDetectDependencyCycleFetchFromDb() throws Exception {
        TaskModel t1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        TaskModel t2 = new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        TaskModel t3 = new TaskModel("T3", "Description of\n T3", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        t1.persistData();
        t2.persistData();
        t3.persistData();

        t1.addDependency(t2);
        t1.persistData();

        try {
            t2 = TaskModel.getById(2);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }

        t2.addDependency(t3);
        t2.persistData();

        try {
            t3 = TaskModel.getById(3);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }

        t3.addDependency(t1);
    }

    @Test
    public void testSetNameFiltersNewlines() throws Exception {
        TaskModel t = new TaskModel();

        t.setName("Hello\nWorld");
        assertThat(t.getName(), equalTo("Hello World"));
        t.setName("Monty\t\rPython");
        assertThat(t.getName(), equalTo("Monty  Python"));
    }

    @Test(expected = ModelNotFoundException.class)
    public void testDeleteData() throws Exception {
        // Create user
        TaskModel t = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        t.persistData();
        int tId = t.getTaskId();

        // Delete project
        t.deleteData();

        // Attempt to fetch project
        TaskModel.getById(tId);
    }

    // TODO
    @Ignore
    @Test
    public void testGetAllByProject() throws Exception {

        assertTrue(false);
    }


    @Test
    public void testGetAllByUser() throws Exception {
        // Create tasks
        TaskModel t1 = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        TaskModel t2 = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        TaskModel t3 = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, null, 8, 9, 7, defaultStartDate, defaultDeadline);
        TaskModel t4 = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, null, 8, 9, 7, defaultStartDate, defaultDeadline);
        t1.persistData();
        t2.persistData();
        t3.persistData();
        t4.persistData();

        List<TaskModel> tasksByAssignee = TaskModel.getAllByAssignee(createdUser);

        assertTrue(tasksByAssignee.contains(t1));
        assertTrue(tasksByAssignee.contains(t2));
        assertFalse(tasksByAssignee.contains(t3));
        assertFalse(tasksByAssignee.contains(t4));
    }


    //TODO
    @Test
    public void cleanData() throws Exception {

    }

    //TODO
    @Test
    public void createTable() throws Exception {

    }

    // Tests getTaskId by checking the difference of two sequentially created tasks is 1
    @Test
    public void testGetTaskId() throws Exception {
        TaskModel t1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        TaskModel t2 = new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        t1.persistData();
        t2.persistData();
        assertEquals(1, t2.getTaskId() - t1.getTaskId());
    }


    @Test
    public void testGetName() throws Exception {
        TaskModel t1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        t1.setName("new name");
        assertEquals("new name", t1.getName());
    }


    @Test
    public void testSetName() throws Exception {
        TaskModel t1 = generateTestTask();
        assertEquals("T1", t1.getName());
    }

    @Test
    public void testGetDescription() throws Exception {
        TaskModel t1 = generateTestTask();
        assertEquals("Description of T1", t1.getDescription());
    }

    @Test
    public void testSetDescription() throws Exception {
        TaskModel t1 = generateTestTask();
        t1.setDescription("description");
        assertEquals("description", t1.getDescription());
    }

    @Test
    public void testGetProjectId() throws Exception {
        TaskModel t1 = new TaskModel("T1", "test", createdProject.getProjectId());
        assertEquals(createdProject.getProjectId(), t1.getProjectId());
    }

    @Test
    public void testSetProjectId() throws Exception {
        TaskModel t1 = generateTestTask();
        t1.setProjectId(createdProject2.getProjectId());
        assertEquals(createdProject2.getProjectId(), t1.getProjectId());
    }


    /**
     * Positive test to check whether budget setter
     * @throws Exception
     */
    @Test
    public void testSetBudget() throws Exception {
        double budget = 134.44;
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setBudget(budget);

        assertThat(createdTask.getBudget(), equalTo(budget));
    }

    /**
     * Negative test to check whether budget can be set with negative values
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public void testSetNegativeBudget() throws Exception {
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setBudget(-100.0);
    }

    /**
     * Negative test to check whether budget can be set with value that would make the project's actual budget surpass
     * its set budget.
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public void testSetBudgetOverProjectBudget() throws Exception {
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setBudget(1000000000.0);
    }

    /**
     * Negative test to check whether a task's deadline can be set before its start date.
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public void testSetDeadlineBeforeStartDate() throws Exception {
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setStartDate(defaultDeadline);
        createdTask.setDeadline(defaultStartDate);
    }

    /**
     * Negative test to check whether a task's start date can be set before its deadline.
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public void testSetStartDateAfterDeadline() throws Exception {
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setDeadline(defaultStartDate);
        createdTask.setStartDate(defaultDeadline);
    }

    /**
     * Negative test to check whether a task's start date can be set before its project's start date.
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public void testSetStartDateBeforeProjectStartDate() throws Exception {
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        Date taskStartDate = new Date(System.currentTimeMillis() - 6 * 9999*9999);
        createdTask.setStartDate(taskStartDate);
    }

    /**
     * Negative test to check whether a task's deadline can be set after its project's deadline.
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public void testSetDeadlineAfterProjectDeadline() throws Exception {
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        Date taskDeadline = new Date(System.currentTimeMillis() + 3 * 9999*9999);
        createdTask.setDeadline(taskDeadline);
    }

    @Test
    public void testSetStartDate() throws Exception {
        TaskModel t1 = generateTestTask();
        Date someDate = new Date(System.currentTimeMillis() + 100);
        t1.setStartDate(someDate);
        assertThat(t1.getStartDate(), equalTo(DateUtils.filterDateToMidnight(someDate)));
    }

    @Test
    public void testSetActualStartDate() throws Exception {
        TaskModel t1 = generateTestTask();
        Date someDate = new Date(System.currentTimeMillis() + 100);
        t1.setActualStartDate(someDate);
        assertThat(t1.getActualStartDate(), equalTo(DateUtils.filterDateToMidnight(someDate)));
    }

    @Test
    public void testPersistActualStartDate() throws Exception {
        TaskModel t1 = generateTestTask();
        Date someDate = new Date(System.currentTimeMillis() + 100);
        t1.setActualStartDate(someDate);
        t1.persistData();
        int tId = t1.getTaskId();

        TaskModel fetchedTask = null;
        try {
            fetchedTask = TaskModel.getById(tId);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }

        assertThat(t1.getActualStartDate(), equalTo(fetchedTask.getActualStartDate()));
    }

    @Test
    public void testSetActualEndDate() throws Exception {
        TaskModel t1 = generateTestTask();
        Date someDate = new Date(System.currentTimeMillis() + 100);
        t1.setActualEndDate(someDate);
        assertThat(t1.getActualEndDate(), equalTo(DateUtils.filterDateToMidnight(someDate)));
    }

    @Test
    public void testPersistActualEndDate() throws Exception {
        TaskModel t1 = generateTestTask();
        Date someDate = new Date(System.currentTimeMillis() + 100);
        t1.setActualEndDate(someDate);
        t1.persistData();
        int tId = t1.getTaskId();

        TaskModel fetchedTask = null;
        try {
            fetchedTask = TaskModel.getById(tId);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }

        assertThat(t1.getActualEndDate(), equalTo(fetchedTask.getActualEndDate()));
    }

    /**
     * Positive test verifying most likely time is saved & retrieved from DB
     * @throws Exception
     */
    @Test
    public void testPersistPertTimes() throws Exception {
        // Create & persist task
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setOptimisticTimeToFinish(10);
        createdTask.setMostLikelyTimeToFinish(20);
        createdTask.setPessimisticTimeToFinish(30);
        createdTask.persistData();
        int tId = createdTask.getTaskId();

        // Fetch task & assert value
        TaskModel fetchedTask = null;
        try {
            fetchedTask = TaskModel.getById(tId);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }
        assertThat(fetchedTask.getOptimisticTimeToFinish(), equalTo(createdTask.getOptimisticTimeToFinish()));
        assertThat(fetchedTask.getMostLikelyTimeToFinish(), equalTo(createdTask.getMostLikelyTimeToFinish()));
        assertThat(fetchedTask.getPesimisticTimeToFinish(), equalTo(createdTask.getPesimisticTimeToFinish()));
    }

    @Test(expected = InvalidInputException.class)
    public void testSetMostLikelyTimeNegativeValue() throws Exception {
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setMostLikelyTimeToFinish(-100);
    }

    @Test(expected = InvalidInputException.class)
    public void testSetOptimisticTimeNegativeValue() throws Exception {
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setOptimisticTimeToFinish(-100);
    }

    @Test(expected = InvalidInputException.class)
    public void testSetPessimisticTimeNegativeValue() throws Exception {
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setPessimisticTimeToFinish(-100);
    }


    @Test
    public void getStartDate() throws Exception {
        TaskModel t1 = generateTestTask();
        Date date = new Date(System.currentTimeMillis() - 9999 * 9999);
        t1.setStartDate(date);
        assertEquals(DateUtils.filterDateToMidnight(date).getTime(), t1.getStartDate().getTime());
    }

    @Test
    public void getStartDateString() throws Exception {
        TaskModel t1 = generateTestTask();
        assertEquals(DateUtils.castDateToString(defaultStartDate), t1.getStartDateString());
    }

    @Test
    public void setStartDate() throws Exception {
        TaskModel t1 = generateTestTask();
        Date date = new Date(System.currentTimeMillis() + 9999*9999);
        t1.setStartDate(date);
        assertEquals(DateUtils.filterDateToMidnight(date), t1.getStartDate());
    }

    @Test
    public void setStartDate1() throws Exception {
        TaskModel t1 = generateTestTask();
        Date date = new Date(System.currentTimeMillis() + 9999*9999);
        String stringDate = DateUtils.castDateToString(date);
        t1.setStartDate(stringDate);
        assertEquals(stringDate, DateUtils.castDateToString(t1.getStartDate()));
    }

    @Test
    public void removeStartDate() throws Exception {
        TaskModel t1 = generateTestTask();
        t1.removeStartDate();
        assertEquals(null, t1.getStartDate());
    }

    @Test
    public void getDeadline() throws Exception {
        TaskModel t1 = generateTestTask();
        assertEquals(DateUtils.filterDateToMidnight(defaultDeadline).getTime(), t1.getDeadline().getTime());
    }

    @Test
    public void getDeadlineString() throws Exception {
        TaskModel t1 = generateTestTask();
        assertEquals(DateUtils.castDateToString(defaultDeadline), t1.getDeadlineString());
    }

    @Test
    public void setDeadline() throws Exception {
        TaskModel t1 = generateTestTask();
        Date date = new Date(System.currentTimeMillis() + 9999*9999);

        t1.setDeadline(date);
        assertEquals(DateUtils.filterDateToMidnight(date).getTime(), t1.getDeadline().getTime());
    }

    @Test
    public void setDeadline1() throws Exception {
        TaskModel t1 = generateTestTask();
        Date date = new Date(System.currentTimeMillis() + 9999*9999);
        String stringDate = DateUtils.castDateToString(date);
        t1.setDeadline(stringDate);

        assertEquals(stringDate, DateUtils.castDateToString(t1.getDeadline()));
    }

    @Test
    public void removeDeadline() throws Exception {
        TaskModel t1 = generateTestTask();
        t1.removeDeadline();
        assertEquals(null, t1.getDeadline());

    }

    @Test
    public void isDone() throws Exception {
        TaskModel t1 = generateTestTask();
        assertFalse(t1.isDone());
    }

    @Test
    public void setDone() throws Exception {
        TaskModel t1 = generateTestTask();
        t1.setDone(true);
        assertTrue(t1.isDone());
    }

    @Test
    public void getAssignee() throws Exception {
        TaskModel t1 = generateTestTask();
        assertEquals(createdUser, t1.getAssignee());
    }

    @Test
    public void setAssignee() throws Exception {
        TaskModel t1 = generateTestTask();
        UserModel createdUser2 = new UserModel("FIRST", "LAST", 22.00);
        createdUser2.persistData();
        t1.setAssignee(createdUser2);
        assertEquals(createdUser2, t1.getAssignee());
    }

    @Test
    public void removeAssignee() throws Exception {
        TaskModel t1 = generateTestTask();
        t1.removeAssignee();
        assertEquals(null, t1.getAssignee());
    }

    @Test
    public void setAssignee1() throws Exception {
        TaskModel t1 = generateTestTask();
        UserModel user = new UserModel("FIRST", "LAST", 22.00);
        user.persistData();
        t1.setAssignee(user.getUserId());
        assertEquals(user.getUserId(), t1.getAssignee().getUserId());
    }

    @Test
    public void setAndGetTags() throws Exception {
        TaskModel t1 = generateTestTask();
        List<String> tags = new ArrayList<String>();
        tags.add("tag1");
        tags.add("tag2");
        t1.setTags(tags);
        assertTrue(tags.equals(t1.getTags()));
    }



    @Test
    public void getTagsString() throws Exception {
        TaskModel t1 = generateTestTask();
        List<String> tags = new ArrayList<String>();
        tags.add("tag1");
        tags.add("tag2");
        t1.setTags(tags);
        assertEquals("tag1 tag2", t1.getTagsString());
    }

    @Test
    public void addTag() throws Exception {
        TaskModel t1 = generateTestTask();
        t1.addTag("tag 1");
        assertEquals("tag 1", t1.getTagsString());
    }

    @Test
    public void addTagsFromString() throws Exception {

    }

    @Test
    public void removeTag() throws Exception {
    }
}
