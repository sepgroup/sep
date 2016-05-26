package com.sepgroup.sep;

import com.sepgroup.sep.project.ProjectController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;

        // Window size
        Rectangle2D screenDimensions = Screen.getPrimary().getVisualBounds();
        double windowWidth = screenDimensions.getWidth() * 3 / 4;
        double windowHeight = screenDimensions.getHeight() * 3 / 4;
        primaryStage.setWidth(windowWidth);
        primaryStage.setHeight(windowHeight);

        // Start with user view
        AbstractController loginController = new ProjectController();
        setPrimaryScene(loginController);

        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryScene(AbstractController controller) throws IOException {
        Parent parent = FXMLLoader.load(controller.getClass().getResource(controller.getFxmlPath()));
        primaryStage.setScene(new Scene(parent, primaryStage.getWidth(), primaryStage.getHeight()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
