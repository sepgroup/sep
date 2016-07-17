package com.sepgroup.sep.tests.ut.model;

import com.sepgroup.sep.SepUserStorage;
import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.model.*;
import org.aeonbits.owner.ConfigFactory;
import org.junit.*;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by jeremybrown on 2016-05-22.
 */
public class TaskModelTest {

    private static Date defaultStartDate = new Date();
    private static Date defaultDeadline = new Date(System.currentTimeMillis() + 9999*9999);

    private ProjectModel createdProject;
    private UserModel createdUser;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        SepUserStorage.createDBTablesIfNotExisting();
    }

    @Before
    public void setUp() throws Exception {
        createdProject = new ProjectModel();
        createdProject.setName("Project 1");
        createdProject.persistData();

        createdUser = new UserModel("FIRST", "LAST", 22.00);
        createdUser.persistData();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Database db = Database.getActiveDB();
        SepUserStorage.dropAllDBTables();
    }

    @Test
    public void testEquals() throws Exception {
        TaskModel t1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser);
        TaskModel t2 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser);

        assertThat(t1, equalTo(t2));
    }

    @Test
    public void testNotEqual() throws Exception {
        TaskModel t1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 20000,
                defaultStartDate, defaultDeadline, false, createdUser);
        TaskModel t2 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser);

        assertThat(t1, not(equalTo(t2)));
    }

    @Test
    public void testPersistData() throws Exception {
        TaskModel createdTask = new TaskModel("TX", "Description of\n TX", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser);
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

    @Test
    public void testPersistDataWithTaskDependencies() throws Exception {
        TaskModel createdTask1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser);
        createdTask1.persistData();

        TaskModel createdTask2 = new TaskModel("T2", "Description of\n TX2", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser);
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

    @Test
    public void testRefreshData() throws Exception {
        // Create task
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser);
        createdTask.persistData();
        int tId = createdTask.getTaskId();

        // Fetch project
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

    @Test(expected = ModelNotFoundException.class)
    public void testDeleteData() throws Exception {
        // Create user
        TaskModel t = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser);
        t.persistData();
        int tId = t.getTaskId();

        // Delete project
        t.deleteData();

        // Attempt to fetch project
        TaskModel.getById(tId);
    }

    @Test
    public void testGetAll() throws Exception {
        new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser).persistData();
        new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 20000, defaultStartDate,
                defaultDeadline, false, createdUser).persistData();
        List<TaskModel> taskList = TaskModel.getAll();

        assertThat(taskList.size(), isA(Integer.class));
        assertThat(taskList.size(), not(0));
    }

    @Test
    public void testGetById() throws Exception {
        // Create task
        TaskModel createdTask  = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser);
        createdTask.persistData();
        int tId = createdTask.getTaskId();

        // Fetch same task
        TaskModel fetchedTask = TaskModel.getById(tId);

        assertThat(fetchedTask, equalTo(createdTask));
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

    @Test(expected = InvalidInputException.class)
    public void testSetDeadlineNotBeforeStartDate() throws Exception {
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setStartDate(defaultDeadline);
        createdTask.setDeadline(defaultStartDate);
    }

    @Test(expected = InvalidInputException.class)
    public void testSetStartDateNotAfterDeadline() throws Exception {
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setDeadline(defaultStartDate);
        createdTask.setStartDate(defaultDeadline);
    }

    @Test(expected = InvalidInputException.class)
    public void testSetNegativeBudget() throws Exception {
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setBudget(-100.0);
    }

    @Test
    public void testSetBudget() throws Exception {
        double budget = 100034.44;
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId());
        createdTask.setBudget(budget);

        assertThat(createdTask.getBudget(), equalTo(budget));
    }

    @Ignore
    @Test
    public void testGetAllByProject() throws Exception {
        // TODO
        assertTrue(false);
    }

    @Test
    public void testGetAllByUser() throws Exception {
        // Create tasks
        TaskModel t1 = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser);
        TaskModel t2 = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser);
        TaskModel t3 = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, null);
        TaskModel t4 = new TaskModel("TTDD", "Description of\n TTDD", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, null);
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

    @Test(expected = TaskDependencyException.class)
    public void testDetectDependencyCycleActiveModels() throws Exception {
        TaskModel t1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser);
        TaskModel t2 = new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser);
        TaskModel t3 = new TaskModel("T3", "Description of\n T3", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser);
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
                defaultDeadline, false, createdUser);
        TaskModel t2 = new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser);
        TaskModel t3 = new TaskModel("T3", "Description of\n T3", createdProject.getProjectId(), 10000, defaultStartDate,
                defaultDeadline, false, createdUser);
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

    @Ignore
    @Test
    public void testGetLastInsertedId() throws Exception {
        // TODO
    }
}
