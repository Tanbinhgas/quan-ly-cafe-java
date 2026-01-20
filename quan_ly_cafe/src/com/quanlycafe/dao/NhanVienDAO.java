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
            e.printStackTrace();
        }

        return danhSach;
    }

    public static void main(String[] args) {
        NhanVienDAO dao = new NhanVienDAO();
        List<NhanVien> ds = dao.getAllNhanVien();

        if (ds.isEmpty()) {
            System.out.println("Không có nhân viên nào trong database.");
        } else {
            System.out.println("Danh sách nhân viên:");
            for (NhanVien nv : ds) {
                System.out.println(nv);
            }
        }
    }
}
