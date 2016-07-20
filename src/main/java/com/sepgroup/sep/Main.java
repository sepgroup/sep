package com.sepgroup.sep;

import com.sepgroup.sep.controller.AbstractController;
import com.sepgroup.sep.controller.DialogCreator;
import com.sepgroup.sep.controller.WelcomeController;
import com.sepgroup.sep.db.DBException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static int screenMinHeight = 560;
    private static int screenMinWidth = 1100;
    private static int screenMaxHeight = 800;
    private static int screenMaxWidth = 1500;

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create Db tables if needed
        try {
            SepUserStorage.createDBTablesIfNotExisting();
        } catch (DBException e) {
            logger.error("Unable to create database", e);
            DialogCreator.showErrorDialog("Error creating database", "Unable to create database, ensure you have " +
                    "access to your home folder.");
        }

        // Create main scene
        Main.primaryStage = primaryStage;

        primaryStage.setTitle("Project Management Application");

        setPrimaryScene(WelcomeController.getFxmlPath());
        primaryStage.setMaxHeight(screenMaxHeight);
        primaryStage.setMaxWidth(screenMaxWidth);
        
        primaryStage.setMinHeight(screenMinHeight);
        primaryStage.setMinWidth(screenMinWidth);


        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static AbstractController setPrimaryScene(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            DialogCreator.showExceptionDialog(e);
            DialogCreator.showErrorDialog("An error has occurred", e.getMessage());
        }
        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(parent));
        } else {
            primaryStage.getScene().setRoot(parent);
        }
        AbstractController actualController = loader.getController();
        parent.getStylesheets().add(actualController.getClass().getResource(actualController.getCssPath()).toExternalForm());

        return actualController;
    }

    public static void main(String[] args) {
        launch(args);

      
    }
}