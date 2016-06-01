package com.sepgroup.sep.tests.ut.model;

import com.sepgroup.sep.ModelNotFoundException;
import com.sepgroup.sep.task.TaskModel;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by jeremybrown on 2016-05-22.
 */
public class TaskModelTest {
    // TODO

    private static Date defaultStartDate = new Date();
    private static Date defaultDeadline = new Date();

    @Test
    public void testEquals() throws Exception {
        TaskModel t1 = new TaskModel("T1", "Description of\n T1", 1, 10000, defaultStartDate, defaultDeadline, false, 0);
        TaskModel t2 = new TaskModel("T1", "Description of\n T1", 1, 10000, defaultStartDate, defaultDeadline, false, 0);

        assertThat(t1, equalTo(t2));
    }

    @Test
    public void testPersistData() throws Exception {
        TaskModel createdTask = new TaskModel("TX", "Description of\n TX", 1, 10000, defaultStartDate, defaultDeadline, false, 0);
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
    public void testRefreshData() throws Exception {
        // Create user
        TaskModel createdTask = new TaskModel("TTDD", "Description of\n TTDD", 1, 10000, defaultStartDate, defaultDeadline, false, 0);
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
        fetchedTask.setDescription(".. budget was cut...");
        fetchedTask.setBudget(0);
        fetchedTask.setDone(true);
        fetchedTask.persistData();

        // Refresh first project instance
        createdTask.refreshData();

        assertThat(createdTask, equalTo(fetchedTask));
    }

    @Test(expected = ModelNotFoundException.class)
    public void testDeleteData() throws Exception {
        // Create user
        TaskModel t = new TaskModel("TTDD", "Description of\n TTDD", 1, 10000, defaultStartDate, defaultDeadline, false, 0);
        t.persistData();
        int tId = t.getTaskId();

        // Delete project
        t.deleteData();

        // Attempt to fetch project
        TaskModel.getById(tId);
    }

    @Test
    public void testGetAll() throws Exception {
        new TaskModel("T1", "Description of\n T1", 1, 10000, defaultStartDate, defaultDeadline, false, 0).persistData();
        new TaskModel("T2", "Description of\n T2", 1, 20000, defaultStartDate, defaultDeadline, false, 0).persistData();
        List<TaskModel> taskList = TaskModel.getAll();

        assertThat(taskList.size(), isA(Integer.class));
        assertThat(taskList.size(), not(0));
    }

    @Test
    public void testGetById() throws Exception {
        // Create task
        TaskModel createdTask  = new TaskModel("T1", "Description of\n T1", 1, 10000, defaultStartDate, defaultDeadline, false, 0);
        createdTask.persistData();
        int tId = createdTask.getTaskId();

        // Fetch same task
        TaskModel fetchedTask = TaskModel.getById(tId);

        assertThat(fetchedTask, equalTo(createdTask));
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
