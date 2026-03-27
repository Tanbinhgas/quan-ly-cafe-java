package com.quanlycafe.dao;

import com.quanlycafe.model.NhanVien;
import com.quanlycafe.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY maNhanVien";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NhanVien nv = new NhanVien(
                    rs.getInt("id"),
                    rs.getString("maNhanVien"),
                    rs.getString("hoTen"),
                    rs.getString("chucVu"),
                    rs.getDouble("luongCoBan"),
                    rs.getString("ngayVaoLam"),
                    rs.getString("trangThai")
                );
                danhSach.add(nv);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách nhân viên: " + e.getMessage());
        }
        return danhSach;
    }

    // Kiểm tra mã nhân viên đã tồn tại chưa
    public boolean maNhanVienDaTonTai(String maNhanVien) {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE maNhanVien = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra mã NV: " + e.getMessage());
        }
        return false;
    }

    // Thêm nhân viên mới
    public boolean themNhanVien(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (maNhanVien, hoTen, chucVu, luongCoBan, ngayVaoLam, trangThai) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getMaNhanVien());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getChucVu());
            ps.setDouble(4, nv.getLuongCoBan());
            ps.setString(5, nv.getNgayVaoLam());
            ps.setString(6, nv.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm nhân viên: " + e.getMessage());
            return false;
        }
    }

    // Sửa thông tin nhân viên
    public boolean suaNhanVien(NhanVien nv) {
        String sql = "UPDATE NhanVien SET maNhanVien=?, hoTen=?, chucVu=?, "
                   + "luongCoBan=?, ngayVaoLam=?, trangThai=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getMaNhanVien());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getChucVu());
            ps.setDouble(4, nv.getLuongCoBan());
            ps.setString(5, nv.getNgayVaoLam());
            ps.setString(6, nv.getTrangThai());
            ps.setInt(7, nv.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi sửa nhân viên: " + e.getMessage());
            return false;
        }
    }

    // Xóa nhân viên
    public boolean xoaNhanVien(int id) {
        String sql = "DELETE FROM NhanVien WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa nhân viên: " + e.getMessage());
            return false;
        }
    }
}
