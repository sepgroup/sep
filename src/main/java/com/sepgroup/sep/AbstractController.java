package com.sepgroup.sep;


import com.sepgroup.sep.db.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jeremybrown on 2016-05-17.
 */
public abstract class AbstractController implements Observer {

    private static Logger logger = LoggerFactory.getLogger(AbstractController.class);

    private String fxmlPath;

    private AbstractModel model;

    protected void setFxmlPath(String fxml) {
        this.fxmlPath = fxml;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public void refreshModel() {
        try {
            model.refreshData();
        } catch (DBException e) {
            logger.error("Unable to refresh model data", e);
        }
    }

    public void persistModel() {
        try {
            model.persistData();
        } catch (DBException e) {
            logger.error("Unable to persist model data", e);
        }
    }
}
