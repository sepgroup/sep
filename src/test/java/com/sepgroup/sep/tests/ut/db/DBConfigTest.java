package com.sepgroup.sep.tests.ut.db;

import com.sepgroup.sep.db.DBConfig;
import org.aeonbits.owner.ConfigFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jeremybrown on 2016-05-22.
 */
public class DBConfigTest {

    private static String expectedActiveDBPath = "test.db";

    @Test
    public void testGetActiveDbPath() {
        DBConfig c = ConfigFactory.create(DBConfig.class);
        String activeDBPath = c.activeDbPath();

        assertThat(activeDBPath, equalTo(expectedActiveDBPath));
    }
}
