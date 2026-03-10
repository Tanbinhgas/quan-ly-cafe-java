package com.quanlycafe.controller;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.quanlycafe.dao.NhanVienDAO;
import com.quanlycafe.model.NhanVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NhanVienController {

    @FXML private TextField txtTimKiem;
    @FXML private TableView<NhanVien> tblNhanVien;
    @FXML private TableColumn<NhanVien, String> colMaNV;
    @FXML private TableColumn<NhanVien, String> colHoTen;
    @FXML private TableColumn<NhanVien, String> colChucVu;
    @FXML private TableColumn<NhanVien, Double> colLuong;
    @FXML private TableColumn<NhanVien, String> colNgayVaoLam;
    @FXML private TableColumn<NhanVien, String> colTrangThai;

    @FXML private Button btnThem;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    private ObservableList<NhanVien> danhSach = FXCollections.observableArrayList();
    private final NhanVienDAO dao = new NhanVienDAO();

    @FXML
    private void initialize() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colChucVu.setCellValueFactory(new PropertyValueFactory<>("chucVu"));
        colLuong.setCellValueFactory(new PropertyValueFactory<>("luongCoBan"));
        colNgayVaoLam.setCellValueFactory(new PropertyValueFactory<>("ngayVaoLam"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        colLuong.setCellFactory(col -> new TableCell<NhanVien, Double>() {
            private final NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
            @Override
            protected void updateItem(Double luong, boolean empty) {
                super.updateItem(luong, empty);
                setText(empty || luong == null ? null : fmt.format(luong));
            }
        });

        tblNhanVien.setRowFactory(tv -> new TableRow<NhanVien>() {
            @Override
            protected void updateItem(NhanVien nv, boolean empty) {
                super.updateItem(nv, empty);
                if (empty || nv == null) {
                    setStyle("");
                } else if ("Đã nghỉ".equals(nv.getTrangThai())) {
                    setStyle("-fx-background-color: #ffebee;");
                } else if ("Nghỉ phép".equals(nv.getTrangThai())) {
                    setStyle("-fx-background-color: #fff8e1;");
                } else {
                    setStyle("");
                }
            }
        });

        btnSua.setDisable(true);
        btnXoa.setDisable(true);
        tblNhanVien.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                boolean coChon = newVal != null;
                btnSua.setDisable(!coChon);
                btnXoa.setDisable(!coChon);
            }
        );

        loadDanhSach();

        txtTimKiem.textProperty().addListener((obs, old, newVal) ->
            timKiem(newVal.trim().toLowerCase())
        );
    }

    private void loadDanhSach() {
        danhSach.clear();
        danhSach.addAll(dao.getAllNhanVien());
        tblNhanVien.setItems(danhSach);
    }

    private void timKiem(String tuKhoa) {
        if (tuKhoa.isEmpty()) {
            tblNhanVien.setItems(danhSach);
        } else {
            ObservableList<NhanVien> ketQua = danhSach.stream()
                .filter(nv -> nv.getMaNhanVien().toLowerCase().contains(tuKhoa)
                           || nv.getHoTen().toLowerCase().contains(tuKhoa))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
            tblNhanVien.setItems(ketQua);
        }
    }

    @FXML
    private void handleThem() {
        boolean daLuu = moCuaSoForm(null);
        if (daLuu) {
            loadDanhSach();
            hienThongBao("Thành công", "Thêm nhân viên thành công!", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleSua() {
        NhanVien nvChon = tblNhanVien.getSelectionModel().getSelectedItem();
        if (nvChon == null) return;

        boolean daLuu = moCuaSoForm(nvChon);
        if (daLuu) {
            loadDanhSach();
            hienThongBao("Thành công", "Cập nhật nhân viên thành công!", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleXoa() {
        NhanVien nvChon = tblNhanVien.getSelectionModel().getSelectedItem();
        if (nvChon == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Bạn có chắc muốn xóa nhân viên này?");
        confirm.setContentText("Mã NV: " + nvChon.getMaNhanVien()
            + "\nHọ tên: " + nvChon.getHoTen());

        Optional<ButtonType> ketQua = confirm.showAndWait();
        if (ketQua.isPresent() && ketQua.get() == ButtonType.OK) {
            if (dao.xoaNhanVien(nvChon.getId())) {
                danhSach.remove(nvChon);
                hienThongBao("Thành công", "Đã xóa nhân viên " + nvChon.getHoTen(), Alert.AlertType.INFORMATION);
            } else {
                hienThongBao("Lỗi", "Xóa thất bại. Vui lòng thử lại.", Alert.AlertType.ERROR);
            }
        }
    }

    private boolean moCuaSoForm(NhanVien nvDeSua) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/quanlycafe/NhanVienForm.fxml")
            );
            Parent root = loader.load();

            NhanVienFormController formCtrl = loader.getController();
            if (nvDeSua != null) {
                formCtrl.setNhanVienDeSua(nvDeSua);
            }

            Stage stage = new Stage();
            stage.setTitle(nvDeSua == null ? "Thêm nhân viên" : "Sửa nhân viên");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            return formCtrl.isDaLuu();

        } catch (Exception e) {
            e.printStackTrace();
            hienThongBao("Lỗi", "Không mở được form: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    private void hienThongBao(String tieu, String noidung, Alert.AlertType loai) {
        Alert alert = new Alert(loai);
        alert.setTitle(tieu);
        alert.setHeaderText(null);
        alert.setContentText(noidung);
        alert.showAndWait();
    }
}
