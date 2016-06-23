package com.sepgroup.sep;

import com.sepgroup.sep.controller.AbstractController;
import com.sepgroup.sep.controller.DialogCreator;
import com.sepgroup.sep.controller.GanttController;
import com.sepgroup.sep.controller.WelcomeController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main extends Application {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create main scene
        Main.primaryStage = primaryStage;


       // Window size

        primaryStage.setTitle("Project Management Application");

        setPrimaryScene(WelcomeController.getFxmlPath());
        primaryStage.setMaxHeight(800);
        primaryStage.setMaxWidth(1000);
        
        primaryStage.setMinHeight(560);
        primaryStage.setMinWidth(810);
        

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
