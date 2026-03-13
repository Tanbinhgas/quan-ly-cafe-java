package com.quanlycafe.dao;

import com.quanlycafe.model.DonHang;
import com.quanlycafe.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DonHangDAO {

    /**
     * @param tenBan
     * @param loaiDon
     * @param gioHang
     * @param giaTheo
     * @param tongTien
     * @param ghiChu
     */
    public boolean luuDonHang(String tenBan, String loaiDon,
                               Map<String, Integer> gioHang,
                               Map<String, Long>    giaTheo,
                               long tongTien, String ghiChu) {
        String sqlDon = "INSERT INTO DonHang (tenBan, loaiDon, tongTien, ghiChu) VALUES (?,?,?,?)";
        String sqlCT  = "INSERT INTO ChiTietDonHang (donHangId, tenMon, donGia, soLuong, thanhTien) VALUES (?,?,?,?,?)";

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
                if (!keys.next()) { conn.rollback(); return false; }
                donHangId = keys.getInt(1);
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlCT)) {
                for (Map.Entry<String, Integer> e : gioHang.entrySet()) {
                    String tenMon  = e.getKey();
                    int    soLuong = e.getValue();
                    long   donGia  = giaTheo.getOrDefault(tenMon, 0L);
                    long   thanh   = donGia * soLuong;
                    ps.setInt(1, donHangId);
                    ps.setString(2, tenMon);
                    ps.setLong(3, donGia);
                    ps.setInt(4, soLuong);
                    ps.setLong(5, thanh);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi lưu đơn hàng: " + e.getMessage());
            return false;
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
                        ? rs.getTimestamp("thoiGian").toString().substring(0, 16)
                        : "",
                    rs.getString("trangThai")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy lịch sử đơn: " + e.getMessage());
        }
        return list;
    }

    public List<String> getChiTiet(int donHangId) {
        List<String> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT tenMon, soLuong, donGia, thanhTien " +
                 "FROM ChiTietDonHang WHERE donHangId = ?")) {
            ps.setInt(1, donHangId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rows.add(String.format("• %s  x%d  (%,d ₫)  =  %,d ₫",
                    rs.getString("tenMon"),
                    rs.getInt("soLuong"),
                    rs.getLong("donGia"),
                    rs.getLong("thanhTien")));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy chi tiết đơn: " + e.getMessage());
        }
        return rows;
    }
}
