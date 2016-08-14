package com.sepgroup.sep.tests.structural;

import com.sepgroup.sep.model.*;
import com.sepgroup.sep.utils.DateUtils;
import org.junit.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class hasDependencyCycleTest {
	private static Date defaultStartDate = new Date();
	private static Date defaultDeadline = new Date(System.currentTimeMillis() + 2 * 9999 * 9999);

	private ProjectModel createdProject, createdProject2;
	private static Date createdProjectStartDate = new Date(System.currentTimeMillis() - 5 * 9999 * 9999);
	private static Date createdProjectDeadline = new Date(System.currentTimeMillis() + 5 * 9999 * 9999);

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
		createdUser = new UserModel("FIRST", "LAST", 22.00);
		createdUser.persistData();

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DBManager.dropAllDBTables();
	}

	/*
	 * 
	 * Test 1 Path:<228,229,230,231,exit>
	 *  Identifier: S3-1
	 */
	@Test(expected = ModelNotFoundException.class)
	public void testhasDependencyCyclePath1() throws Exception {
		TaskModel task1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
				defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
		TaskModel task2 = new TaskModel("T2", "Description of\n T1", createdProject.getProjectId(), 10000,
				defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
		Method hasDependencyCycle = task1.getClass().getDeclaredMethod("hasDependencyCycle", TaskModel.class);
		hasDependencyCycle.setAccessible(true);

		task1.persistData();
		task2.persistData();
		task2.deleteData();
		task2.refreshData();
		assertThat(hasDependencyCycle.invoke(task1, task2), equalTo(false));
	}

	/*
	 * 
	 * Path 2: <228,229,233,234,235,exit>
	 * Identifier: S3-2
	 */
	@Test
	public void testhasDependencyCyclePath2() throws Exception {
		TaskModel task1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
				defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
		TaskModel task2 = new TaskModel("T2", "Description of\n T1", createdProject.getProjectId(), 10000,
				defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
		Method hasDependencyCycle = task1.getClass().getDeclaredMethod("hasDependencyCycle", TaskModel.class);
		hasDependencyCycle.setAccessible(true);
		task1.persistData();
		task2.persistData();
		hasDependencyCycle.invoke(task2, task1);
		assertThat(hasDependencyCycle.invoke(task1, task2), equalTo(false));

	}

	/*
	 * 
	 * 
	 * Path 3:<228,229,233,234,237,238, exit>
	 * Identifier: S3-3
	 * 
	 */
	@Test
	public void testhasDependencyCyclePath3() throws Exception {
		TaskModel task1 = new TaskModel("T1", "Description of\n T1", createdProject.getProjectId(), 10000,
				defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
		TaskModel task2 = new TaskModel("T2", "Description of\n T1", createdProject.getProjectId(), 10000,
				defaultStartDate, defaultDeadline, false, createdUser, 8, 9, 7, defaultStartDate, defaultDeadline);
		Method hasDependencyCycle = task1.getClass().getDeclaredMethod("hasDependencyCycle", TaskModel.class);
		hasDependencyCycle.setAccessible(true);
		task1.persistData();
		task2.persistData();
		task1.addDependency(task2);
		task2.addDependency(task1);
		task1.persistData();
		task2.persistData();
		assertThat(hasDependencyCycle.invoke(task1, task2), equalTo(true));
	}

	/*
	 * Path4:<228,229,233,234,237,242,247,exit>, Unreachable test.
	 * Identifier: S3-4
	 * 
	 */
	@Ignore ("Test path unreachable\n" +
            "List cannot be empty if nonempty condition has been satisfied before")
	@Test
	public void testhasDependencyCyclePath4() throws Exception {

	}

	/*
	 * Path5:<228,229,230,233,234,237,242,243,244,exit> Unreachable test.
	 * Identifier: S3-5
	 * 
	 */
	@Ignore
	@Test
	public void testhasDependencyCyclePath5() throws Exception {

	}

	/*
	 * Path6:<228,229,233,234,237,242,243,242,243,244,exit> Unreachable test.
	 * Identifier: S3-6
	 * 
	 */
	@Ignore
	@Test
	public void testhasDependencyCyclePath6() throws Exception {

	}

}
