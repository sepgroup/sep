package com.sepgroup.sep.controller;


import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.Observer;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.AbstractModel;
import com.sepgroup.sep.model.ProjectModel;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jeremybrown on 2016-05-17.
 */
public abstract class AbstractController implements Observer {

    private static Logger logger = LoggerFactory.getLogger(AbstractController.class);

    private String fxmlPath;
    
    private String CssPath;

    protected AbstractModel model;

    private Scene previousScene;

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
     * Gets the path to the view's associated CSS file
     * @return the path to the view's associated CSS file
     */
    public String getCssPath() {
        return CssPath;
    }
    
    /**
     * Sets the path to the view's associated CSS file
     * @param css the path to the view's associated CSS file
     */
    
    public void setCssPath(String css) {
        this.CssPath = css;
    }

    /**
     *
     * @return
     */
    public Scene getPreviousScene() {
        return previousScene;
    }

    /**
     *
     * @param previousScene
     */
    public void setPreviousScene(Scene previousScene) {
        this.previousScene = previousScene;
    }

    /**
     * Instructs the model to reload its data from the database
     */
    public void refreshModel() {
        try {
            model.refreshData();
        } catch (ModelNotFoundException e) {
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
