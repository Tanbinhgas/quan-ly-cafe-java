package com.quanlycafe.controller;

import com.quanlycafe.dao.TableDAO;
import com.quanlycafe.model.Table;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableController {

    @FXML
    private GridPane tableGrid;

    private final TableDAO tableDAO = new TableDAO();

    private static final Map<String, Long> MENU = new LinkedHashMap<>();
    static {
        MENU.put("Cà phê phin",       25_000L);
        MENU.put("Cappuccino",         45_000L);
        MENU.put("Latte",              45_000L);
        MENU.put("Matcha Mật Mè",      50_000L);
        MENU.put("Trà Chanh Giã Tay",  30_000L);
        MENU.put("Trà Đào Cam Sả",     40_000L);
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
            moPopupChonMon(ban);
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

    private void doiTrangThai(Table ban, Button btn, String trangThaiMoi) {
        if (tableDAO.capNhatTrangThai(ban.getId(), trangThaiMoi)) {
            ban.setStatus(trangThaiMoi);
            applyMauTrangThai(btn, trangThaiMoi);
        } else {
            showError("Cập nhật trạng thái thất bại!");
        }
    }

    private void moPopupChonMon(Table ban) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Gọi món — " + ban.getTableName());
        popup.setResizable(false);

        Map<String, Integer> gioHang = new LinkedHashMap<>();

        Label lblTieu = new Label("Gọi món  —  " + ban.getTableName());
        lblTieu.setFont(Font.font("System Bold", 18));
        lblTieu.setStyle("-fx-text-fill:#2c3e50;");

        ListView<String> listGio = new ListView<>();
        listGio.setPrefHeight(190);

        Label lblTong = new Label("Tổng: 0 ₫");
        lblTong.setFont(Font.font("System Bold", 15));
        lblTong.setStyle("-fx-text-fill:#e74c3c;");

        Runnable refreshGio = () -> {
            listGio.getItems().clear();
            long tong = 0;
            for (Map.Entry<String, Integer> e : gioHang.entrySet()) {
                long gia   = MENU.getOrDefault(e.getKey(), 0L);
                long thanh = gia * e.getValue();
                tong += thanh;
                listGio.getItems().add(
                    String.format("%s  x%d  =  %,d ₫", e.getKey(), e.getValue(), thanh)
                );
            }
            lblTong.setText(String.format("Tổng: %,d ₫", tong));
        };

        GridPane gridMon = new GridPane();
        gridMon.setHgap(10);
        gridMon.setVgap(10);
        gridMon.setPadding(new Insets(10));

        int col = 0, row = 0;
        for (Map.Entry<String, Long> mon : MENU.entrySet()) {
            String ten = mon.getKey();
            long   gia = mon.getValue();

            Button btnMon = new Button(ten + "\n" + String.format("%,d ₫", gia));
            btnMon.setPrefSize(175, 65);
            btnMon.setWrapText(true);
            btnMon.setStyle(
                "-fx-background-color:#ecf0f1; -fx-font-size:12;" +
                "-fx-background-radius:8; -fx-cursor:hand; -fx-text-alignment:center;"
            );
            btnMon.setOnMouseEntered(e -> btnMon.setStyle(
                "-fx-background-color:#d5d8dc; -fx-font-size:12;" +
                "-fx-background-radius:8; -fx-cursor:hand; -fx-text-alignment:center;"
            ));
            btnMon.setOnMouseExited(e -> btnMon.setStyle(
                "-fx-background-color:#ecf0f1; -fx-font-size:12;" +
                "-fx-background-radius:8; -fx-cursor:hand; -fx-text-alignment:center;"
            ));
            btnMon.setOnAction(e -> { gioHang.merge(ten, 1, Integer::sum); refreshGio.run(); });

            gridMon.add(btnMon, col, row);
            col++;
            if (col >= 2) { col = 0; row++; }
        }

        Button btnBot1 = new Button("➖  Bớt 1");
        btnBot1.setMaxWidth(Double.MAX_VALUE);
        btnBot1.setPrefHeight(34);
        btnBot1.setStyle("-fx-background-color:#e67e22; -fx-text-fill:white;" +
            "-fx-background-radius:6; -fx-cursor:hand; -fx-font-weight:bold;");
        btnBot1.setOnAction(e -> {
            String chon = listGio.getSelectionModel().getSelectedItem();
            if (chon == null) return;
            String tenMon = chon.split("  x")[0].trim();
            if (gioHang.containsKey(tenMon)) {
                int sl = gioHang.get(tenMon);
                if (sl <= 1) gioHang.remove(tenMon);
                else gioHang.put(tenMon, sl - 1);
                refreshGio.run();
            }
        });

        Button btnXoaTat = new Button("🗑  Xóa tất");
        btnXoaTat.setMaxWidth(Double.MAX_VALUE);
        btnXoaTat.setPrefHeight(34);
        btnXoaTat.setStyle("-fx-background-color:#c0392b; -fx-text-fill:white;" +
            "-fx-background-radius:6; -fx-cursor:hand; -fx-font-weight:bold;");
        btnXoaTat.setOnAction(e -> { gioHang.clear(); refreshGio.run(); });

        HBox hboxXoa = new HBox(8, btnBot1, btnXoaTat);
        HBox.setHgrow(btnBot1, Priority.ALWAYS);
        HBox.setHgrow(btnXoaTat, Priority.ALWAYS);

        Button btnXacNhan = new Button("✅  Xác nhận đặt món");
        btnXacNhan.setMaxWidth(Double.MAX_VALUE);
        btnXacNhan.setPrefHeight(40);
        btnXacNhan.setStyle(
            "-fx-background-color:#27ae60; -fx-text-fill:white;" +
            "-fx-font-weight:bold; -fx-font-size:13; -fx-background-radius:8; -fx-cursor:hand;"
        );
        btnXacNhan.setOnAction(e -> {
            if (gioHang.isEmpty()) { showError("Chưa chọn món nào!"); return; }
            long tong = gioHang.entrySet().stream()
                .mapToLong(en -> MENU.getOrDefault(en.getKey(), 0L) * en.getValue()).sum();

            StringBuilder sb = new StringBuilder();
            gioHang.forEach((ten, sl) -> sb.append(String.format("• %s  x%d\n", ten, sl)));
            sb.append(String.format("\nTổng cộng: %,d ₫", tong));

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận đặt món");
            confirm.setHeaderText(ban.getTableName() + " — Đơn hàng:");
            confirm.setContentText(sb.toString());
            confirm.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK) {
                    new Alert(Alert.AlertType.INFORMATION,
                        "Đã ghi nhận đơn cho " + ban.getTableName() +
                        "!\nTổng: " + String.format("%,d ₫", tong))
                        .showAndWait();
                    popup.close();
                }
            });
        });

        Button btnHuyBo = new Button("✖  Hủy bỏ");
        btnHuyBo.setPrefHeight(40);
        btnHuyBo.setPrefWidth(100);
        btnHuyBo.setStyle("-fx-background-color:#ecf0f1; -fx-cursor:hand;" +
            "-fx-background-radius:8; -fx-font-size:13;");
        btnHuyBo.setOnAction(e -> popup.close());

        HBox hboxBtn = new HBox(10, btnXacNhan, btnHuyBo);
        HBox.setHgrow(btnXacNhan, Priority.ALWAYS);

        Label lblGio = new Label("🛒  Giỏ hàng");
        lblGio.setFont(Font.font("System Bold", 14));

        VBox panelPhai = new VBox(10, lblGio, listGio, hboxXoa,
                                  new Separator(), lblTong, hboxBtn);
        panelPhai.setPadding(new Insets(12));
        panelPhai.setPrefWidth(300);
        panelPhai.setStyle(
            "-fx-background-color:#fafafa; -fx-border-color:#dddddd;" +
            "-fx-border-radius:8; -fx-background-radius:8;"
        );

        ScrollPane scroll = new ScrollPane(gridMon);
        scroll.setFitToWidth(true);
        scroll.setPrefWidth(390);
        scroll.setPrefHeight(420);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

        HBox body = new HBox(16, scroll, panelPhai);
        body.setPadding(new Insets(10));

        VBox rootLayout = new VBox(12, lblTieu, new Separator(), body);
        rootLayout.setPadding(new Insets(16));
        rootLayout.setStyle("-fx-background-color:white;");

        popup.setScene(new Scene(rootLayout, 720, 540));
        popup.show();
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
