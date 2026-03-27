package com.quanlycafe.controller;

import com.quanlycafe.dao.NhanVienDAO;
import com.quanlycafe.model.NhanVien;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NhanVienFormController {

    @FXML private Label             lblTieuDe;
    @FXML private TextField         txtMaNV;
    @FXML private TextField         txtHoTen;
    @FXML private ComboBox<String>  cmbChucVu;
    @FXML private TextField         txtLuong;
    @FXML private DatePicker        dpNgayVaoLam;
    @FXML private ComboBox<String>  cmbTrangThai;
    @FXML private Label             lblLoi;
    @FXML private Button            btnLuu;

    private NhanVien nvDangSua = null;
    private boolean  daLuu     = false;

    // DB dùng DATE → cần format yyyy-MM-dd
    private static final DateTimeFormatter FMT_DB      = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // Hiển thị cho người dùng dạng dd/MM/yyyy
    private static final DateTimeFormatter FMT_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        cmbChucVu.setItems(FXCollections.observableArrayList(
            "Quản lý", "Thu ngân", "Pha chế", "Phục vụ", "Bảo vệ", "Tạp vụ"
        ));
        cmbTrangThai.setItems(FXCollections.observableArrayList(
            "Đang làm", "Nghỉ phép", "Đã nghỉ"
        ));
        cmbTrangThai.setValue("Đang làm");

        // Mặc định ngày hôm nay
        dpNgayVaoLam.setValue(LocalDate.now());

        // Chỉ cho nhập số vào ô lương
        txtLuong.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) txtLuong.setText(oldVal);
        });
    }

    // Gọi từ NhanVienController khi SỬA
    public void setNhanVienDeSua(NhanVien nv) {
        this.nvDangSua = nv;
        lblTieuDe.setText("SỬA NHÂN VIÊN");
        btnLuu.setText("Cập nhật");

        txtMaNV.setText(nv.getMaNhanVien());
        txtMaNV.setEditable(false); // Mã NV không được sửa để tránh trùng lặp khóa chính
        txtHoTen.setText(nv.getHoTen());
        cmbChucVu.setValue(nv.getChucVu());
        txtLuong.setText(String.valueOf((long) nv.getLuongCoBan()));
        cmbTrangThai.setValue(nv.getTrangThai());

        // Parse ngày từ DB (yyyy-MM-dd) → DatePicker
        try {
            String ngay = nv.getNgayVaoLam();
            if (ngay != null && !ngay.isBlank()) {
                // Hỗ trợ cả 2 format: yyyy-MM-dd và dd/MM/yyyy
                if (ngay.contains("-")) {
                    dpNgayVaoLam.setValue(LocalDate.parse(ngay, FMT_DB));
                } else {
                    dpNgayVaoLam.setValue(LocalDate.parse(ngay, FMT_DISPLAY));
                }
            }
        } catch (Exception e) {
            dpNgayVaoLam.setValue(LocalDate.now());
        }
    }

    @FXML
    private void handleLuu() {
        lblLoi.setText("");

        String maNV    = txtMaNV.getText().trim();
        String hoTen   = txtHoTen.getText().trim();
        String chucVu  = cmbChucVu.getValue();
        String luongTx = txtLuong.getText().trim();
        String tthai   = cmbTrangThai.getValue();

        // Validate bắt buộc
        if (maNV.isEmpty() || hoTen.isEmpty() || chucVu == null || luongTx.isEmpty()) {
            lblLoi.setText("⚠ Vui lòng điền đầy đủ các trường bắt buộc (*).");
            return;
        }
        if (dpNgayVaoLam.getValue() == null) {
            lblLoi.setText("⚠ Vui lòng chọn ngày vào làm.");
            return;
        }

        // Validate lương
        double luong;
        try {
            luong = Double.parseDouble(luongTx);
            if (luong < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblLoi.setText("⚠ Lương không hợp lệ. Vui lòng nhập số dương.");
            return;
        }

        // Chuyển ngày sang format yyyy-MM-dd cho SQL Server DATE column
        String ngayDB = dpNgayVaoLam.getValue().format(FMT_DB);

        NhanVienDAO dao = new NhanVienDAO();

        if (nvDangSua == null) {
            // THÊM MỚI
            if (dao.maNhanVienDaTonTai(maNV)) {
                lblLoi.setText("⚠ Mã nhân viên \"" + maNV + "\" đã tồn tại.");
                return;
            }
            NhanVien nvMoi = new NhanVien(maNV, hoTen, chucVu, luong, ngayDB, tthai);
            if (dao.themNhanVien(nvMoi)) {
                daLuu = true;
                dongForm();
            } else {
                lblLoi.setText("✖ Thêm thất bại. Kiểm tra lại thông tin.");
            }
        } else {
            // SỬA
            nvDangSua.setHoTen(hoTen);
            nvDangSua.setChucVu(chucVu);
            nvDangSua.setLuongCoBan(luong);
            nvDangSua.setNgayVaoLam(ngayDB);
            nvDangSua.setTrangThai(tthai);

            if (dao.suaNhanVien(nvDangSua)) {
                daLuu = true;
                dongForm();
            } else {
                lblLoi.setText("✖ Cập nhật thất bại. Kiểm tra lại thông tin.");
            }
        }
    }

    @FXML
    private void handleHuy() { dongForm(); }

    public boolean isDaLuu() { return daLuu; }

    private void dongForm() {
        ((Stage) btnLuu.getScene().getWindow()).close();
    }
}
