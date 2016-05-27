package com.sepgroup.sep;

import com.sepgroup.sep.db.DBException;

/**
 * Created by jeremybrown on 2016-05-18.
 */
public abstract class AbstractModel extends Observable {

    public abstract void refreshData() throws ModelNotFoundException;

    public abstract void persistData() throws DBException;

    public abstract void deleteData() throws DBException;

}
