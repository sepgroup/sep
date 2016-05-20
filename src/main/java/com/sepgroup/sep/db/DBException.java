package com.sepgroup.sep.db;

/**
 * Created by jeremybrown on 2016-05-20.
 */
public class DBException extends Exception {

    public DBException(String message) {
        super(message);
    }

    public DBException(Throwable cause) {
        super(cause);
    }
}
