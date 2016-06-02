package com.sepgroup.sep;

import com.sepgroup.sep.controller.AbstractController;
import com.sepgroup.sep.controller.WelcomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;

       // Window size

        primaryStage.setTitle("Project Management Application");

        AbstractController loginController = new WelcomeController();
        setPrimaryScene(loginController);
        primaryStage.setMaxHeight(420);
        primaryStage.setMaxWidth(500);
        
        primaryStage.setMinHeight(420);
        primaryStage.setMinWidth(500);
        

        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryScene(AbstractController controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(controller.getFxmlPath()));
        loader.setController(controller);
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        parent.getStylesheets().add(controller.getClass().getResource(controller.getCssPath()).toExternalForm());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
