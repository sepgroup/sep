package com.sepgroup.sep;

import java.sql.ResultSet;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
    
    
    public static void main(String[] args) {
        launch(args);
		// TODO Auto-generated method stub
		Database db= new Database();
		project p= new project("project test", "2015-05-23", "2015-06-25", 2500, false);
		System.out.println(p.toString());
		String sql= "SELECT * FROM Project;";
		System.out.println(project.findBySql(sql));
    }
}
