package com.quanlycafe;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {  
    
    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Xin chào! Đây là Phần mềm Quản lý Quán Cafe");

        StackPane root = new StackPane();
        root.getChildren().add(label);

        Scene scene = new Scene(root, 1600, 900);

        primaryStage.setTitle("Quản lý Quán Cafe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
