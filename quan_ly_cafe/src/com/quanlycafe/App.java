package com.quanlycafe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            var resource = getClass().getResource("NhanVienView.fxml");
            System.out.println("Đường dẫn FXML: " + resource);

            if (resource == null) {
                throw new RuntimeException("Không tìm thấy file FXML");
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 720);
            primaryStage.setTitle("Quản lý Quán Cafe");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi load FXML: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
