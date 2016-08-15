package com.sepgroup.sep.tests.structural;

import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.*;
import com.sepgroup.sep.tests.TestCommons;
import org.junit.*;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test runSingleResultQuery() from TaskModel.java.
 * 8 tests for basis path coverage.
 * Created by charles on 2016-08-13.
 */
public class RunSingleResultQueryTest {

    private static Date defaultStartDate = new Date();
    private static Date defaultDeadline = new Date(System.currentTimeMillis() + 2 * 9999*9999);

    private ProjectModel createdProject;
    private UserModel createdUser;

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
        createdUser = TestCommons.createMainUser();
    }

    @After
    public void tearDown() throws Exception {
        createdProject.deleteData();
        createdUser.deleteData();
    }

    /**
     * <p>
     *     Identifier: S2-1
     * </p>
     * <p>
     *     Path: <976,978,980,1033,1034,1048,exit>
     * </p>
     * <p>
     *
     * </p>
     */
    @Test(expected = ModelNotFoundException.class)
    public void testRunSingleResultQueryPath1() throws Exception {
        int tId = 20;

        // Try fetch from empty database
        TaskModel fetchedTask = TaskModel.getById(tId);
    }

    /**
     * <p>
     *     Identifier: S2-2
     * </p>
     * <p>
     *     Path: <976,978,980,982,983,984,985,986,987,988,989,990,991,992,992,998,999,1000,1001,1003,1004,1005,exit>
     * </p>
     * <p>
     *     Unfeasible path: handling invalid dates is done at the database input level, therefore it is not technically possible to read an invalid date from the database.
     *     This path is not reproducible without invalid data in the database, which does not seem possible with SQLite3.
     * </p>
     */
    @Ignore
    @Test(expected = DBException.class)
    public void testRunSingleResultQueryPath2() throws Exception {
        // Create task
        Date incorrectStartDate = new Date(1000,15,45);

        TaskModel task1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, incorrectStartDate, defaultDeadline);
        task1.persistData();
        int tId = task1.getTaskId();

        // Fetch task
        TaskModel fetchedTask = TaskModel.getById(tId);
    }

    /**
     * <p>
     *     Identifier: S2-3
     * </p>
     * <p>
     *     Test valid read of a task w/ a user but w/o tags from the database.
     * </p>
     * <p>
     *     Path: <976,978,980,982,983,984,985,986,987,988,989,990,991,992,992,998,999,1000,1001,1003,1007,1008,1009,1010,1013,1014,1015,1016,1018,1021,1023,1024,1025,1029,1030,1041,1042,1043,1047,exit>
     * </p>
     */
    @Test
    public void testRunSingleResultQueryPath3() throws Exception {
        TaskModel task = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
        task.persistData();
        int tId = task.getTaskId();

        // Fetch same task
        TaskModel fetchedTask = TaskModel.getById(tId);
        assertThat(fetchedTask, equalTo(task));
    }

    /**
     * <p>
     *     Identifier: S2-4
     * </p>
     * <p>
     *     Test valid read of a task w/o a user but w/ tags from the database.
     * </p>
     * <p>
     *     Path: <976,978,980,982,983,984,985,986,987,988,989,990,991,992,992,998,999,1000,1001,1003,1007,1008,1009,1010,1013,1021,1023,1024,1025,1029,1030,1041,1042,1043,1047,exit>
     * </p>
     */
    @Test
    public void testRunSingleResultQueryPath4() throws Exception {
        TaskModel task = new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, null, 8, 9, 7, defaultStartDate, defaultDeadline);
        task.addTag("Val1");
        task.persistData();
        int tId = task.getTaskId();

        // Fetch same task
        TaskModel fetchedTask = TaskModel.getById(tId);
        assertThat(fetchedTask, equalTo(task));
    }

    /**
     * <p>
     *     Identifier: S2-5
     * </p>
     * <p>
     *     Path: <976,978,980,982,983,984,985,986,987,988,989,990,991,992,998,999,1000,1001,1003,1007,1008,1009,1010,1013,1021,1023,1027,1029,1030,1041,1042,1043,1047,exit>
     * </p>
     * <p>
     *     Unfeasible path: this path tests handling the case that there is an issue closing the connection to the database (task has tags).
     *     This is not simple to simulate with our current code.
     * </p>
     */
    @Ignore
    @Test
    public void testRunSingleResultQueryPath5() throws Exception {
        TaskModel task = new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, null, 8, 9, 7, defaultStartDate, defaultDeadline);
        task.addTag("Val1");
        task.persistData();
        int tId = task.getTaskId();

        // Fetch same task
        TaskModel fetchedTask = TaskModel.getById(tId);
        assertThat(fetchedTask, equalTo(task));
    }

    /**
     * <p>
     *     Identifier: S2-6
     * </p>
     * <p>
     *     Test valid read of a task w/o a user and w/o tags from the database.
     * </p>
     * <p>
     *     Path: <976,978,980,982,983,984,985,986,987,988,989,990,991,992,998,999,1000,1001,1003,1007,1008,1009,1010,1013,1021,1023,1027,1029,1030,1041,1042,1047,exit>
     * </p>
     */
    @Test
    public void testRunSingleResultQueryPath6() throws Exception {
        TaskModel task4 = new TaskModel("T4", "Description of\n T4", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, null, 8, 9, 7, defaultStartDate, defaultDeadline);
        task4.persistData();
        int tId = task4.getTaskId();

        // Fetch same task
        TaskModel fetchedTask = TaskModel.getById(tId);
        assertThat(fetchedTask, equalTo(task4));
    }

    /**
     * <p>
     *     Identifier: S2-7
     * </p>
     * <p>
     *     Path: <976,978,980,982,983,984,985,986,987,988,989,990,991,992,992,998,999,1000,1001,1003,1007,1008,1009,1010,1013,1021,1023,1027,1029,1030,1041,1042,1043,1045,exit>
     * </p>
     * <p>
     *     Unfeasible path: this path tests handling the case that there is an issue closing the connection to the database (task has no tags).
     *     This is not simple to simulate with our current code.
     * </p>
     */
    @Ignore
    @Test
    public void testRunSingleResultQueryPath7() throws Exception {
        TaskModel task = new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, null, 8, 9, 7, defaultStartDate, defaultDeadline);
        task.persistData();
        int tId = task.getTaskId();

        // Fetch same task
        TaskModel fetchedTask = TaskModel.getById(tId);
        assertThat(fetchedTask, equalTo(task));
    }

    /**
     * <p>
     *     Identifier: S2-8
     * </p>
     * <p>
     *     Path: <976,978,980,982,983,984,985,986,987,988,989,990,991,992,992,998,999,1000,1001,1003,1007,1008,1009,1010,1013,1021,1023,1027,1029,1030,1036,1037,1038,exit>
     * </p>
     * <p>
     *     Unfeasible path: Test catching an SQLException thrown by the database driver when fetching data from the database (fetched task has no users, no tags)
     *     This does not happen if all the lines before the exception have executed without an error (the exception would be thrown earlier).
     * </p>
     */
    @Ignore
    @Test
    public void testRunSingleResultQueryPath8() throws Exception {
        TaskModel task = new TaskModel("T2", "Description of\n T2", createdProject.getProjectId(), 10000,
                defaultStartDate, defaultDeadline, false, null, 8, 9, 7, defaultStartDate, defaultDeadline);
        task.persistData();
        int tId = task.getTaskId();

        // Fetch same task
        TaskModel fetchedTask = TaskModel.getById(tId);
        assertThat(fetchedTask, equalTo(task));
    }
}
