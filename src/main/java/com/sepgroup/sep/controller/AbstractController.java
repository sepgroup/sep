package com.sepgroup.sep.controller;


import com.sepgroup.sep.model.Observer;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jeremybrown on 2016-05-17.
 */
public abstract class AbstractController implements Observer {

    private static Logger logger = LoggerFactory.getLogger(AbstractController.class);

    private String CssPath;

    private Scene previousScene;

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

}
