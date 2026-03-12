package com.quanlycafe.controller;

import com.quanlycafe.dao.NguyenLieuDAO;
import com.quanlycafe.model.NguyenLieu;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NguyenLieuFormController {

    @FXML private TextField txtMaNguyenLieu;
    @FXML private TextField txtTenNguyenLieu;
    @FXML private ComboBox<String> cboDonViTinh;
    @FXML private TextField txtSoLuongTon;
    @FXML private TextField txtSoLuongToiThieu;
    @FXML private TextField txtNgayCapNhat;
    @FXML private TextArea  txtGhiChu;

    private NguyenLieu nlHienTai;
    private Runnable onSaved;
    private final NguyenLieuDAO dao = new NguyenLieuDAO();

    @FXML
    public void initialize() {
        cboDonViTinh.setItems(FXCollections.observableArrayList(
            "kg", "lít", "hộp", "gói", "cái", "chai", "túi", "thùng"));
        cboDonViTinh.setValue("kg");
        txtNgayCapNhat.setText(
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    public void setNguyenLieu(NguyenLieu nl) {
        this.nlHienTai = nl;
        if (nl != null) {
            txtMaNguyenLieu.setText(nl.getMaNguyenLieu());
            txtMaNguyenLieu.setDisable(true);
            txtTenNguyenLieu.setText(nl.getTenNguyenLieu());
            cboDonViTinh.setValue(nl.getDonViTinh());
            txtSoLuongTon.setText(String.valueOf(nl.getSoLuongTon()));
            txtSoLuongToiThieu.setText(String.valueOf(nl.getSoLuongToiThieu()));
            txtNgayCapNhat.setText(nl.getNgayCapNhat());
            txtGhiChu.setText(nl.getGhiChu());
        }
    }

    public void setOnSaved(Runnable cb) { this.onSaved = cb; }

    @FXML
    private void handleLuu() {
        String ma  = txtMaNguyenLieu.getText().trim();
        String ten = txtTenNguyenLieu.getText().trim();
        if (ma.isEmpty() || ten.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Mã và tên không được để trống."); return;
        }
        double ton, toiThieu;
        try {
            ton      = Double.parseDouble(txtSoLuongTon.getText().trim());
            toiThieu = Double.parseDouble(txtSoLuongToiThieu.getText().trim());
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.WARNING, "Số lượng phải là số hợp lệ."); return;
        }
        boolean ok;
        if (nlHienTai == null) {
            if (dao.maDaTonTai(ma)) { alert(Alert.AlertType.WARNING, "Mã đã tồn tại."); return; }
            ok = dao.them(new NguyenLieu(ma, ten, cboDonViTinh.getValue(),
                ton, toiThieu, txtNgayCapNhat.getText().trim(), txtGhiChu.getText().trim()));
        } else {
            nlHienTai.setTenNguyenLieu(ten);
            nlHienTai.setDonViTinh(cboDonViTinh.getValue());
            nlHienTai.setSoLuongTon(ton);
            nlHienTai.setSoLuongToiThieu(toiThieu);
            nlHienTai.setNgayCapNhat(txtNgayCapNhat.getText().trim());
            nlHienTai.setGhiChu(txtGhiChu.getText().trim());
            ok = dao.sua(nlHienTai);
        }
        if (ok) { if (onSaved != null) onSaved.run(); dongCua(); }
        else alert(Alert.AlertType.ERROR, "Lưu thất bại!");
    }

    @FXML private void handleHuy() { dongCua(); }

    private void dongCua() { ((Stage) txtMaNguyenLieu.getScene().getWindow()).close(); }

    private void alert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg); a.setHeaderText(null); a.showAndWait();
    }
}