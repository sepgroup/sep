package com.sepgroup.sep.tests.ut.db;

import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.db.DatabaseFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jeremybrown on 2016-07-25.
 */
public class DatabaseFactoryTest {

    private static String dbPath = "test.db";

    @Test
    public void testGetDB() throws Exception {
        Database db = DatabaseFactory.getDB(dbPath);

        assertThat(db, notNullValue());
        assertThat(db.getDbPath(), endsWith(dbPath));
    }

    @Test
    public void testGetActiveDB() throws Exception {
        Database db = DatabaseFactory.getActiveDB();

        assertThat(db, notNullValue());
        assertThat(db.getDbPath(), endsWith(dbPath));
    }

}
