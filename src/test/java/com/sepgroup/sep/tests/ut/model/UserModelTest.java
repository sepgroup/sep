package com.sepgroup.sep.tests.ut.model;

import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.UserModel;
import org.aeonbits.owner.ConfigFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by jeremybrown on 2016-05-30.
 */
public class UserModelTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ConfigFactory.setProperty("configPath", ProjectModelTest.class.getResource("/test-db.properties").getFile());
        Database db = Database.getActiveDB();
        db.createTables();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Database db = Database.getActiveDB();
        db.dropAllTables();
    }

    @Test
    public void testEquals() throws Exception {
        // Create two projects with same data
        UserModel u1 = new UserModel("FirstName", "LastName", 10.99);
        UserModel u2 = new UserModel("FirstName", "LastName", 10.99);

        assertTrue(u1.equals(u2));
    }

    @Test
    public void testPersistData() throws Exception {
        UserModel createdUser = new UserModel("John2", "Smith2", 1.00);
        createdUser.persistData();
        int uId = createdUser.getUserId();

        UserModel fetchedUser = null;
        try {
            fetchedUser = UserModel.getById(uId);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }
        assertThat(fetchedUser, equalTo(createdUser));
    }

    @Test
    public void testRefreshData() throws Exception {
        // Create user
        UserModel createdUser = new UserModel("John3", "Smith3", 200);
        createdUser.persistData();
        int uId = createdUser.getUserId();

        // Fetch project
        UserModel fetchedUser = null;
        try {
            fetchedUser = UserModel.getById(uId);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }

        // Modify & persist project
        fetchedUser.setFirstName("Ronnie");
        fetchedUser.setLastName("Drumph");
        fetchedUser.setSalaryPerHour(10000000000.00);
        fetchedUser.persistData();

        // Refresh first project instance
        createdUser.refreshData();

        assertThat(createdUser, equalTo(fetchedUser));
    }

    @Test(expected = ModelNotFoundException.class)
    public void testDeleteData() throws Exception {
        // Create user
        UserModel u = new UserModel("John", "Smith", 1000.00);
        u.persistData();
        int pId = u.getUserId();

        // Delete project
        u.deleteData();

        // Attempt to fetch project
        UserModel.getById(pId);
    }

    @Test
    public void testGetAll() throws Exception {
        // Create two users
        new UserModel("FirstName", "LastName", 10.99).persistData();
        new UserModel("FirstName2", "LastName2", 100.99).persistData();

        List<UserModel> userList = UserModel.getAll();

        assertThat(userList.size(), isA(Integer.class));
        assertThat(userList.size(), not(0));
    }

    @Test
    public void testGetById() throws Exception {
        // Create user
        UserModel createdUser = new UserModel("Bobby", "Moynihan", 1000000.00);
        createdUser.persistData();
        int uId = createdUser.getUserId();

        // Fetch same user
        UserModel fetchedUser = UserModel.getById(uId);

        assertThat(fetchedUser, equalTo(createdUser));
    }

    @Test
    public void testGetLastInsertedId() throws Exception {
        // TODO
    }
}

