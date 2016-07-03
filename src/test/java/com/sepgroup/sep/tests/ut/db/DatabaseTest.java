package com.sepgroup.sep.tests.ut.db;

import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.model.ProjectModel;
import org.aeonbits.owner.ConfigFactory;
import org.junit.*;

import java.io.File;
import java.nio.file.Paths;
import java.sql.ResultSet;

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

    private static String expectedProjectName = "P1";

    private static String projectTableName = ProjectModel.ProjectModelDBObject.TABLE_NAME;
    private static String projectIDColumn = ProjectModel.ProjectModelDBObject.PROJECT_ID_COLUMN;
    private static String projectNameColumn = ProjectModel.ProjectModelDBObject.PROJECT_NAME_COLUMN;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ConfigFactory.setProperty("configPath", DatabaseTest.class.getResource(File.separator + "test-db.properties").getFile());

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
        assertThat(db.getDbPath(), endsWith(dbPath));
    }

    @Test
    public void testGetActiveDB() throws Exception {
        db = Database.getActiveDB();

        assertThat(db, notNullValue());
        assertThat(db.getDbPath(), endsWith(dbPath));
    }

    @Test
    public void testDBInsertAndQuery() throws Exception {
        int insertedKey = insertProject();
        ResultSet rs = db.query("SELECT * FROM " + projectTableName + " WHERE " + projectIDColumn + "=" + insertedKey);

        assertThat(rs.next(), equalTo(true));

        String actualProjectName = rs.getString(projectNameColumn);
        assertThat(expectedProjectName, equalTo(actualProjectName));
    }

    @Test
    public void testDBUpdate() throws Exception {
        int insertedKey = insertProject();

        String updatedProjectName = "PPP111";
        db.update("UPDATE " + projectTableName + " SET " + projectNameColumn + "='" + updatedProjectName +
                "' WHERE " + projectIDColumn + "=" + insertedKey);

        ResultSet rs = db.query("SELECT * FROM " + projectTableName + " WHERE " + projectIDColumn + "=" + insertedKey);

        String actualProjectName = rs.getString(projectNameColumn);
        assertThat(actualProjectName, equalTo(updatedProjectName));
    }

    @Test
    public void testDBDelete() throws Exception {
        createTables();
        int insertedKey = insertProject();

        db.update("DELETE FROM " + projectTableName + " WHERE " + projectIDColumn + "=" + insertedKey);

        ResultSet rs = db.query("SELECT * FROM " + projectTableName + " WHERE " + projectIDColumn + "=" + insertedKey);
        assertThat(rs.next(), equalTo(false));
    }

    @Test
    public void testDBClean() throws Exception {
        createTables();
        int insertedKey = insertProject();

        db.clean();

        ResultSet rs = db.query("SELECT * FROM " + projectTableName + " WHERE " + projectIDColumn + "=" + insertedKey);
        assertThat(rs.next(), equalTo(false));
    }

    @Test
    public void testDBCreate() throws Exception {
        db = Database.getDB(dbPath);
        db.dropTable(projectTableName);
        db.createTables();

        int insertedKey = insertProject();

        ResultSet rs = db.query("SELECT * FROM " + projectTableName + " WHERE " + projectIDColumn + "=" + insertedKey);
        assertThat(rs.next(), equalTo(true));
    }

    private void createTables() throws Exception {
        db = Database.getDB(dbPath);
        db.createTables();
    }

    private int insertProject() throws Exception {
        db = Database.getDB(dbPath);
        String sql = "INSERT INTO Project (" + projectNameColumn + ") VALUES ('" + expectedProjectName + "');";
        return db.insert(sql);
    }
}
