package com.sepgroup.sep.tests.structural;

import com.sepgroup.sep.model.*;
import org.junit.*;

import java.lang.reflect.Method;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test hasDependencyCycle() from TaskModel.java.
 * 6 tests for basis path coverage.
 * Created by nicola on 2016-08-12.
 */
public class HasDependencyCycleTest {
	private static Date defaultStartDate = new Date();
	private static Date defaultDeadline = new Date(System.currentTimeMillis() + 2 * 9999 * 9999);

	private ProjectModel createdProject, createdProject2;
	private static Date createdProjectStartDate = new Date(System.currentTimeMillis() - 5 * 9999 * 9999);
	private static Date createdProjectDeadline = new Date(System.currentTimeMillis() + 5 * 9999 * 9999);

	private UserModel createdUser;

    private Method hasDependencyCycle;

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
		createdUser = new UserModel("FIRST", "LAST", 22.00);
		createdUser.persistData();

        hasDependencyCycle = TaskModel.class.getDeclaredMethod("hasDependencyCycle", TaskModel.class);
        hasDependencyCycle.setAccessible(true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DBManager.dropAllDBTables();
	}

    /**
     * <p>
     *     Identifier: S3-1
     * </p>
     * <p>
     *     Path 1: <228,229,230,231,exit>
     * </p>
     */
	@Test(expected = ModelNotFoundException.class)
	public void testHasDependencyCyclePath1() throws Exception {
		TaskModel task1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
				defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
		TaskModel task2 = new TaskModel("T2", "Description of\n T1", createdProject.getProjectId(), 10000,
				defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);

		task1.persistData();
		task2.persistData();
		task2.deleteData();
		task2.refreshData();
		assertThat(hasDependencyCycle.invoke(task1, task2), equalTo(false));
	}

    /**
     * <p>
     *     Identifier: S3-2
     * </p>
     * <p>
     *     Path 2: <228,229,233,234,235,exit>
     * </p>
     */
	@Test
	public void testHasDependencyCyclePath2() throws Exception {
		TaskModel task1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
				defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
		TaskModel task2 = new TaskModel("T2", "Description of\n T1", createdProject.getProjectId(), 10000,
				defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);

        task1.persistData();
		task2.persistData();
		hasDependencyCycle.invoke(task2, task1);
		assertThat(hasDependencyCycle.invoke(task1, task2), equalTo(false));

	}

    /**
     * <p>
     *     Identifier: S3-3
     * </p>
     * <p>
     *     Path 3: <228,229,233,234,237,238, exit>
     * </p>
     */
	@Test
	public void testHasDependencyCyclePath3() throws Exception {
		TaskModel task1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
				defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
		TaskModel task2 = new TaskModel("T2", "Description of\n T1", createdProject.getProjectId(), 10000,
				defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);

        task1.persistData();
		task2.persistData();
		task1.addDependency(task2);
		task2.addDependency(task1);
		task1.persistData();
		task2.persistData();
		assertThat(hasDependencyCycle.invoke(task1, task2), equalTo(true));
	}

    /**
     * <p>
     *     Identifier: S3-4
     * </p>
     * <p>
     *     Path 4: <228,229,233,234,237,242,247,exit>, Unreachable test.
     * </p>
     */
	@Ignore ("Test path unreachable\n" +
            "List cannot be empty if nonempty condition has been satisfied before")
	@Test
	public void testHasDependencyCyclePath4() throws Exception {

	}

    /**
     * <p>
     *     Identifier: S3-5
     * </p>
     * <p>
     *     Path 5: <228,229,230,233,234,237,242,243,244,exit> Unreachable test.
     * </p>
     */
	@Ignore("Test path unreachable")
	@Test
	public void testHasDependencyCyclePath5() throws Exception {

	}

    /**
     * <p>
     *     Identifier: S3-6
     * </p>
     * <p>
     *     Path 6: <228,229,233,234,237,242,243,242,243,244,exit> Unreachable test.
     * </p>
     */
	@Ignore("Test path unreachable")
	@Test
	public void testHasDependencyCyclePath6() throws Exception {

	}

}
