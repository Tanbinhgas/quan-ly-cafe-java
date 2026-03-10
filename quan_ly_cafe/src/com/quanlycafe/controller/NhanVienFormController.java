package com.quanlycafe.controller;

import com.quanlycafe.dao.NhanVienDAO;
import com.quanlycafe.model.NhanVien;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class NhanVienFormController {

    @FXML private Label lblTieuDe;
    @FXML private TextField txtMaNV;
    @FXML private TextField txtHoTen;
    @FXML private ComboBox<String> cmbChucVu;
    @FXML private TextField txtLuong;
    @FXML private TextField txtNgayVaoLam;
    @FXML private ComboBox<String> cmbTrangThai;
    @FXML private Label lblLoi;
    @FXML private Button btnLuu;

    private NhanVien nvDangSua = null;
    private boolean daLuu = false;

    @FXML
    private void initialize() {
        cmbChucVu.setItems(FXCollections.observableArrayList(
            "Quản lý", "Thu ngân", "Pha chế", "Phục vụ", "Bảo vệ", "Tạp vụ"
        ));
        cmbTrangThai.setItems(FXCollections.observableArrayList(
            "Đang làm", "Nghỉ phép", "Đã nghỉ"
        ));
        cmbTrangThai.setValue("Đang làm");

        txtLuong.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                txtLuong.setText(oldVal);
            }
        });
    }

    public void setNhanVienDeSua(NhanVien nv) {
        this.nvDangSua = nv;
        lblTieuDe.setText("SỬA NHÂN VIÊN");
        btnLuu.setText("Cập nhật");

        txtMaNV.setText(nv.getMaNhanVien());
        txtMaNV.setEditable(false);
        txtHoTen.setText(nv.getHoTen());
        cmbChucVu.setValue(nv.getChucVu());
        txtLuong.setText(String.valueOf((long) nv.getLuongCoBan()));
        txtNgayVaoLam.setText(nv.getNgayVaoLam());
        cmbTrangThai.setValue(nv.getTrangThai());
    }

    @FXML
    private void handleLuu() {
        lblLoi.setText("");

        String maNV    = txtMaNV.getText().trim();
        String hoTen   = txtHoTen.getText().trim();
        String chucVu  = cmbChucVu.getValue();
        String luongTx = txtLuong.getText().trim();
        String ngay    = txtNgayVaoLam.getText().trim();
        String tthai   = cmbTrangThai.getValue();

        if (maNV.isEmpty() || hoTen.isEmpty() || chucVu == null || luongTx.isEmpty() || ngay.isEmpty()) {
            lblLoi.setText("⚠ Vui lòng điền đầy đủ các trường bắt buộc (*).");
            return;
        }

        double luong;
        try {
            luong = Double.parseDouble(luongTx);
            if (luong < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblLoi.setText("⚠ Lương không hợp lệ. Vui lòng nhập số dương.");
            return;
        }

        NhanVienDAO dao = new NhanVienDAO();

        if (nvDangSua == null) {
            if (dao.maNhanVienDaTonTai(maNV)) {
                lblLoi.setText("⚠ Mã nhân viên \"" + maNV + "\" đã tồn tại.");
                return;
            }
            NhanVien nvMoi = new NhanVien(maNV, hoTen, chucVu, luong, ngay, tthai);
            if (dao.themNhanVien(nvMoi)) {
                daLuu = true;
                dongForm();
            } else {
                lblLoi.setText("✖ Thêm thất bại. Vui lòng thử lại.");
            }
        } else {
            nvDangSua.setHoTen(hoTen);
            nvDangSua.setChucVu(chucVu);
            nvDangSua.setLuongCoBan(luong);
            nvDangSua.setNgayVaoLam(ngay);
            nvDangSua.setTrangThai(tthai);

            if (dao.suaNhanVien(nvDangSua)) {
                daLuu = true;
                dongForm();
            } else {
                lblLoi.setText("✖ Cập nhật thất bại. Vui lòng thử lại.");
            }
        }
    }

    @FXML
    private void handleHuy() {
        dongForm();
    }

    public boolean isDaLuu() {
        return daLuu;
    }

    private void dongForm() {
        ((Stage) btnLuu.getScene().getWindow()).close();
    }
}
