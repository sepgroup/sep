package com.sepgroup.sep.tests.unit;

import com.sepgroup.sep.SepUserStorage;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;

/**
 * Created by jeremybrown on 2016-07-24.
 */
public class SepUserStorageTest {

    @Test
    public void testGetPath() throws Exception {
        String homeDir = System.getProperty("user.home");
        Path expectedPath = Paths.get(homeDir, ".sep");

        Path actualPath = SepUserStorage.getPath();

        assertThat(actualPath, equalTo(expectedPath));
        assertTrue(actualPath.toFile().exists());
        assertTrue(actualPath.toFile().isDirectory());
    }
}
