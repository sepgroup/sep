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
        if (!(obj1 == null && obj2 == null) && // Equal if both are null
                obj1 == null || obj2 == null ||  // Not equal if 1 / 2 null
                !obj1.equals(obj2)) { // Not equal if contents not equal
            return false;
        }
        else {
            return true;
        }
    }

}
