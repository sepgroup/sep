package com.sepgroup.sep;

import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.model.UserModel;
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

    /**
     * Creates the DB tables if they do not already exist
     * @throws DBException if the tables could not be created
     */
    public static void createDBTablesIfNotExisting() throws DBException {
        UserModel.createTable();
        ProjectModel.createTable();
        TaskModel.createTable();
    }

    /**
     * Deletes all tables & data in the database
     * USE WITH CAUTION!
     * @throws DBException if tables could not be deleted
     */
    public static void dropAllDBTables() throws DBException {
        Database db = Database.getActiveDB();
        db.dropTable(ProjectModel.ProjectModelDBObject.TABLE_NAME);
        db.dropTable(TaskModel.TaskModelDBObject.TABLE_NAME);
        db.dropTable(TaskModel.TaskModelDBObject.DEPENDENCIES_TABLE_NAME);
        db.dropTable(UserModel.UserModelDBObject.TABLE_NAME);
    }

}
