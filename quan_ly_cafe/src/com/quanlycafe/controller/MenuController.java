package com.quanlycafe.controller;

import com.quanlycafe.dao.DonHangDAO;
import com.quanlycafe.model.DonHang;
import com.quanlycafe.model.Table;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuController {

    @FXML private FlowPane  flowMenu;
    @FXML private TextField txtTimKiem;
    @FXML private Label     lblTenBan;
    @FXML private Label     lblTenBanCart;
    @FXML private Button    btnQuayLai;
    @FXML private Button    btnUongTaiCho;
    @FXML private Button    btnMangVe;
    @FXML private VBox      cartItems;
    @FXML private Label     lblTongTien;
    @FXML private TextArea  txtGhiChu;

    private Table     banHienTai  = null;
    private StackPane contentPane = null;
    private Runnable  onQuayLai   = null;
    private String    loaiDon     = "Uống tại chỗ";

    private final DonHangDAO donHangDAO = new DonHangDAO();

    private static final String S_ACTIVE =
        "-fx-background-color:#1a2634; -fx-text-fill:#f0c040;" +
        "-fx-font-weight:bold; -fx-background-radius:20; -fx-cursor:hand; -fx-font-size:11;";
    private static final String S_NORMAL =
        "-fx-background-color:#ecf0f1; -fx-text-fill:#555;" +
        "-fx-font-weight:bold; -fx-background-radius:20; -fx-cursor:hand; -fx-font-size:11;";

    private static class MonUong {
        String ten, fileAnh, moTa; long gia; List<String> nguyenLieu;
        MonUong(String ten, long gia, String fileAnh, String moTa, String... nl) {
            this.ten = ten; this.gia = gia; this.fileAnh = fileAnh;
            this.moTa = moTa; this.nguyenLieu = Arrays.asList(nl);
        }
    }

    private final Map<MonUong, Integer> gioHang = new LinkedHashMap<>();

    private final List<MonUong> danhSachMon = Arrays.asList(
        new MonUong("Cà phê phin",      25_000, "cafe_phin.jpg",
            "Cà phê phin truyền thống đậm đà",
            "Cà phê rang xay", "Sữa đặc", "Nước sôi"),
        new MonUong("Cappuccino",        45_000, "capuchino.jpg",
            "Cappuccino Ý béo ngậy thơm ngon",
            "Espresso", "Sữa tươi", "Bọt sữa"),
        new MonUong("Latte",             45_000, "latte.jpg",
            "Latte mịn màng nhẹ nhàng",
            "Espresso", "Sữa tươi", "Bọt sữa mịn"),
        new MonUong("Matcha Mật Mè",     50_000, "matcha_mat_me.jpg",
            "Matcha béo ngọt với mật ong",
            "Bột matcha", "Sữa tươi", "Mật ong", "Đá"),
        new MonUong("Trà Chanh Giã Tay", 30_000, "tra_chanh_gia_tay.jpg",
            "Trà chanh giã tay chua ngọt mát lạnh",
            "Trà xanh", "Chanh tươi", "Đường", "Đá"),
        new MonUong("Trà Đào Cam Sả",    40_000, "tra_dao_cam_sa.jpg",
            "Trà đào thơm mát với cam và sả",
            "Trà xanh", "Đào ngâm", "Cam tươi", "Sả", "Đá")
    );

    public void setBan(Table ban, StackPane contentPane, Runnable onQuayLai) {
        this.banHienTai = ban; this.contentPane = contentPane; this.onQuayLai = onQuayLai;
        String tag = "📍 " + ban.getTableName();
        lblTenBan.setText(tag); lblTenBan.setVisible(true); lblTenBan.setManaged(true);
        lblTenBanCart.setText(tag);
        btnQuayLai.setVisible(true); btnQuayLai.setManaged(true);
    }

    @FXML
    public void initialize() {
        lblTenBan.setVisible(false); lblTenBan.setManaged(false);
        lblTenBanCart.setText("");
        btnQuayLai.setVisible(false); btnQuayLai.setManaged(false);

        capNhatStyleLoai();
        renderMon(danhSachMon);
        renderCart();

        txtTimKiem.textProperty().addListener((obs, o, n) -> {
            String kw = n.trim().toLowerCase();
            if (kw.isEmpty()) renderMon(danhSachMon);
            else renderMon(danhSachMon.stream()
                    .filter(m -> m.ten.toLowerCase().contains(kw)).toList());
        });
    }

    @FXML private void chonUongTaiCho() { loaiDon = "Uống tại chỗ"; capNhatStyleLoai(); }
    @FXML private void chonMangVe()     { loaiDon = "Mang về";       capNhatStyleLoai(); }
    private void capNhatStyleLoai() {
        btnUongTaiCho.setStyle("Uống tại chỗ".equals(loaiDon) ? S_ACTIVE : S_NORMAL);
        btnMangVe.setStyle("Mang về".equals(loaiDon)           ? S_ACTIVE : S_NORMAL);
    }

    @FXML private void quayLaiBan() {
        if (contentPane == null) return;
        try {
            Node t = FXMLLoader.load(getClass().getResource("/com/quanlycafe/table.fxml"));
            contentPane.getChildren().setAll(t);
            if (onQuayLai != null) onQuayLai.run();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void renderMon(List<MonUong> list) {
        flowMenu.getChildren().clear();
        for (MonUong mon : list) flowMenu.getChildren().add(taoCard(mon));
    }

    private VBox taoCard(MonUong mon) {
        VBox card = new VBox(0);
        card.setPrefWidth(190);
        card.setStyle("-fx-background-color:white; -fx-background-radius:12;" +
                      "-fx-effect:dropshadow(gaussian,#cccccc,8,0,0,2); -fx-cursor:hand;");

        ImageView iv = new ImageView();
        iv.setFitWidth(190); iv.setFitHeight(140); iv.setPreserveRatio(false);
        try {
            var url = getClass().getResource("/com/quanlycafe/img/" + mon.fileAnh);
            if (url != null) iv.setImage(new Image(url.toExternalForm()));
        } catch (Exception ignored) {}

        StackPane imgPane = new StackPane(iv);
        imgPane.setStyle("-fx-background-radius:12 12 0 0; -fx-background-color:#ecf0f1;");
        imgPane.setPrefHeight(140);

        Label lblGia = new Label(String.format("%,d ₫", mon.gia));
        lblGia.setStyle("-fx-background-color:#1a2634; -fx-text-fill:#f0c040;" +
                        "-fx-font-weight:bold; -fx-font-size:11;" +
                        "-fx-padding:3 8 3 8; -fx-background-radius:20;");
        StackPane.setAlignment(lblGia, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(lblGia, new Insets(0, 8, 8, 0));
        imgPane.getChildren().add(lblGia);

        VBox content = new VBox(4);
        content.setPadding(new Insets(10, 12, 12, 12));

        Label lblTen = new Label(mon.ten);
        lblTen.setFont(Font.font("System Bold", 13));
        lblTen.setStyle("-fx-text-fill:#1a2634; -fx-font-weight:bold;");
        lblTen.setWrapText(true);

        Label lblMoTa = new Label(mon.moTa);
        lblMoTa.setStyle("-fx-text-fill:#95a5a6; -fx-font-size:10;");
        lblMoTa.setWrapText(true);

        Label lblNLTitle = new Label("📦 Nguyên liệu:");
        lblNLTitle.setStyle("-fx-font-size:10; -fx-font-weight:bold; -fx-text-fill:#7f8c8d;");

        VBox nlBox = new VBox(2);
        for (String nl : mon.nguyenLieu) {
            HBox row = new HBox(4); row.setAlignment(Pos.CENTER_LEFT);
            Label dot = new Label("•"); dot.setStyle("-fx-text-fill:#9b59b6; -fx-font-size:11;");
            Label lbl = new Label(nl); lbl.setStyle("-fx-font-size:10; -fx-text-fill:#555;");
            row.getChildren().addAll(dot, lbl);
            nlBox.getChildren().add(row);
        }
        content.getChildren().addAll(lblTen, lblMoTa, new Separator(), lblNLTitle, nlBox);
        card.getChildren().addAll(imgPane, content);

        String baseStyle = "-fx-background-color:white; -fx-background-radius:12;" +
                           "-fx-effect:dropshadow(gaussian,#cccccc,8,0,0,2); -fx-cursor:hand;";
        String hoverStyle = "-fx-background-color:white; -fx-background-radius:12;" +
                            "-fx-effect:dropshadow(gaussian,#aaaaaa,14,0,0,4);" +
                            "-fx-cursor:hand; -fx-translate-y:-3;";
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e  -> card.setStyle(baseStyle));
        card.setOnMouseClicked(e -> themVaoGio(mon));
        return card;
    }

    private void themVaoGio(MonUong mon) {
        gioHang.merge(mon, 1, Integer::sum);
        renderCart();
    }

    private void renderCart() {
        cartItems.getChildren().clear();
        long tong = 0;

        for (Map.Entry<MonUong, Integer> entry : gioHang.entrySet()) {
            MonUong mon   = entry.getKey();
            int     sl    = entry.getValue();
            long    thanh = mon.gia * sl;
            tong += thanh;

            HBox row1 = new HBox(4);
            row1.setAlignment(Pos.CENTER_LEFT);
            Label ten = new Label(mon.ten);
            ten.setStyle("-fx-font-size:12; -fx-text-fill:#2c3e50; -fx-font-weight:bold;");
            ten.setWrapText(true);
            ten.setMaxWidth(160);
            Label gia = new Label(String.format("%,d ₫", thanh));
            gia.setStyle("-fx-font-size:11; -fx-text-fill:#e74c3c; -fx-font-weight:bold;");
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            row1.getChildren().addAll(ten, sp, gia);

            HBox row2 = new HBox(6);
            row2.setAlignment(Pos.CENTER_LEFT);
            Button tru  = nutNho("➖", "#e67e22");
            Label slLbl = new Label(String.valueOf(sl));
            slLbl.setMinWidth(28);
            slLbl.setPrefWidth(28);
            slLbl.setAlignment(Pos.CENTER);
            slLbl.setStyle("-fx-font-weight:bold; -fx-font-size:13; -fx-text-fill:#1a2634;" +
                           "-fx-background-color:#f0f0f0; -fx-background-radius:6;" +
                           "-fx-padding:2 0 2 0;");
            Button cong = nutNho("➕", "#27ae60");
            Region sp2  = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
            Button xoa  = nutNho("✕ Xóa", "#c0392b");

            tru.setOnAction(e -> {
                int m = gioHang.get(mon) - 1;
                if (m <= 0) gioHang.remove(mon); else gioHang.put(mon, m);
                renderCart();
            });
            cong.setOnAction(e -> { gioHang.put(mon, sl + 1); renderCart(); });
            xoa.setOnAction(e  -> { gioHang.remove(mon);       renderCart(); });
            row2.getChildren().addAll(tru, slLbl, cong, sp2, xoa);

            VBox item = new VBox(3, row1, row2);
            item.setStyle("-fx-background-color:#f8f9fa; -fx-background-radius:8;" +
                          "-fx-padding:6 8 6 8;");
            cartItems.getChildren().add(item);
        }

        if (!gioHang.isEmpty()) {
            Button clear = new Button("🗑  Xóa tất cả");
            clear.setMaxWidth(Double.MAX_VALUE);
            clear.setStyle("-fx-background-color:#fdecea; -fx-text-fill:#c0392b;" +
                           "-fx-background-radius:6; -fx-cursor:hand; -fx-font-size:11;" +
                           "-fx-padding:5 0 5 0;");
            clear.setOnAction(e -> { gioHang.clear(); renderCart(); });
            cartItems.getChildren().add(clear);
        }

        lblTongTien.setText(String.format("Tổng: %,d ₫", tong));
    }

    private Button nutNho(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:transparent; -fx-text-fill:" + color + ";" +
                   "-fx-cursor:hand; -fx-font-size:11; -fx-padding:2 6 2 6;");
        return b;
    }

    @FXML private void thanhToan() {
        if (gioHang.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Chưa chọn món nào!").showAndWait();
            return;
        }
        long   tong   = gioHang.entrySet().stream()
                            .mapToLong(e -> e.getKey().gia * e.getValue()).sum();
        String tenBan = (banHienTai != null) ? banHienTai.getTableName() : "Mang về";
        String ghiChu = txtGhiChu.getText().trim();
        String icon   = "Mang về".equals(loaiDon) ? "🛍" : "🪑";

        StringBuilder sb = new StringBuilder();
        sb.append(icon).append("  ").append(loaiDon)
          .append("   |   📍 ").append(tenBan).append("\n\n");
        gioHang.forEach((m, sl) ->
            sb.append(String.format("• %s  x%d  =  %,d ₫\n", m.ten, sl, m.gia * sl)));
        if (!ghiChu.isBlank()) sb.append("\n📝 ").append(ghiChu);
        sb.append(String.format("\n\n💰 Tổng: %,d ₫", tong));

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận đặt món");
        confirm.setHeaderText("Kiểm tra đơn hàng:");
        confirm.setContentText(sb.toString());
        confirm.showAndWait().ifPresent(res -> {
            if (res != ButtonType.OK) return;

            Map<String, Integer> gioHangMap = new LinkedHashMap<>();
            Map<String, Long>    giaMap     = new LinkedHashMap<>();
            gioHang.forEach((m, sl) -> { gioHangMap.put(m.ten, sl); giaMap.put(m.ten, m.gia); });

            boolean saved = donHangDAO.luuDonHang(tenBan, loaiDon, gioHangMap, giaMap, tong, ghiChu);

            new Alert(Alert.AlertType.INFORMATION,
                "✅ Đặt món thành công!\n" + icon + "  " + loaiDon +
                "  |  📍 " + tenBan + "\nTổng: " + String.format("%,d ₫", tong) +
                (saved ? "\n\nĐã lưu vào lịch sử." : "\n\n⚠ Không lưu được DB."))
                .showAndWait();

            gioHang.clear(); txtGhiChu.clear(); renderCart();
            quayLaiBan();
        });
    }

    public void xemLichSu() {
        List<DonHang> list = donHangDAO.getAllDonHang();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("📋 Lịch sử đơn hàng");

        Label lblTitle = new Label("📋  Lịch sử đơn hàng");
        lblTitle.setFont(Font.font("System Bold", 18));
        lblTitle.setStyle("-fx-text-fill:#1a2634;");

        long tongDT   = list.stream().mapToLong(DonHang::getTongTien).sum();
        long doanhThuHomNay = donHangDAO.getDoanhThuHomNay();
        long doanhThuThang = donHangDAO.getDoanhThuThang();
        long soTC     = list.stream().filter(d -> "Uống tại chỗ".equals(d.getLoaiDon())).count();
        long soMV     = list.stream().filter(d -> "Mang về".equals(d.getLoaiDon())).count();

        HBox stats = new HBox(12);
            stats.getChildren().addAll(
                statBox("💰 Tổng doanh thu", String.format("%,d ₫", tongDT), "#27ae60"),
                statBox("📅 Hôm nay", String.format("%,d ₫", doanhThuHomNay), "#16a085"),
                statBox("📆 Tháng này", String.format("%,d ₫", doanhThuThang), "#8e44ad"),
                statBox("🪑 Tại chỗ", soTC + " đơn", "#3498db"),
                statBox("🛍 Mang về", soMV + " đơn", "#9b59b6"),
                statBox("📦 Tổng đơn", list.size() + " đơn", "#e67e22")
        );
        stats.setStyle("-fx-background-color:#f8f9fa; -fx-padding:10; -fx-background-radius:8;");

        TableView<DonHang> tv = new TableView<>();
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Chọn ngày");

        ComboBox<Integer> cbThang = new ComboBox<>();
        for(int i=1;i<=12;i++) cbThang.getItems().add(i);
        cbThang.setPromptText("Chọn tháng");

        Button btnLoc = new Button("🔍 Lọc");

        TextField txtMin = new TextField();
        txtMin.setPromptText("Doanh thu từ");

        TextField txtMax = new TextField();
        txtMax.setPromptText("Đến");

        HBox locBox = new HBox(10,
                new Label("📅 Ngày:"), datePicker,
                new Label("📆 Tháng:"), cbThang,
                new Label("💰 Từ:"), txtMin,
                new Label("Đến:"), txtMax,
                btnLoc
        );

        // Nút Lọc: ưu tiên khoảng doanh thu > tháng > ngày, có nút Xóa lọc
        Button btnXoaLoc = new Button("✕ Xóa lọc");
        btnXoaLoc.setStyle("-fx-background-color:#ecf0f1; -fx-text-fill:#555;" +
                           "-fx-background-radius:6; -fx-cursor:hand;");
        btnXoaLoc.setOnAction(e -> {
            datePicker.setValue(null);
            cbThang.setValue(null);
            txtMin.clear();
            txtMax.clear();
            tv.getItems().setAll(donHangDAO.getAllDonHang());
        });
        locBox.getChildren().add(btnXoaLoc);

        btnLoc.setOnAction(e -> {
            String sMin = txtMin.getText().trim();
            String sMax = txtMax.getText().trim();

            // Ưu tiên 1: lọc khoảng doanh thu
            if (!sMin.isEmpty() || !sMax.isEmpty()) {
                try {
                    // Cho phép để trống một đầu → mặc định 0 hoặc Long.MAX_VALUE
                    long min = sMin.isEmpty() ? 0L : Long.parseLong(sMin.replaceAll("[^0-9]", ""));
                    long max = sMax.isEmpty() ? Long.MAX_VALUE : Long.parseLong(sMax.replaceAll("[^0-9]", ""));
                    if (min > max) {
                        new Alert(Alert.AlertType.WARNING, "Giá trị 'Từ' phải nhỏ hơn hoặc bằng 'Đến'!").showAndWait();
                        return;
                    }
                    tv.getItems().setAll(donHangDAO.getDonHangTheoDoanhThu(min, max));
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.WARNING, "Vui lòng nhập số hợp lệ cho khoảng doanh thu!").showAndWait();
                }
                return;
            }

            // Ưu tiên 2: lọc theo tháng
            if (cbThang.getValue() != null) {
                tv.getItems().setAll(donHangDAO.getDonHangTheoThang(cbThang.getValue()));
                return;
            }

            // Ưu tiên 3: lọc theo ngày cụ thể
            if (datePicker.getValue() != null) {
                java.sql.Date ngay = java.sql.Date.valueOf(datePicker.getValue());
                tv.getItems().setAll(donHangDAO.getDonHangTheoNgay(ngay));
                return;
            }

            // Không có điều kiện → hiện tất cả
            tv.getItems().setAll(donHangDAO.getAllDonHang());
        });

        TableColumn<DonHang, String> c1 = new TableColumn<>("⏰ Thời gian");
        c1.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getThoiGian()));

        TableColumn<DonHang, String> c2 = new TableColumn<>("📍 Bàn");
        c2.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTenBan()));
        c2.setMaxWidth(80);

        TableColumn<DonHang, String> c3 = new TableColumn<>("Loại");
        c3.setCellValueFactory(d -> {
            String l = d.getValue().getLoaiDon();
            return new javafx.beans.property.SimpleStringProperty(
                "Mang về".equals(l) ? "🛍 Mang về" : "🪑 Tại chỗ");
        });
        c3.setMaxWidth(110);

        TableColumn<DonHang, String> c4 = new TableColumn<>("💰 Tổng");
        c4.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            String.format("%,d ₫", d.getValue().getTongTien())));
        c4.setMaxWidth(110);

        TableColumn<DonHang, String> c5 = new TableColumn<>("📝 Ghi chú");
        c5.setCellValueFactory(d -> {
            String gc = d.getValue().getGhiChu();
            return new javafx.beans.property.SimpleStringProperty(gc == null ? "" : gc);
        });

        TableColumn<DonHang, Void> c6 = new TableColumn<>("");
        c6.setMaxWidth(75);
        c6.setCellFactory(col -> new TableCell<>() {
            final Button btn = new Button("👁 Xem");
            { btn.setStyle("-fx-background-color:#3498db; -fx-text-fill:white;" +
                           "-fx-background-radius:6; -fx-cursor:hand; -fx-font-size:11;");
              btn.setOnAction(e -> hienChiTiet(
                  getTableView().getItems().get(getIndex()), stage)); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty); setGraphic(empty ? null : btn); }
        });

        tv.getColumns().addAll(c1, c2, c3, c4, c5, c6);
        tv.getItems().addAll(list);
        if (list.isEmpty()) tv.setPlaceholder(new Label("Chưa có đơn hàng nào."));

        VBox root = new VBox(14, lblTitle, stats, locBox, tv);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:white;");
        VBox.setVgrow(tv, Priority.ALWAYS);

        stage.setScene(new Scene(root, 1100, 700));
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    private HBox statBox(String title, String value, String color) {
        Label t = new Label(title); t.setStyle("-fx-font-size:11; -fx-text-fill:#7f8c8d;");
        Label v = new Label(value); v.setStyle("-fx-font-size:14; -fx-font-weight:bold; -fx-text-fill:" + color + ";");
        VBox box = new VBox(2, t, v);
        box.setStyle("-fx-background-color:white; -fx-padding:8 14 8 14;" +
                     "-fx-background-radius:8; -fx-border-color:#eee; -fx-border-radius:8;");
        return new HBox(box);
    }

    private void hienChiTiet(DonHang don, Stage owner) {
        List<String> rows = donHangDAO.getChiTiet(don.getId());
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.initOwner(owner);
        a.setTitle("Chi tiết đơn #" + don.getId());
        a.setHeaderText(
            ("Mang về".equals(don.getLoaiDon()) ? "🛍 Mang về" : "🪑 Uống tại chỗ") +
            "  |  📍 " + don.getTenBan() + "\n⏰ " + don.getThoiGian());
        a.setContentText(String.join("\n", rows) +
            "\n\n💰 Tổng: " + String.format("%,d ₫", don.getTongTien()) +
            (don.getGhiChu() != null && !don.getGhiChu().isBlank() ? "\n📝 " + don.getGhiChu() : ""));
        a.getDialogPane().setMinWidth(380);
        a.showAndWait();
    }
}
