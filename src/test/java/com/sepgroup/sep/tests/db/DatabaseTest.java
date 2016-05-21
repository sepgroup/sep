package com.sepgroup.sep.tests.db;

import com.sepgroup.sep.db.Database;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by jeremybrown on 2016-05-21.
 */
public class DatabaseTest {

    @Test
    public void testGetActiveDB() {
        Database db = Database.getActiveDB();

        assertNotNull(db);
    }

    @Test
    public void testGetDB() {
        String dbPath = "sqlite/testDB.db";
        Database db = Database.getDB(dbPath);

        assertNotNull(db);
    }

    @Ignore
    @Test
    public void testDBInsert() {
        // TODO
    }

    @Ignore
    @Test
    public void testDBQuery() {
        // TODO
    }

    @Ignore
    @Test
    public void testDBUpdate() {
        // TODO
    }
}
