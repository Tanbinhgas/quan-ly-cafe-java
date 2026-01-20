package com.quanlycafe.controller;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.Collectors;

import com.quanlycafe.dao.NhanVienDAO;
import com.quanlycafe.model.NhanVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class NhanVienController {

    @FXML
    private TextField txtTimKiem;
    @FXML
    private TableView<NhanVien> tblNhanVien;
    @FXML
    private TableColumn<NhanVien, String> colMaNV;
    @FXML
    private TableColumn<NhanVien, String> colHoTen;
    @FXML
    private TableColumn<NhanVien, String> colChucVu;
    @FXML
    private TableColumn<NhanVien, Double> colLuong;
    @FXML
    private TableColumn<NhanVien, String> colNgayVaoLam;
    @FXML
    private TableColumn<NhanVien, String> colTrangThai;

    private ObservableList<NhanVien> danhSach = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colChucVu.setCellValueFactory(new PropertyValueFactory<>("chucVu"));
        colLuong.setCellValueFactory(new PropertyValueFactory<>("luongCoBan"));
        colNgayVaoLam.setCellValueFactory(new PropertyValueFactory<>("ngayVaoLam"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colLuong.setCellFactory(column -> new javafx.scene.control.TableCell<NhanVien, Double>() {
            private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            @Override
            protected void updateItem(Double luong, boolean empty) {
                super.updateItem(luong, empty);
                if (empty || luong == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(luong));
                }
            }
        });
        loadDanhSach();

        txtTimKiem.textProperty().addListener((observable, oldValue, newValue) -> {
            timKiem(newValue.trim().toLowerCase());
        });
    }

    private void loadDanhSach() {
        danhSach.clear();
        danhSach.addAll(new NhanVienDAO().getAllNhanVien());
        tblNhanVien.setItems(danhSach);
    }

    private void timKiem(String tuKhoa) {
        if (tuKhoa.isEmpty()) {
            tblNhanVien.setItems(danhSach);
        } else {
            ObservableList<NhanVien> ketQuaLoc = danhSach.stream()
                    .filter(nv -> nv.getMaNhanVien().toLowerCase().contains(tuKhoa) ||
                            nv.getHoTen().toLowerCase().contains(tuKhoa))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            tblNhanVien.setItems(ketQuaLoc);
        }
    }
}
