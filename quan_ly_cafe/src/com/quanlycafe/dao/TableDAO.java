package com.quanlycafe.dao;

import com.quanlycafe.model.Table;
import com.quanlycafe.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {

    public List<Table> getAllTables() {
        List<Table> list = new ArrayList<>();
        String sql = "SELECT * FROM Ban ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Table(
                    rs.getInt("id"),
                    rs.getString("table_name"),
                    rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách bàn: " + e.getMessage());
        }
        return list;
    }

    public boolean capNhatTrangThai(int id, String status) {
        String sql = "UPDATE Ban SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật trạng thái bàn: " + e.getMessage());
            return false;
        }
    }

    public boolean themBan(String tableName) {
        String sql = "INSERT INTO Ban (table_name, status) VALUES (?, N'Trống')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tableName);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi thêm bàn: " + e.getMessage());
            return false;
        }
    }

    public boolean xoaBan(int id) {
        String sql = "DELETE FROM Ban WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi xóa bàn: " + e.getMessage());
            return false;
        }
    }
}
