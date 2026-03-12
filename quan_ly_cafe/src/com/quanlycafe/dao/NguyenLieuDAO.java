package com.quanlycafe.dao;

import com.quanlycafe.model.NguyenLieu;
import com.quanlycafe.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NguyenLieuDAO {

    public List<NguyenLieu> getAllNguyenLieu() {
        List<NguyenLieu> ds = new ArrayList<>();
        String sql = "SELECT * FROM NguyenLieu ORDER BY maNguyenLieu";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ds.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Lỗi lấy nguyên liệu: " + e.getMessage());
        }
        return ds;
    }

    public List<NguyenLieu> timKiem(String tuKhoa) {
        List<NguyenLieu> ds = new ArrayList<>();
        String sql = "SELECT * FROM NguyenLieu WHERE tenNguyenLieu LIKE ? OR maNguyenLieu LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) ds.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Lỗi tìm nguyên liệu: " + e.getMessage());
        }
        return ds;
    }

    public List<NguyenLieu> getNguyenLieuTonThap() {
        List<NguyenLieu> ds = new ArrayList<>();
        String sql = "SELECT * FROM NguyenLieu WHERE soLuongTon < soLuongToiThieu";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ds.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
        return ds;
    }

    public boolean maDaTonTai(String ma) {
        String sql = "SELECT COUNT(*) FROM NguyenLieu WHERE maNguyenLieu = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return false;
    }

    public boolean them(NguyenLieu nl) {
        String sql = "INSERT INTO NguyenLieu (maNguyenLieu,tenNguyenLieu,donViTinh," +
                     "soLuongTon,soLuongToiThieu,ngayCapNhat,ghiChu) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nl.getMaNguyenLieu());
            ps.setString(2, nl.getTenNguyenLieu());
            ps.setString(3, nl.getDonViTinh());
            ps.setDouble(4, nl.getSoLuongTon());
            ps.setDouble(5, nl.getSoLuongToiThieu());
            ps.setString(6, nl.getNgayCapNhat());
            ps.setString(7, nl.getGhiChu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    public boolean sua(NguyenLieu nl) {
        String sql = "UPDATE NguyenLieu SET maNguyenLieu=?,tenNguyenLieu=?,donViTinh=?," +
                     "soLuongTon=?,soLuongToiThieu=?,ngayCapNhat=?,ghiChu=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nl.getMaNguyenLieu());
            ps.setString(2, nl.getTenNguyenLieu());
            ps.setString(3, nl.getDonViTinh());
            ps.setDouble(4, nl.getSoLuongTon());
            ps.setDouble(5, nl.getSoLuongToiThieu());
            ps.setString(6, nl.getNgayCapNhat());
            ps.setString(7, nl.getGhiChu());
            ps.setInt(8, nl.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    public boolean xoa(int id) {
        String sql = "DELETE FROM NguyenLieu WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    private NguyenLieu map(ResultSet rs) throws SQLException {
        return new NguyenLieu(rs.getInt("id"), rs.getString("maNguyenLieu"),
            rs.getString("tenNguyenLieu"), rs.getString("donViTinh"),
            rs.getDouble("soLuongTon"), rs.getDouble("soLuongToiThieu"),
            rs.getString("ngayCapNhat"), rs.getString("ghiChu"));
    }
}