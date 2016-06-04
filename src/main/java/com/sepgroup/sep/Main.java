package com.sepgroup.sep;

import com.sepgroup.sep.controller.AbstractController;
import com.sepgroup.sep.controller.DialogCreator;
import com.sepgroup.sep.controller.WelcomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main extends Application {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;

       // Window size

        primaryStage.setTitle("Project Management Application");

        setPrimaryScene(new WelcomeController());
        primaryStage.setMaxHeight(800);
        primaryStage.setMaxWidth(1200);
        
        primaryStage.setMinHeight(420);
        primaryStage.setMinWidth(500);
        

        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static AbstractController setPrimaryScene(AbstractController controller) {
        FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(controller.getFxmlPath()));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            DialogCreator.showExceptionDialog(e);
            DialogCreator.showErrorDialog("Error", "An error has occurred", e.getMessage());
        }

        primaryStage.setScene(new Scene(parent));
        parent.getStylesheets().add(controller.getClass().getResource(controller.getCssPath()).toExternalForm());

        AbstractController actualController = loader.getController();
        return actualController;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
