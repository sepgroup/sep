package com.sepgroup.sep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jeremybrown on 2016-06-28.
 */
public class SepUserStorage {

    private static Logger logger = LoggerFactory.getLogger(SepUserStorage.class);

    private static String homeDir = System.getProperty("user.home");
    private static Path userStoragePath = Paths.get(homeDir, ".sep");

    /**
     * Private constructor to hide implicit public one
     */
    private SepUserStorage() {}

    /**
     * Get the local user storage path
     * @return the path to the local user storage
     */
    public static Path getPath() {
        if (!Files.isDirectory(userStoragePath)) {
            try {
                Files.deleteIfExists(userStoragePath);
            } catch (IOException e) {
                logger.error("Unable to delete existing file at " + userStoragePath);
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
            logger.error("Unable to create user storage");
        }
    }

}
