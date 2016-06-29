package com.sepgroup.sep;

import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jeremybrown on 2016-06-28.
 */
public class SepUserStorage {

    private static String homeDir = System.getProperty("user.home");
    private static Path userStoragePath = Paths.get(homeDir, ".sep");

    public static Path getPath() {
        if (!Files.isDirectory(userStoragePath)) {
            try {
                Files.deleteIfExists(userStoragePath);
            } catch (IOException e) {
                // Unable to delete existing file $HOME$/.sep
            }
        }

        if (Files.notExists(userStoragePath)) {
            createSepUserStorageDirectory();
        }

        return userStoragePath;
    }

    private static void createSepUserStorageDirectory() {
        try {
            Files.createDirectories(userStoragePath);
        } catch (IOException e) {
            // Unable to create user storage
        }
    }

    public static void createDBTablesIfNotExisting() throws DBException {
        Database db = Database.getActiveDB();
        db.createTables();
    }
}
