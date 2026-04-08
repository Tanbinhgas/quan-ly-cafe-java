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
    @FXML private Label lblRoleBadge;

    private String currentRole = "nhanvien";
    private String currentUser = "";

    private static final String S_NHANVIEN   = btn("#2ecc71");
    private static final String S_BAN        = btn("#3498db");
    private static final String S_MENU       = btn("#9b59b6");
    private static final String S_NGUYENLIEU = btn("#16a085");
    private static final String S_LICHSU     = btn("#e67e22");
    private static final String S_ACTIVE     =
        "-fx-background-color:#f0c040; -fx-text-fill:#1a2634;" +
        "-fx-font-weight:bold; -fx-font-size:13;" +
        "-fx-background-radius:8; -fx-cursor:hand; -fx-padding:0 18 0 18;";
    private static final String S_DISABLED   =
        "-fx-background-color:#4a5568; -fx-text-fill:#718096;" +
        "-fx-font-weight:bold; -fx-font-size:13;" +
        "-fx-background-radius:8; -fx-padding:0 18 0 18;" +
        "-fx-cursor:default; -fx-opacity:0.5;";

    private static String btn(String color) {
        return "-fx-background-color:" + color + "; -fx-text-fill:white;" +
               "-fx-font-weight:bold; -fx-font-size:13;" +
               "-fx-background-radius:8; -fx-cursor:hand; -fx-padding:0 18 0 18;";
    }

    public void setRole(String role, String username) {
        this.currentRole = role;
        this.currentUser = username;
        apDungPhanQuyen();
        capNhatThongKe();
    }

    @FXML
    public void initialize() {
    }

    private void apDungPhanQuyen() {
        boolean isAdmin = "admin".equals(currentRole);

        if (lblRoleBadge != null) {
            if (isAdmin) {
                lblRoleBadge.setText("👑 " + currentUser + " (Admin)");
                lblRoleBadge.setStyle(
                    "-fx-text-fill:#f0c040; -fx-font-weight:bold;" +
                    "-fx-font-size:12; -fx-background-color:#2c3e50;" +
                    "-fx-padding:4 12 4 12; -fx-background-radius:20;");
            } else {
                lblRoleBadge.setText("👤 " + currentUser + " (Nhân viên)");
                lblRoleBadge.setStyle(
                    "-fx-text-fill:white; -fx-font-size:12;" +
                    "-fx-background-color:#3d5166;" +
                    "-fx-padding:4 12 4 12; -fx-background-radius:20;");
            }
        }

        if (!isAdmin) {
            btnNhanVien.setVisible(false);
            btnNhanVien.setManaged(false);
            btnLichSu.setVisible(false);
            btnLichSu.setManaged(false);
        }
    }

    private void capNhatThongKe() {
        boolean isAdmin = "admin".equals(currentRole);

        if (isAdmin) {
            try { lblSoNhanVien.setText(
                String.valueOf(new NhanVienDAO().getAllNhanVien().size()));
            } catch (Exception e) { lblSoNhanVien.setText("--"); }
        } else {
            lblSoNhanVien.setText("🔒");
        }

        try {
            var ds = new TableDAO().getAllTables();
            lblBanTrong.setText(String.valueOf(
                ds.stream().filter(b -> "Trống".equals(b.getStatus())).count()));
            lblBanCoKhach.setText(String.valueOf(
                ds.stream().filter(b -> "Có khách".equals(b.getStatus())).count()));
        } catch (Exception e) {
            lblBanTrong.setText("--"); lblBanCoKhach.setText("--");
        }

        try { lblKhoTonThap.setText(
            String.valueOf(new NguyenLieuDAO().getNguyenLieuTonThap().size()));
        } catch (Exception e) { lblKhoTonThap.setText("--"); }
    }

    @FXML
    private void showNhanVien() {
        if (!isAdmin()) return;
        reset(); btnNhanVien.setStyle(S_ACTIVE);
        load("/com/quanlycafe/NhanVienView.fxml");
    }

    @FXML private void showMenu()       { reset(); btnMenu.setStyle(S_ACTIVE);       load("/com/quanlycafe/MenuView.fxml"); }
    @FXML private void showNguyenLieu() { reset(); btnNguyenLieu.setStyle(S_ACTIVE); load("/com/quanlycafe/NguyenLieuView.fxml"); }

    @FXML
    private void showBan() {
        reset(); btnBan.setStyle(S_ACTIVE);
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
        if (!isAdmin()) return;
        reset(); btnLichSu.setStyle(S_ACTIVE);
        try {
            MenuController mc = new MenuController();
            mc.xemLichSu();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không mở được lịch sử!").showAndWait();
        }
        reset();
    }

    private boolean isAdmin() {
        return "admin".equals(currentRole);
    }

    private void reset() {
        if (isAdmin()) btnNhanVien.setStyle(S_NHANVIEN);
        btnBan.setStyle(S_BAN);
        btnMenu.setStyle(S_MENU);
        btnNguyenLieu.setStyle(S_NGUYENLIEU);
        if (isAdmin()) btnLichSu.setStyle(S_LICHSU);
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
            if (bt == ButtonType.OK) {
                try {
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/quanlycafe/Login.fxml"));
                    Node root = loader.load();
                    Stage stage = (Stage) btnLogout.getScene().getWindow();
                    stage.setScene(new javafx.scene.Scene(
                        (javafx.scene.Parent) root, 1280, 720));
                    stage.setTitle("Cafe Manager — Đăng nhập");
                } catch (Exception e) { e.printStackTrace(); }
            }
        });
    }
}
