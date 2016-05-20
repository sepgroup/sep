package com.sepgroup.sep.db;

import com.sepgroup.sep.AbstractModel;

import java.sql.ResultSet;

/**
 * Created by jeremybrown on 2016-05-20.
 */
public interface DBObject {

    /**
     * Get the database table name for this entity
     * @return the table name of this entity in the database
     */
    String getTableName();

    int getLastInsertedId() throws DBException;

    ResultSet findById(int i) throws DBException;

    /**
     * Gets the result for the specified SQL query from the database
     * @param s
     * @return
     * @throws DBException
     */
    ResultSet findBySql(String s) throws DBException;

    /**
     * Creates the entity in the database
     * @return the database PK (the ID) of the created entity
     * @throws DBException if the operation failed
     */
    int create() throws DBException;

    /**
     * Updates the entity in the database
     * @throws DBException if the operation failed
     */
    void update() throws DBException;

    /**
     * Deletes the entity from the database
     * @throws DBException if the operation failed
     */
    void delete() throws DBException;

}
