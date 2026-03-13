package com.quanlycafe.controller;

import com.quanlycafe.dao.NguyenLieuDAO;
import com.quanlycafe.dao.NhanVienDAO;
import com.quanlycafe.dao.TableDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private StackPane contentPane;
    @FXML private VBox      welcomePane;

    @FXML private Button btnNhanVien;
    @FXML private Button btnBan;
    @FXML private Button btnMenu;
    @FXML private Button btnNguyenLieu;
    @FXML private Button btnLichSu;
    @FXML private Button btnLogout;

    @FXML private Label lblSoNhanVien;
    @FXML private Label lblBanTrong;
    @FXML private Label lblBanCoKhach;
    @FXML private Label lblKhoTonThap;

    private static final String S_NHANVIEN   = btn("#2ecc71");
    private static final String S_BAN        = btn("#3498db");
    private static final String S_MENU       = btn("#9b59b6");
    private static final String S_NGUYENLIEU = btn("#16a085");
    private static final String S_LICHSU     = btn("#e67e22");
    private static final String S_ACTIVE     =
        "-fx-background-color:#f0c040; -fx-text-fill:#1a2634;" +
        "-fx-font-weight:bold; -fx-font-size:13;" +
        "-fx-background-radius:8; -fx-cursor:hand; -fx-padding:0 18 0 18;";

    private static String btn(String color) {
        return "-fx-background-color:" + color + "; -fx-text-fill:white;" +
               "-fx-font-weight:bold; -fx-font-size:13;" +
               "-fx-background-radius:8; -fx-cursor:hand; -fx-padding:0 18 0 18;";
    }

    @FXML
    public void initialize() { capNhatThongKe(); }

    private void capNhatThongKe() {
        try { lblSoNhanVien.setText(
            String.valueOf(new NhanVienDAO().getAllNhanVien().size()));
        } catch (Exception e) { lblSoNhanVien.setText("--"); }

        try {
            var ds = new TableDAO().getAllTables();
            lblBanTrong.setText(String.valueOf(
                ds.stream().filter(b -> "Trống".equals(b.getStatus())).count()));
            lblBanCoKhach.setText(String.valueOf(
                ds.stream().filter(b -> "Có khách".equals(b.getStatus())).count()));
        } catch (Exception e) { lblBanTrong.setText("--"); lblBanCoKhach.setText("--"); }

        try { lblKhoTonThap.setText(
            String.valueOf(new NguyenLieuDAO().getNguyenLieuTonThap().size()));
        } catch (Exception e) { lblKhoTonThap.setText("--"); }
    }

    @FXML private void showNhanVien()   { reset(); btnNhanVien.setStyle(S_ACTIVE);   load("/com/quanlycafe/NhanVienView.fxml"); }
    @FXML private void showMenu()       { reset(); btnMenu.setStyle(S_ACTIVE);       load("/com/quanlycafe/MenuView.fxml"); }
    @FXML private void showNguyenLieu() { reset(); btnNguyenLieu.setStyle(S_ACTIVE); load("/com/quanlycafe/NguyenLieuView.fxml"); }

    @FXML
    private void showBan() {
        reset();
        btnBan.setStyle(S_ACTIVE);
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/quanlycafe/table.fxml"));
            Node trang = loader.load();
            TableController tableCtrl = loader.getController();
            tableCtrl.setContentPane(contentPane, () -> showBan());
            contentPane.getChildren().setAll(trang);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không load được table.fxml").showAndWait();
        }
    }

    @FXML
    private void showLichSu() {
        reset();
        btnLichSu.setStyle(S_ACTIVE);
        try {
            MenuController mc = new MenuController();
            mc.xemLichSu();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không mở được lịch sử!").showAndWait();
        }
        reset();
    }

    private void reset() {
        btnNhanVien.setStyle(S_NHANVIEN);
        btnBan.setStyle(S_BAN);
        btnMenu.setStyle(S_MENU);
        btnNguyenLieu.setStyle(S_NGUYENLIEU);
        btnLichSu.setStyle(S_LICHSU);
    }

    private void load(String path) {
        try {
            Node trang = FXMLLoader.load(getClass().getResource(path));
            contentPane.getChildren().setAll(trang);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không load được:\n" + path).showAndWait();
        }
    }

    @FXML
    private void logout() {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setHeaderText(null);
        c.setContentText("Bạn có chắc muốn đăng xuất?");
        c.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK)
                ((Stage) btnLogout.getScene().getWindow()).close();
        });
    }
}
