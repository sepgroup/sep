package com.sepgroup.sep.model;

/**
 * Created by jeremybrown on 2016-05-20.
 */
public class ModelNotFoundException extends Exception {

    public ModelNotFoundException(String message) {
        super(message);
    }

    public ModelNotFoundException(Throwable cause) {
        super(cause);
    }

    public ModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
