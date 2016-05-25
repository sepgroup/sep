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

    /**
     * Sets the path to the view's associated FXML file
     * @param fxml the path to the view's associated FXML file
     */
    protected void setFxmlPath(String fxml) {
        this.fxmlPath = fxml;
    }

    /**
     * Gets the path to the view's associated FXML file
     * @return the path to the view's associated FXML file
     */
    public String getFxmlPath() {
        return fxmlPath;
    }

    /**
     * Instructs the model to reload its data from the database
     */
    public void refreshModel() {
        try {
            model.refreshData();
        } catch (DBException e) {
            logger.error("Unable to refresh model data", e);
        }
    }

    /**
     * Instructs the model to persist its data to the database
     */
    public void persistModel() {
        try {
            model.persistData();
        } catch (DBException e) {
            logger.error("Unable to persist model data", e);
        }
    }
}
