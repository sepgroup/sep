package com.sepgroup.sep.tests.ut.model;

import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.model.UserModel;
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
    private static Date defaultDeadline = new Date();

    private ProjectModel createdProject;
    private UserModel createdUser;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ConfigFactory.setProperty("configPath", ProjectModelTest.class.getResource("/test-db.properties").getFile());
    }

    @Before
    public void setUp() throws Exception {
        createdProject = new ProjectModel();
        createdProject.setName("Project 1");
        createdProject.persistData();

        createdUser = new UserModel("FIRST", "LAST", 22.00);
        createdUser.persistData();
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
                defaultDeadline, false, null).persistData();
        new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 20000, defaultStartDate,
                defaultDeadline, false, null).persistData();
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


    @Ignore
    @Test
    public void testGetAllByProject() throws Exception {
        // TODO
        assertTrue(false);
    }

    @Ignore
    @Test
    public void testGetAllByUser() throws Exception {
        // TODO
        assertTrue(false);
    }

    @Ignore
    @Test
    public void testGetLastInsertedId() throws Exception {
        // TODO
    }
}
