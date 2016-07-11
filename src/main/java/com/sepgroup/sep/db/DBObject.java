package com.sepgroup.sep.db;

import com.sepgroup.sep.model.AbstractModel;
import com.sepgroup.sep.model.InvalidInputException;
import com.sepgroup.sep.model.ModelNotFoundException;

import java.util.List;

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

    List<? extends AbstractModel> findAll() throws ModelNotFoundException, InvalidInputException;

    AbstractModel findById(int i) throws ModelNotFoundException, InvalidInputException;

    /**
     * Gets the result for the specified SQL query from the database
     * @param s
     * @return
     * @throws DBException
     */
    List<? extends AbstractModel> findBySql(String s) throws ModelNotFoundException, InvalidInputException;

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

    /**
     * Clean the table (remove all tuples from table)
     * @throws DBException
     */
    void clean() throws DBException;

    /**
     * Create the table (it is used to create a table when it is not exist)
     * @throws DBException
     */
    void createTable() throws DBException;

    /**
     * Drops the table from the database
     * USE WITH CAUTION!
     * @throws DBException
     */
    void dropTable() throws DBException;
}
