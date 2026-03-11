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
import java.util.Optional;

public class TableController {

    @FXML
    private GridPane tableGrid;

    private final TableDAO tableDAO = new TableDAO();

    private static final Map<String, Long> MENU = new LinkedHashMap<>();
    static {
        MENU.put("Cà phê đen",        25_000L);
        MENU.put("Cà phê sữa",        30_000L);
        MENU.put("Bạc xỉu",           32_000L);
        MENU.put("Trà sữa trân châu", 45_000L);
        MENU.put("Matcha latte",      40_000L);
        MENU.put("Sinh tố xoài",      35_000L);
        MENU.put("Nước ép cam",       30_000L);
        MENU.put("Bánh mì thịt",      25_000L);
        MENU.put("Bánh croissant",    35_000L);
        MENU.put("Cheesecake",        45_000L);
    }

    @FXML
    public void initialize() {
        loadBanTuDB();
    }

    private void loadBanTuDB() {
        tableGrid.getChildren().clear();
        tableGrid.getColumnConstraints().clear();
        tableGrid.getRowConstraints().clear();

        int MAX_COL = 4;
        for (int i = 0; i < MAX_COL; i++) {
            javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints();
            cc.setHgrow(javafx.scene.layout.Priority.ALWAYS);
            cc.setPercentWidth(100.0 / MAX_COL);
            tableGrid.getColumnConstraints().add(cc);
        }

        List<Table> danhSachBan = tableDAO.getAllTables();

        if (danhSachBan.isEmpty()) {
            hienBanMacDinh();
            return;
        }

        int tongBan = danhSachBan.size();
        int soHang = (int) Math.ceil((double) tongBan / MAX_COL);
        for (int i = 0; i < soHang; i++) {
            javafx.scene.layout.RowConstraints rc = new javafx.scene.layout.RowConstraints();
            rc.setVgrow(javafx.scene.layout.Priority.ALWAYS);
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
        GridPane.setHgrow(btn, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setVgrow(btn, javafx.scene.layout.Priority.ALWAYS);
        btn.setUserData(ban);
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

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(ban.getTableName());
        alert.setHeaderText(ban.getTableName() + "  —  " + trangThai);
        alert.setContentText("Bạn muốn làm gì?");

        ButtonType btnGoiMon   = new ButtonType("Gọi món");
        ButtonType btnCoKhach  = new ButtonType("Có khách");
        ButtonType btnDatTruoc = new ButtonType("Đặt trước");
        ButtonType btnTrong    = new ButtonType("Trống");
        ButtonType btnHuy      = new ButtonType("Hủy", ButtonType.CANCEL.getButtonData());

        alert.getButtonTypes().setAll(btnGoiMon, btnCoKhach, btnDatTruoc, btnTrong, btnHuy);

        Optional<ButtonType> kq = alert.showAndWait();
        if (!kq.isPresent()) return;

        if (kq.get() == btnGoiMon) {
            if ("Trống".equals(trangThai)) {
                if (tableDAO.capNhatTrangThai(ban.getId(), "Có khách")) {
                    ban.setStatus("Có khách");
                    applyMauTrangThai(btn, "Có khách");
                }
            }
            moPopupChonMon(ban);

        } else {
            String trangThaiMoi = null;
            if (kq.get() == btnCoKhach)  trangThaiMoi = "Có khách";
            if (kq.get() == btnDatTruoc) trangThaiMoi = "Đặt trước";
            if (kq.get() == btnTrong)    trangThaiMoi = "Trống";

            if (trangThaiMoi != null) {
                if (tableDAO.capNhatTrangThai(ban.getId(), trangThaiMoi)) {
                    ban.setStatus(trangThaiMoi);
                    applyMauTrangThai(btn, trangThaiMoi);
                } else {
                    showError("Cập nhật trạng thái thất bại!");
                }
            }
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
        listGio.setPrefHeight(180);
        listGio.setPrefWidth(260);

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
                    String.format("%-22s x%d = %,d ₫", e.getKey(), e.getValue(), thanh)
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
            String ten  = mon.getKey();
            long   gia  = mon.getValue();

            Button btnMon = new Button(ten + "\n" + String.format("%,d ₫", gia));
            btnMon.setPrefSize(148, 62);
            btnMon.setStyle(
                "-fx-background-color:#ecf0f1; -fx-font-size:11;" +
                "-fx-background-radius:8; -fx-cursor:hand; -fx-text-alignment:center;"
            );
            btnMon.setOnMouseEntered(e -> btnMon.setStyle(
                "-fx-background-color:#d5d8dc; -fx-font-size:11;" +
                "-fx-background-radius:8; -fx-cursor:hand; -fx-text-alignment:center;"
            ));
            btnMon.setOnMouseExited(e -> btnMon.setStyle(
                "-fx-background-color:#ecf0f1; -fx-font-size:11;" +
                "-fx-background-radius:8; -fx-cursor:hand; -fx-text-alignment:center;"
            ));
            btnMon.setOnAction(e -> {
                gioHang.merge(ten, 1, Integer::sum);
                refreshGio.run();
            });

            gridMon.add(btnMon, col, row);
            col++;
            if (col >= 2) { col = 0; row++; }
        }

        Button btnBot1 = new Button("Bớt 1");
        btnBot1.setPrefWidth(118);
        btnBot1.setStyle("-fx-background-color:#e67e22; -fx-text-fill:white;" +
            "-fx-background-radius:6; -fx-cursor:hand;");
        btnBot1.setOnAction(e -> {
            String chon = listGio.getSelectionModel().getSelectedItem();
            if (chon == null) return;
            String tenMon = chon.trim().split("  x")[0].trim();
            if (!gioHang.containsKey(tenMon)) {
                tenMon = chon.split("x\\d")[0].trim();
            }
            if (gioHang.containsKey(tenMon)) {
                int sl = gioHang.get(tenMon);
                if (sl <= 1) gioHang.remove(tenMon);
                else gioHang.put(tenMon, sl - 1);
                refreshGio.run();
            }
        });

        Button btnXoaTat = new Button("Xóa tất");
        btnXoaTat.setPrefWidth(118);
        btnXoaTat.setStyle("-fx-background-color:#c0392b; -fx-text-fill:white;" +
            "-fx-background-radius:6; -fx-cursor:hand;");
        btnXoaTat.setOnAction(e -> { gioHang.clear(); refreshGio.run(); });

        HBox hboxXoa = new HBox(8, btnBot1, btnXoaTat);

        Button btnXacNhan = new Button("Xác nhận đặt món");
        btnXacNhan.setPrefWidth(255);
        btnXacNhan.setPrefHeight(40);
        btnXacNhan.setStyle(
            "-fx-background-color:#27ae60; -fx-text-fill:white;" +
            "-fx-font-weight:bold; -fx-font-size:14;" +
            "-fx-background-radius:8; -fx-cursor:hand;"
        );
        btnXacNhan.setOnAction(e -> {
            if (gioHang.isEmpty()) {
                showError("Chưa chọn món nào!");
                return;
            }
            long tong = gioHang.entrySet().stream()
                .mapToLong(en -> MENU.getOrDefault(en.getKey(), 0L) * en.getValue())
                .sum();

            StringBuilder sb = new StringBuilder();
            gioHang.forEach((ten, sl) ->
                sb.append(String.format("• %-22s x%d\n", ten, sl))
            );
            sb.append(String.format("\nTổng cộng: %,d ₫", tong));

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận đặt món");
            confirm.setHeaderText(ban.getTableName() + " — Đơn hàng:");
            confirm.setContentText(sb.toString());

            confirm.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK) {
                    Alert ok = new Alert(Alert.AlertType.INFORMATION);
                    ok.setTitle("Thành công");
                    ok.setHeaderText(null);
                    ok.setContentText("Đã ghi nhận đơn cho " + ban.getTableName() + "!\nTổng: " + String.format("%,d ₫", tong));
                    ok.showAndWait();
                    popup.close();
                }
            });
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setPrefWidth(80);
        btnHuy.setStyle("-fx-background-color:#ecf0f1; -fx-cursor:hand; -fx-background-radius:6;");
        btnHuy.setOnAction(e -> popup.close());

        HBox hboxBtn = new HBox(10, btnXacNhan, btnHuy);
        hboxBtn.setAlignment(Pos.CENTER_RIGHT);

        Label lblGio = new Label("Giỏ hàng");
        lblGio.setFont(Font.font("System Bold", 14));

        VBox panelPhai = new VBox(10, lblGio, listGio, hboxXoa,
                                  new Separator(), lblTong, hboxBtn);
        panelPhai.setPadding(new Insets(10));
        panelPhai.setPrefWidth(280);
        panelPhai.setStyle(
            "-fx-background-color:#fafafa;" +
            "-fx-border-color:#dddddd;" +
            "-fx-border-radius:8;" +
            "-fx-background-radius:8;"
        );

        ScrollPane scroll = new ScrollPane(gridMon);
        scroll.setFitToWidth(true);
        scroll.setPrefWidth(340);
        scroll.setPrefHeight(430);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

        HBox body = new HBox(16, scroll, panelPhai);
        body.setPadding(new Insets(10));

        VBox rootLayout = new VBox(12, lblTieu, new Separator(), body);
        rootLayout.setPadding(new Insets(16));
        rootLayout.setStyle("-fx-background-color:white;");

        popup.setScene(new Scene(rootLayout, 680, 560));
        popup.show();
    }

    private void hienBanMacDinh() {
        int MAX_COL = 4, TONG_BAN = 12;
        int soHang = (int) Math.ceil((double) TONG_BAN / MAX_COL);

        tableGrid.getColumnConstraints().clear();
        for (int i = 0; i < MAX_COL; i++) {
            javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints();
            cc.setHgrow(javafx.scene.layout.Priority.ALWAYS);
            cc.setPercentWidth(100.0 / MAX_COL);
            tableGrid.getColumnConstraints().add(cc);
        }
        tableGrid.getRowConstraints().clear();
        for (int i = 0; i < soHang; i++) {
            javafx.scene.layout.RowConstraints rc = new javafx.scene.layout.RowConstraints();
            rc.setVgrow(javafx.scene.layout.Priority.ALWAYS);
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
