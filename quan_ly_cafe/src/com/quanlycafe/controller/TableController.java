package com.quanlycafe.controller;

import com.quanlycafe.dao.TableDAO;
import com.quanlycafe.model.Table;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class TableController {

    @FXML
    private GridPane tableGrid;

    private final TableDAO tableDAO = new TableDAO();

    private StackPane contentPane;
    private Runnable  onQuayLaiBan;

    public void setContentPane(StackPane contentPane, Runnable onQuayLaiBan) {
        this.contentPane    = contentPane;
        this.onQuayLaiBan   = onQuayLaiBan;
    }

    @FXML
    public void initialize() { loadBanTuDB(); }

    private void loadBanTuDB() {
        tableGrid.getChildren().clear();
        tableGrid.getColumnConstraints().clear();
        tableGrid.getRowConstraints().clear();

        int MAX_COL = 4;
        for (int i = 0; i < MAX_COL; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setPercentWidth(100.0 / MAX_COL);
            tableGrid.getColumnConstraints().add(cc);
        }

        List<Table> danhSachBan = tableDAO.getAllTables();
        if (danhSachBan.isEmpty()) { hienBanMacDinh(); return; }

        int soHang = (int) Math.ceil((double) danhSachBan.size() / MAX_COL);
        for (int i = 0; i < soHang; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            rc.setPercentHeight(100.0 / soHang);
            tableGrid.getRowConstraints().add(rc);
        }

        int col = 0, row = 0;
        for (Table ban : danhSachBan) {
            tableGrid.add(taoNutBan(ban), col, row);
            col++;
            if (col >= MAX_COL) { col = 0; row++; }
        }
    }

    private Button taoNutBan(Table ban) {
        Button btn = new Button(ban.getTableName());
        btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btn.setPrefHeight(130);
        GridPane.setHgrow(btn, Priority.ALWAYS);
        GridPane.setVgrow(btn, Priority.ALWAYS);
        applyMauTrangThai(btn, ban.getStatus());
        btn.setOnAction(e -> handleChonBan(ban, btn));
        return btn;
    }

    private void applyMauTrangThai(Button btn, String status) {
        String mau;
        switch (status == null ? "Trống" : status) {
            case "Có khách":  mau = "#e74c3c"; break;
            case "Đặt trước": mau = "#f39c12"; break;
            default:          mau = "#27ae60";
        }
        btn.setStyle(
            "-fx-background-color:" + mau + ";" +
            "-fx-text-fill:white; -fx-font-weight:bold;" +
            "-fx-font-size:13; -fx-cursor:hand; -fx-background-radius:8;"
        );
    }

    private void handleChonBan(Table ban, Button btn) {
        String trangThai = ban.getStatus() == null ? "Trống" : ban.getStatus();

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(ban.getTableName());
        dialog.setResizable(false);

        String mauTT = "Có khách".equals(trangThai) ? "#e74c3c"
                     : "Đặt trước".equals(trangThai) ? "#f39c12" : "#27ae60";

        Label lblTen = new Label(ban.getTableName());
        lblTen.setStyle("-fx-font-size:18; -fx-font-weight:bold; -fx-text-fill:#2c3e50;");
        Label lblTT = new Label("Trạng thái: " + trangThai);
        lblTT.setStyle("-fx-font-size:13; -fx-text-fill:" + mauTT + "; -fx-font-weight:bold;");

        VBox header = new VBox(4, lblTen, lblTT);
        header.setPadding(new Insets(16, 20, 12, 20));
        header.setStyle("-fx-background-color:#f8f9fa; -fx-border-color:#dee2e6; -fx-border-width:0 0 1 0;");

        Button b1 = taoNutHanhDong("🍽  Gọi món",   "#9b59b6", "white");
        Button b2 = taoNutHanhDong("🔴  Có khách",  "#e74c3c", "white");
        Button b3 = taoNutHanhDong("📅  Đặt trước", "#f39c12", "white");
        Button b4 = taoNutHanhDong("✅  Trống",      "#27ae60", "white");
        Button b5 = taoNutHanhDong("✖  Hủy",        "#ecf0f1", "#555555");

        if ("Có khách".equals(trangThai))  b2.setDisable(true);
        if ("Đặt trước".equals(trangThai)) b3.setDisable(true);
        if ("Trống".equals(trangThai))     b4.setDisable(true);

        VBox btnBox = new VBox(8, b1, b2, b3, b4, new Separator(), b5);
        btnBox.setPadding(new Insets(14, 20, 16, 20));

        b1.setOnAction(e -> {
            dialog.close();
            if ("Trống".equals(ban.getStatus())) {
                if (tableDAO.capNhatTrangThai(ban.getId(), "Có khách")) {
                    ban.setStatus("Có khách");
                    applyMauTrangThai(btn, "Có khách");
                }
            }
            goiMonVaoMenuView(ban);
        });
        b2.setOnAction(e -> { doiTrangThai(ban, btn, "Có khách");  dialog.close(); });
        b3.setOnAction(e -> { doiTrangThai(ban, btn, "Đặt trước"); dialog.close(); });
        b4.setOnAction(e -> { doiTrangThai(ban, btn, "Trống");     dialog.close(); });
        b5.setOnAction(e -> dialog.close());

        VBox root = new VBox(header, btnBox);
        root.setStyle("-fx-background-color:white;");
        dialog.setScene(new Scene(root, 260, 310));
        dialog.show();
    }

    private Button taoNutHanhDong(String text, String bg, String fg) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(38);
        b.setStyle(
            "-fx-background-color:" + bg + "; -fx-text-fill:" + fg + ";" +
            "-fx-font-size:13; -fx-background-radius:8; -fx-cursor:hand; -fx-font-weight:bold;"
        );
        return b;
    }

    // XỬ LÝ SỬA
    private void doiTrangThai(Table ban, Button btn, String trangThaiMoi) {
        if (tableDAO.capNhatTrangThai(ban.getId(), trangThaiMoi)) {
            ban.setStatus(trangThaiMoi);
            applyMauTrangThai(btn, trangThaiMoi);
        } else {
            showError("Cập nhật trạng thái thất bại!");
        }
    }

    // GỌI MENU VIEW
    private void goiMonVaoMenuView(Table ban) {
        if (contentPane == null) {
            new Alert(Alert.AlertType.ERROR,
                "Lỗi: không tìm được contentPane!\nHãy mở qua nút Quản lý bàn.")
                .showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/quanlycafe/MenuView.fxml"));
            Node trang = loader.load();
            MenuController menuCtrl = loader.getController();
            menuCtrl.setBan(ban, contentPane, onQuayLaiBan);
            contentPane.getChildren().setAll(trang);
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không load được MenuView.fxml!").showAndWait();
        }
    }

    private void hienBanMacDinh() {
        int MAX_COL = 4, TONG_BAN = 12;
        int soHang = (int) Math.ceil((double) TONG_BAN / MAX_COL);
        for (int i = 0; i < MAX_COL; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setPercentWidth(100.0 / MAX_COL);
            tableGrid.getColumnConstraints().add(cc);
        }
        for (int i = 0; i < soHang; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            rc.setPercentHeight(100.0 / soHang);
            tableGrid.getRowConstraints().add(rc);
        }
        for (int i = 0; i < TONG_BAN; i++) {
            Table banGia = new Table(i + 1, "Bàn " + (i + 1), "Trống");
            tableGrid.add(taoNutBan(banGia), i % MAX_COL, i / MAX_COL);
        }
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
