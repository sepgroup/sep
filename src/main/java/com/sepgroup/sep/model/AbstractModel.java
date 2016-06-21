package com.sepgroup.sep.model;

import com.sepgroup.sep.Observable;
import com.sepgroup.sep.db.DBException;

/**
 * Created by jeremybrown on 2016-05-18.
 */
public abstract class AbstractModel extends Observable {

    public abstract void refreshData() throws ModelNotFoundException;

    public abstract void persistData() throws DBException;

    public abstract void deleteData() throws DBException;

    protected boolean equalsNullable(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;  // equal if both are null
        }
        else if (obj1 == null || obj2 == null) {
            return false;  // not equal if only one is null
        }
        else if (obj1.equals(obj2)) {
            return true;  // equal if both are equal
        }
        else {
            return false;  // not equal otherwise
        }
    }

}
