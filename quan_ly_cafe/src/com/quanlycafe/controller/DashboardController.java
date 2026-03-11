package com.quanlycafe.controller;

import com.quanlycafe.dao.NhanVienDAO;
import com.quanlycafe.dao.TableDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private StackPane contentPane;
    @FXML private VBox welcomePane;

    @FXML private Button btnNhanVien;
    @FXML private Button btnBan;
    @FXML private Button btnLogout;

    @FXML private Label lblSoNhanVien;
    @FXML private Label lblBanTrong;
    @FXML private Label lblBanCoKhach;

    private static final String STYLE_ACTIVE =
        "-fx-background-color:#f0c040; -fx-text-fill:#1a2634;" +
        "-fx-font-weight:bold; -fx-font-size:13;" +
        "-fx-background-radius:8; -fx-cursor:hand;";

    private static final String STYLE_NHANVIEN =
        "-fx-background-color:#2ecc71; -fx-text-fill:white;" +
        "-fx-font-weight:bold; -fx-font-size:13;" +
        "-fx-background-radius:8; -fx-cursor:hand;";

    private static final String STYLE_BAN =
        "-fx-background-color:#3498db; -fx-text-fill:white;" +
        "-fx-font-weight:bold; -fx-font-size:13;" +
        "-fx-background-radius:8; -fx-cursor:hand;";

    @FXML
    public void initialize() {
        capNhatThongKe();
    }

    private void capNhatThongKe() {
        try {
            int soNV = new NhanVienDAO().getAllNhanVien().size();
            lblSoNhanVien.setText(String.valueOf(soNV));
        } catch (Exception e) {
            lblSoNhanVien.setText("--");
        }

        try {
            var dsBan = new TableDAO().getAllTables();
            long banTrong   = dsBan.stream().filter(b -> "Trống".equals(b.getStatus())).count();
            long banCoKhach = dsBan.stream().filter(b -> "Có khách".equals(b.getStatus())).count();
            lblBanTrong.setText(String.valueOf(banTrong));
            lblBanCoKhach.setText(String.valueOf(banCoKhach));
        } catch (Exception e) {
            lblBanTrong.setText("--");
            lblBanCoKhach.setText("--");
        }
    }

    @FXML
    private void showNhanVien() {
        loadTrang("/com/quanlycafe/NhanVienView.fxml");
        btnNhanVien.setStyle(STYLE_ACTIVE);
        btnBan.setStyle(STYLE_BAN);
    }

    @FXML
    private void showBan() {
        loadTrang("/com/quanlycafe/table.fxml");
        btnBan.setStyle(STYLE_ACTIVE);
        btnNhanVien.setStyle(STYLE_NHANVIEN);
    }

    private void loadTrang(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node trang = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(trang);

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                "Không load được màn hình:\n" + fxmlPath).showAndWait();
        }
    }

    @FXML
    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Đăng xuất");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn đăng xuất?");

        confirm.showAndWait().ifPresent(kq -> {
            if (kq == javafx.scene.control.ButtonType.OK) {
                Stage stage = (Stage) btnLogout.getScene().getWindow();
                stage.close();
            }
        });
    }
}
