package com.sepgroup.sep.tests.ut.db;

import com.sepgroup.sep.db.DBConfig;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jeremybrown on 2016-05-22.
 */
public class DBConfigTest {

    private static String expectedActiveDBPath = "test.db";

    @BeforeClass
    public static void setUpBeforeMethod() throws Exception {
        ConfigFactory.setProperty("configPath", DBConfigTest.class.getResource(File.separator + "test-db.properties").getFile());
    }

    @Test
    public void testGetActiveDbPath() {
        DBConfig c = ConfigFactory.create(DBConfig.class);
        String activeDBPath = c.activeDbPath();

        assertThat(activeDBPath, equalTo(expectedActiveDBPath));
    }
}
