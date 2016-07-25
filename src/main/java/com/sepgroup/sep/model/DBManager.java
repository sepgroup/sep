package com.sepgroup.sep.model;

import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.DatabaseFactory;
import com.sepgroup.sep.db.Database;

/**
 * Created by jeremybrown on 2016-07-24.
 */
public class DBManager {
    /**
     * Creates the DB tables if they do not already exist
     * @throws DBException if the tables could not be created
     */
    public static void createDBTablesIfNotExisting() throws DBException {
        ProjectModel.createTable();
        TaskModel.createTable();
        UserModel.createTable();
    }

    /**
     * Deletes all tables & data in the database
     * USE WITH CAUTION!
     * @throws DBException if tables could not be deleted
     */
    public static void dropAllDBTables() throws DBException {
        Database db = DatabaseFactory.getActiveDB();
        db.dropTable(ProjectModel.ProjectModelDBObject.TABLE_NAME);
        db.dropTable(TaskModel.TaskModelDBObject.TABLE_NAME);
        db.dropTable(TaskModel.TaskModelDBObject.DEPENDENCIES_TABLE_NAME);
        db.dropTable(UserModel.UserModelDBObject.TABLE_NAME);
    }
}
