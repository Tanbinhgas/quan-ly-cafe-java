package com.quanlycafe.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MenuController {

    @FXML private FlowPane flowMenu;
    @FXML private TextField txtTimKiem;

    private static class MonUong {
        String ten, fileAnh, moTa;
        long gia;
        List<String> nguyenLieu;

        MonUong(String ten, long gia, String fileAnh, String moTa, String... nl) {
            this.ten = ten; this.gia = gia;
            this.fileAnh = fileAnh; this.moTa = moTa;
            this.nguyenLieu = Arrays.asList(nl);
        }
    }

    private final List<MonUong> danhSachMon = Arrays.asList(
        new MonUong("Cà phê phin", 25_000, "cafe_phin.jpg",
            "Cà phê phin truyền thống đậm đà",
            "Cà phê rang xay", "Sữa đặc", "Nước sôi"),

        new MonUong("Cappuccino", 45_000, "capuchino.jpg",
            "Cappuccino Ý béo ngậy thơm ngon",
            "Espresso", "Sữa tươi", "Bọt sữa"),

        new MonUong("Latte", 45_000, "latte.jpg",
            "Latte mịn màng nhẹ nhàng",
            "Espresso", "Sữa tươi", "Bọt sữa mịn"),

        new MonUong("Matcha Mật Mè", 50_000, "matcha_mat_me.jpg",
            "Matcha béo ngọt với mật ong",
            "Bột matcha", "Sữa tươi", "Mật ong", "Đá"),

        new MonUong("Trà Chanh Giã Tay", 30_000, "tra_chanh_gia_tay.jpg",
            "Trà chanh giã tay chua ngọt mát lạnh",
            "Trà xanh", "Chanh tươi", "Đường", "Đá"),

        new MonUong("Trà Đào Cam Sả", 40_000, "tra_dao_cam_sa.jpg",
            "Trà đào thơm mát với cam và sả",
            "Trà xanh", "Đào ngâm", "Cam tươi", "Sả", "Đá")
    );

    @FXML
    public void initialize() {
        renderMon(danhSachMon);

        txtTimKiem.textProperty().addListener((obs, o, n) -> {
            String kw = n.trim().toLowerCase();
            if (kw.isEmpty()) {
                renderMon(danhSachMon);
            } else {
                List<MonUong> loc = danhSachMon.stream()
                    .filter(m -> m.ten.toLowerCase().contains(kw))
                    .toList();
                renderMon(loc);
            }
        });
    }

    private void renderMon(List<MonUong> list) {
        flowMenu.getChildren().clear();
        for (MonUong mon : list) {
            flowMenu.getChildren().add(taoCard(mon));
        }
    }

    private VBox taoCard(MonUong mon) {
        VBox card = new VBox(0);
        card.setPrefWidth(210);
        card.setStyle(
            "-fx-background-color:white;" +
            "-fx-background-radius:12;" +
            "-fx-effect:dropshadow(gaussian,#cccccc,8,0,0,2);" +
            "-fx-cursor:hand;"
        );

        ImageView imgView = new ImageView();
        imgView.setFitWidth(210);
        imgView.setFitHeight(160);
        imgView.setPreserveRatio(false);

        try {
            var url = getClass().getResource("/com/quanlycafe/img/" + mon.fileAnh);
            if (url != null) {
                imgView.setImage(new Image(url.toExternalForm()));
            } else {
                imgView.setStyle("-fx-background-color:#ecf0f1;");
            }
        } catch (Exception e) {
            System.err.println("Không load được ảnh: " + mon.fileAnh);
        }

        StackPane imgPane = new StackPane(imgView);
        imgPane.setStyle("-fx-background-radius:12 12 0 0; -fx-background-color:#ecf0f1;");
        imgPane.setPrefHeight(160);

        Label lblGia = new Label(String.format("%,d ₫", mon.gia));
        lblGia.setStyle(
            "-fx-background-color:#1a2634;" +
            "-fx-text-fill:#f0c040;" +
            "-fx-font-weight:bold;" +
            "-fx-font-size:12;" +
            "-fx-padding:4 10 4 10;" +
            "-fx-background-radius:20;"
        );
        StackPane.setAlignment(lblGia, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(lblGia, new Insets(0, 10, 10, 0));
        imgPane.getChildren().add(lblGia);

        VBox content = new VBox(6);
        content.setPadding(new Insets(12, 14, 14, 14));

        Label lblTen = new Label(mon.ten);
        lblTen.setFont(Font.font("System Bold", 14));
        lblTen.setStyle("-fx-text-fill:#1a2634; -fx-font-weight:bold;");
        lblTen.setWrapText(true);

        Label lblMoTa = new Label(mon.moTa);
        lblMoTa.setStyle("-fx-text-fill:#95a5a6; -fx-font-size:11;");
        lblMoTa.setWrapText(true);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#f0f0f0;");

        Label lblNLTitle = new Label("📦 Nguyên liệu:");
        lblNLTitle.setStyle("-fx-font-size:11; -fx-font-weight:bold; -fx-text-fill:#7f8c8d;");

        VBox nlBox = new VBox(3);
        for (String nl : mon.nguyenLieu) {
            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER_LEFT);

            Label dot = new Label("•");
            dot.setStyle("-fx-text-fill:#9b59b6; -fx-font-weight:bold;");

            Label lblNl = new Label(nl);
            lblNl.setStyle("-fx-font-size:11; -fx-text-fill:#555555;");

            row.getChildren().addAll(dot, lblNl);
            nlBox.getChildren().add(row);
        }

        content.getChildren().addAll(lblTen, lblMoTa, sep, lblNLTitle, nlBox);
        card.getChildren().addAll(imgPane, content);

        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color:white;" +
            "-fx-background-radius:12;" +
            "-fx-effect:dropshadow(gaussian,#aaaaaa,14,0,0,4);" +
            "-fx-cursor:hand;" +
            "-fx-translate-y:-3;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color:white;" +
            "-fx-background-radius:12;" +
            "-fx-effect:dropshadow(gaussian,#cccccc,8,0,0,2);" +
            "-fx-cursor:hand;" +
            "-fx-translate-y:0;"
        ));

        return card;
    }
}
