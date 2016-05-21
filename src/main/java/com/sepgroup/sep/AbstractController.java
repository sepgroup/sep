package com.sepgroup.sep;


import com.sepgroup.sep.db.DBException;

/**
 * Created by jeremybrown on 2016-05-17.
 */
public abstract class AbstractController implements Observer {

    private String fxmlPath;

    private AbstractModel model;

    protected void setFxmlPath(String fxml) {
        this.fxmlPath = fxml;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public void refreshModel() throws DBException {
        model.refreshData();
    }

    public void persistModel() throws DBException {
        model.persistData();
    }
}
