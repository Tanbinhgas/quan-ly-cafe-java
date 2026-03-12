package com.quanlycafe.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUser;

    @FXML
    private PasswordField txtPass;

    @FXML
    private Label lblMessage;

    @FXML
    private void login() {

        String user = txtUser.getText();
        String pass = txtPass.getText();

        if(user.isEmpty() || pass.isEmpty()){
    lblMessage.setText("Vui lòng nhập tài khoản và mật khẩu");
    return;
}

        if(user.equals("admin") && pass.equals("123")){

            try{

                Parent root = FXMLLoader.load(
                        getClass().getResource("/com/quanlycafe/Dashboard.fxml")
                );

                Stage stage = (Stage) txtUser.getScene().getWindow();

                stage.setScene(new Scene(root,1280,720));
                stage.setTitle("Cafe Manager");

            }catch(Exception e){
                e.printStackTrace();
            }

        }else{
            lblMessage.setText("Sai tài khoản hoặc mật khẩu");
        }
    }
    @FXML
    private void exit() {

    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setTitle("Thoát chương trình");
    confirm.setHeaderText(null);
    confirm.setContentText("Bạn có chắc muốn thoát?");

    confirm.showAndWait().ifPresent(rs -> {
        if(rs == ButtonType.OK){
            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.close();
        }
    });
}
}
