package com.sepgroup.sep.tests.unit.model;

import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.*;
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
        DBManager.createDBTablesIfNotExisting();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DBManager.dropAllDBTables();
    }

    @Test
    public void testEquals() throws Exception {
        // Create two projects with same data
        UserModel u1 = new UserModel("FirstName", "LastName", 10.99);
        UserModel u2 = new UserModel("FirstName", "LastName", 10.99);

        assertTrue(u1.equals(u2));
    }

    @Test
    public void testEqualsNull() throws Exception {
        UserModel u = new UserModel();
        assertThat(u.equals(null), equalTo(false));
    }

    @Test
    public void testEqualsDifferentType() throws Exception {
        UserModel u = new UserModel();
        assertThat(u.equals(new TaskModel()), equalTo(false));
    }

    @Test
    public void testEqualsDifferentUserId() throws Exception {
        UserModel u1 = new UserModel("J", "D", 20);
        u1.persistData();
        UserModel u2 = new UserModel("J", "D", 20);
        u2.persistData();

        assertThat(u1.equals(u2), equalTo(false));
    }

    @Test
    public void testEqualsDifferentFirstName() throws Exception {
        UserModel u1 = new UserModel("Ja", "Dom", 20);
        u1.persistData();
        UserModel u2 = new UserModel("Jo", "Dom", 20);
        u2.persistData();

        assertThat(u1.equals(u2), equalTo(false));
    }

    @Test
    public void testEqualsDifferentLastName() throws Exception {
        UserModel u1 = new UserModel("J", "Dom", 20);
        u1.persistData();
        UserModel u2 = new UserModel("J", "Dim", 20);
        u2.persistData();

        assertThat(u1.equals(u2), equalTo(false));
    }

    @Test
    public void testEqualsDifferentSalaryPerHour() throws Exception {
        UserModel u1 = new UserModel("J", "Dom", 20.1);
        u1.persistData();
        UserModel u2 = new UserModel("J", "Dom", 20.23);
        u2.persistData();

        assertThat(u1.equals(u2), equalTo(false));
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

    @Test(expected = DBException.class)
    public void testPersistDataNoNames() throws Exception {
        UserModel u = new UserModel();
        u.persistData();
    }

    @Test(expected = DBException.class)
    public void testPersistDataNoFirstName() throws Exception {
        UserModel u = new UserModel();
        u.setLastName("LN");
        u.persistData();
    }

    @Test(expected = DBException.class)
    public void testPersistDataNoLastName() throws Exception {
        UserModel u = new UserModel();
        u.setFirstName("FN");
        u.persistData();
    }

    @Test(expected = ModelNotFoundException.class)
    public void testCleanData() throws Exception {
        UserModel u1 = new UserModel("FN", "LN", 43.30);
        u1.persistData();
        UserModel.cleanData();

        UserModel.getAll();
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
    public void testGetEmptyUser() throws Exception {
        UserModel emptyUser = UserModel.getEmptyUser();
        assertThat(emptyUser.getFirstName(), equalTo("None"));
        assertThat(emptyUser.getLastName(), equalTo(""));
    }

    @Test
    public void testSetFirstName() throws Exception {
        UserModel u1 = new UserModel();
        u1.setFirstName("FN1");
        String expected = "FN1";
        assertThat(u1.getFirstName(), equalTo(expected));
    }

    @Test(expected = InvalidInputException.class)
    public void testSetFirstNameTooLong() throws Exception {
        UserModel u1 = new UserModel();
        u1.setFirstName("FNlksdjf;ljlkkljdsfkljslakdfjalj3290jfdsaf092j0fj03dajlfdjasf90233j0i1");
    }

    @Test
    public void testSetLastName() throws Exception {
        UserModel u1 = new UserModel();
        u1.setLastName("LN1");
        String expected = "LN1";
        assertThat(u1.getLastName(), equalTo(expected));
    }

    @Test(expected = InvalidInputException.class)
    public void testSetLastNameTooLong() throws Exception {
        UserModel u1 = new UserModel();
        u1.setLastName("FNlksdjf;ljlkkljdsfkljslakdfjalj3290jfdsaf092j0fj03dajlfdjasf90233j0i1");
    }

    @Test
    public void testGetFullName() throws Exception {
        UserModel u = new UserModel("Johnny", "Smithy", 50.43);
        String expected = "Johnny Smithy";
        String actual = u.getFullName();
        assertThat(expected, equalTo(actual));
    }

    @Test
    public void testSetSalaryPerHour() throws Exception {
        UserModel u = new UserModel();

        u.setSalaryPerHour(49.58);
        double expected1 = 49.58;
        assertThat(u.getSalaryPerHour(), equalTo(expected1));

        u.setSalaryPerHour(53.495820);
        double expected2 = 53.50;
        assertThat(u.getSalaryPerHour(), equalTo(expected2));
    }

    @Test(expected = InvalidInputException.class)
    public void testSetSalaryPerHourNegative() throws Exception {
        UserModel u = new UserModel();
        u.setSalaryPerHour(-23.9485);
    }

    @Test
    public void testToString() throws Exception {
        UserModel u = new UserModel();
        String expected1 = "[ ]    ";
        assertThat(expected1, equalTo(u.toString()));

        u.setFirstName("Bobby");
        u.setLastName("Moynihan");
        u.persistData();
        int uId = u.getUserId();
        String expected2 = "[" + uId + "] Bobby Moynihan";

    }

    @Test
    public void testGetLastInsertedId() throws Exception {
        // TODO
    }
}

