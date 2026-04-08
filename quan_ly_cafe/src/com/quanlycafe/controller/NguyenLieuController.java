package com.quanlycafe.controller;

import com.quanlycafe.dao.NguyenLieuDAO;
import com.quanlycafe.model.NguyenLieu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NguyenLieuController {

    @FXML private TextField txtTimKiem;
    @FXML private CheckBox  chkTonThap;
    @FXML private TableView<NguyenLieu> tblNguyenLieu;
    @FXML private TableColumn<NguyenLieu, String> colMaNL;
    @FXML private TableColumn<NguyenLieu, String> colTenNL;
    @FXML private TableColumn<NguyenLieu, String> colDonViTinh;
    @FXML private TableColumn<NguyenLieu, Double> colSoLuongTon;
    @FXML private TableColumn<NguyenLieu, Double> colToiThieu;
    @FXML private TableColumn<NguyenLieu, String> colNgayCapNhat;
    @FXML private TableColumn<NguyenLieu, String> colGhiChu;

    private final NguyenLieuDAO dao = new NguyenLieuDAO();
    private ObservableList<NguyenLieu> danhSach;
    private FilteredList<NguyenLieu> filtered;

    @FXML
    public void initialize() {
        colMaNL.setCellValueFactory(new PropertyValueFactory<>("maNguyenLieu"));
        colTenNL.setCellValueFactory(new PropertyValueFactory<>("tenNguyenLieu"));
        colDonViTinh.setCellValueFactory(new PropertyValueFactory<>("donViTinh"));
        colSoLuongTon.setCellValueFactory(new PropertyValueFactory<>("soLuongTon"));
        colToiThieu.setCellValueFactory(new PropertyValueFactory<>("soLuongToiThieu"));
        colNgayCapNhat.setCellValueFactory(new PropertyValueFactory<>("ngayCapNhat"));
        colGhiChu.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));

        tblNguyenLieu.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(NguyenLieu item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); return; }
                setStyle(item.isTonThap() ? "-fx-background-color:#fff3cd;" : "");
            }
        });

        taiDuLieu();
        txtTimKiem.textProperty().addListener((obs, o, n) -> applyFilter());
    }

    private void taiDuLieu() {
        danhSach = FXCollections.observableArrayList(dao.getAllNguyenLieu());
        filtered  = new FilteredList<>(danhSach, p -> true);
        tblNguyenLieu.setItems(filtered);
        applyFilter();
    }

    @FXML
    public void applyFilter() {
        String kw     = txtTimKiem.getText().toLowerCase().trim();
        boolean chyTon = chkTonThap != null && chkTonThap.isSelected();
        filtered.setPredicate(nl -> {
            boolean matchKw  = kw.isEmpty()
                || nl.getTenNguyenLieu().toLowerCase().contains(kw)
                || nl.getMaNguyenLieu().toLowerCase().contains(kw);
            return matchKw && (!chyTon || nl.isTonThap());
        });
    }

    @FXML private void handleThem() { moForm(null); }

    @FXML
    private void handleSua() {
        NguyenLieu sel = tblNguyenLieu.getSelectionModel().getSelectedItem();
        if (sel == null) { alert(Alert.AlertType.WARNING, "Vui lòng chọn nguyên liệu cần sửa."); return; }
        moForm(sel);
    }

    @FXML
    private void handleXoa() {
        NguyenLieu sel = tblNguyenLieu.getSelectionModel().getSelectedItem();
        if (sel == null) { alert(Alert.AlertType.WARNING, "Vui lòng chọn nguyên liệu cần xóa."); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
            "Xóa nguyên liệu \"" + sel.getTenNguyenLieu() + "\"?");
        c.setHeaderText(null);
        c.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                if (dao.xoa(sel.getMaNguyenLieu())) taiDuLieu();
                else alert(Alert.AlertType.ERROR, "Xóa thất bại!");
            }
        });
    }

    private void moForm(NguyenLieu nl) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/quanlycafe/NguyenLieuForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(nl == null ? "Thêm nguyên liệu" : "Sửa nguyên liệu");
            stage.initModality(Modality.APPLICATION_MODAL);
            NguyenLieuFormController ctrl = loader.getController();
            ctrl.setNguyenLieu(nl);
            ctrl.setOnSaved(this::taiDuLieu);
            stage.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void alert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg); a.setHeaderText(null); a.showAndWait();
    }
}