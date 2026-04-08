package com.quanlycafe.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField     txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Label         lblMessage;

    private static final String[][] ACCOUNTS = {
        {"admin",    "123", "admin"},
        {"nhanvien", "123", "nhanvien"},
    };

    @FXML
    private void login() {
        String user = txtUser.getText().trim();
        String pass = txtPass.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            lblMessage.setText("Vui lòng nhập tài khoản và mật khẩu!");
            return;
        }

        String role = null;
        for (String[] acc : ACCOUNTS) {
            if (acc[0].equals(user) && acc[1].equals(pass)) {
                role = acc[2];
                break;
            }
        }

        if (role == null) {
            lblMessage.setText("Sai tài khoản hoặc mật khẩu!");
            lblMessage.setStyle("-fx-text-fill:#e74c3c; -fx-font-size:12;");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/quanlycafe/Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dc = loader.getController();
            dc.setRole(role, user);

            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 720));
            stage.setTitle("Cafe Manager — " +
                ("admin".equals(role) ? "Quản trị viên" : "Nhân viên"));

        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @FXML
    private void exit() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Thoát chương trình");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn thoát?");
        confirm.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK)
                ((Stage) txtUser.getScene().getWindow()).close();
        });
    }
}
