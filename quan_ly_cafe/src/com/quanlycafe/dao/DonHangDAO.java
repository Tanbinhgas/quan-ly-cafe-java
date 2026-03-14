package com.quanlycafe.dao;

import com.quanlycafe.model.DonHang;
import com.quanlycafe.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DonHangDAO {

    /**
     * @return kết quả dạng String[]:
     *   [0] = "OK"   → thành công
     *   [0] = "WARN" → lưu đơn OK nhưng không tìm thấy định mức trong MonNuoc
     *   [0] = "ERR"  → lỗi, rollback
     *   [1] = thông báo chi tiết
     */
    public String[] luuDonHang(String tenBan, String loaiDon,
                                Map<String, Integer> gioHang,
                                Map<String, Long>    giaTheo,
                                long tongTien, String ghiChu) {

        String sqlDon = "INSERT INTO DonHang (tenBan, loaiDon, tongTien, ghiChu) VALUES (?,?,?,?)";
        String sqlCT  = "INSERT INTO ChiTietDonHang (donHangId,tenMon,donGia,soLuong,thanhTien) VALUES (?,?,?,?,?)";
        String sqlDM  = "SELECT maNguyenLieu, dinhMucMoiLy FROM MonNuoc WHERE tenMon = ?";
        String sqlTru = "UPDATE NguyenLieu " +
                        "SET soLuongTon = CASE WHEN soLuongTon - ? < 0 THEN 0 ELSE soLuongTon - ? END, " +
                        "    ngayCapNhat = CONVERT(NVARCHAR(20), GETDATE(), 103) " +
                        "WHERE maNguyenLieu = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int donHangId;
            try (PreparedStatement ps = conn.prepareStatement(sqlDon, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, tenBan);
                ps.setString(2, loaiDon);
                ps.setLong(3, tongTien);
                ps.setString(4, ghiChu == null || ghiChu.isBlank() ? null : ghiChu);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (!keys.next()) { conn.rollback(); return new String[]{"ERR", "Không tạo được đơn hàng!"}; }
                donHangId = keys.getInt(1);
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlCT)) {
                for (Map.Entry<String, Integer> e : gioHang.entrySet()) {
                    String tenMon  = e.getKey();
                    int    soLuong = e.getValue();
                    long   donGia  = giaTheo.getOrDefault(tenMon, 0L);
                    ps.setInt(1, donHangId);
                    ps.setString(2, tenMon);
                    ps.setLong(3, donGia);
                    ps.setInt(4, soLuong);
                    ps.setLong(5, donGia * soLuong);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            Map<String, Double> canTru = new LinkedHashMap<>();
            boolean coNL = false;

            try (PreparedStatement ps = conn.prepareStatement(sqlDM)) {
                for (Map.Entry<String, Integer> e : gioHang.entrySet()) {
                    String tenMon  = e.getKey();
                    int    soLuong = e.getValue();
                    ps.setString(1, tenMon);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        coNL = true;
                        String  ma      = rs.getString("maNguyenLieu");
                        double  dinhMuc = rs.getDouble("dinhMucMoiLy");
                        double  canTruNL = dinhMuc * soLuong;
                        canTru.merge(ma, canTruNL, Double::sum);
                    }
                }
            }

            if (!canTru.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlTru)) {
                    for (Map.Entry<String, Double> e : canTru.entrySet()) {
                        double luong = e.getValue();
                        ps.setDouble(1, luong);
                        ps.setDouble(2, luong);
                        ps.setString(3, e.getKey());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit();

            if (!coNL) {
                return new String[]{"WARN",
                    "Đặt món thành công!\nNhưng không tìm thấy định mức trong bảng MonNuoc " +
                    "→ kho chưa được trừ.\n(Kiểm tra bảng MonNuoc đã chạy SQL chưa?)"};
            }

            StringBuilder sb = new StringBuilder();
            canTru.forEach((ma, luong) ->
                sb.append(String.format("  • %s: trừ %.3f\n", ma, luong)));
            return new String[]{"OK", sb.toString()};

        } catch (SQLException e) {
            System.err.println("Lỗi luuDonHang: " + e.getMessage());
            return new String[]{"ERR", "Lỗi DB: " + e.getMessage()};
        }
    }

    public List<DonHang> getAllDonHang() {
        List<DonHang> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT * FROM DonHang ORDER BY thoiGian DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new DonHang(
                    rs.getInt("id"),
                    rs.getString("tenBan"),
                    rs.getString("loaiDon"),
                    rs.getLong("tongTien"),
                    rs.getString("ghiChu"),
                    rs.getTimestamp("thoiGian") != null
                        ? rs.getTimestamp("thoiGian").toString().substring(0, 16) : "",
                    rs.getString("trangThai")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllDonHang: " + e.getMessage());
        }
        return list;
    }

    public List<String> getChiTiet(int donHangId) {
        List<String> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT tenMon,soLuong,donGia,thanhTien FROM ChiTietDonHang WHERE donHangId=?")) {
            ps.setInt(1, donHangId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rows.add(String.format("• %s  x%d  (%,d ₫)  =  %,d ₫",
                    rs.getString("tenMon"), rs.getInt("soLuong"),
                    rs.getLong("donGia"), rs.getLong("thanhTien")));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getChiTiet: " + e.getMessage());
        }
        return rows;
    }
}
