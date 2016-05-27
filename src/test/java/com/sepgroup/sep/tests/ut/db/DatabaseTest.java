package com.sepgroup.sep.tests.ut.db;

import com.sepgroup.sep.db.Database;
import org.aeonbits.owner.ConfigFactory;
import org.junit.*;

import java.sql.ResultSet;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jeremybrown on 2016-05-21.
 */
public class DatabaseTest {

    private static String dbPath = "test.db";

    private static Database db;

    @BeforeClass
    public static void setUpBeforeMethod() throws Exception {
        ConfigFactory.setProperty("configPath", DatabaseTest.class.getResource("/test-db.properties").getFile());
    }

    @After
    public void tearDownAfterMethod() throws Exception {
        if (db != null) {
            db.closeConnection();
        }
    }

    @Test
    public void testGetDB() throws Exception {
        db = Database.getDB(dbPath);

        assertThat(db, notNullValue());
        assertThat(db.getDbPath(), endsWith("/test.db"));
    }

    @Test
    public void testGetActiveDB() throws Exception {
        db = Database.getActiveDB();
        assertThat(db, notNullValue());
        assertThat(db.getDbPath(), endsWith("/test.db"));
    }

    @Test
    public void testDBInsert() throws Exception {
        db = Database.getDB(dbPath);
        String sql = "INSERT INTO Task (TaskName, Description, PID) VALUES ('T1', 'D1', 11);";
        int insertedTaskKey = db.insert(sql);

        ResultSet rs = db.query("SELECT * FROM Task WHERE TID=" + insertedTaskKey);

        assertThat(rs.next(), equalTo(true));
    }

    @Test
    public void testDBQuery() throws Exception {
        db = Database.getDB(dbPath);
        String sql = "INSERT INTO Project (ProjectName) VALUES ('P1');";
        int insertedTaskKey = db.insert(sql);

        ResultSet rs = db.query("SELECT * FROM Project WHERE PID=" + insertedTaskKey);

        assertThat(rs.next(), equalTo(true));
    }

    @Test
    public void testDBUpdate() throws Exception{
        db = Database.getDB(dbPath);
        String sql = "INSERT INTO Task (TaskName, Description, PID) VALUES ('T3', 'D3', 33);";
        int insertedTaskKey = db.insert(sql);

        db.update("UPDATE Task SET Description='D33', PID=333 WHERE TID=" + insertedTaskKey);
        ResultSet rs = db.query("SELECT * FROM Task WHERE TID=" + insertedTaskKey);

        assertThat(rs.next(), equalTo(true));
        assertThat(rs.getString("Description"), equalTo("D33"));
        assertThat(rs.getInt("PID"), equalTo(333));
    }

    @Test
    public void testDBDelete() throws Exception {
        db = Database.getDB(dbPath);
        String sql = "INSERT INTO Task (TaskName, Description, PID) VALUES ('T1', 'D1', 25);";
        int insertedTaskKey = db.insert(sql);
        db.update("DELETE FROM Task WHERE TID=" + insertedTaskKey);
        ResultSet rs = db.query("SELECT * FROM Task WHERE TID=" + insertedTaskKey);

        assertThat(rs.next(), equalTo(false));
    }
}
