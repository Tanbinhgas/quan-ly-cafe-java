package com.quanlycafe.dao;

import com.quanlycafe.model.NguyenLieu;
import com.quanlycafe.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NguyenLieuDAO {

    // ── LẤY TẤT CẢ ───────────────────────────────────────────────────────────
    public List<NguyenLieu> getAllNguyenLieu() {
        List<NguyenLieu> list = new ArrayList<>();
        String sql = "SELECT * FROM NguyenLieu ORDER BY maNguyenLieu";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("Lỗi getAllNguyenLieu: " + e.getMessage()); }
        return list;
    }

    // ── TÌM KIẾM ─────────────────────────────────────────────────────────────
    public List<NguyenLieu> timKiem(String tuKhoa) {
        List<NguyenLieu> list = new ArrayList<>();
        String sql = "SELECT * FROM NguyenLieu WHERE tenNguyenLieu LIKE ? OR maNguyenLieu LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("Lỗi timKiem: " + e.getMessage()); }
        return list;
    }

    // ── KHO SẮP HẾT ──────────────────────────────────────────────────────────
    public List<NguyenLieu> getNguyenLieuTonThap() {
        List<NguyenLieu> list = new ArrayList<>();
        String sql = "SELECT * FROM NguyenLieu WHERE soLuongTon < soLuongToiThieu";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("Lỗi getTonThap: " + e.getMessage()); }
        return list;
    }

    // ── KIỂM TRA MÃ ──────────────────────────────────────────────────────────
    public boolean maDaTonTai(String ma) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT COUNT(*) FROM NguyenLieu WHERE maNguyenLieu = ?")) {
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    // ── THÊM ─────────────────────────────────────────────────────────────────
    public boolean them(NguyenLieu nl) {
        String sql = "INSERT INTO NguyenLieu (maNguyenLieu,tenNguyenLieu,donViTinh," +
                     "soLuongTon,soLuongToiThieu,ngayCapNhat,ghiChu) VALUES (?,?,?,?,?,?,?)";
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
        } catch (SQLException e) { System.err.println("Lỗi them: " + e.getMessage()); return false; }
    }

    // ── SỬA ──────────────────────────────────────────────────────────────────
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
        } catch (SQLException e) { System.err.println("Lỗi sua: " + e.getMessage()); return false; }
    }

    public boolean xoa(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "DELETE FROM NguyenLieu WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Lỗi xoa: " + e.getMessage()); return false; }
    }

    public List<String> truNguyenLieu(Map<String, Integer> gioHang) {
        List<String> canhBao = new ArrayList<>();

        String sqlCT = "SELECT nl.id, nl.tenNguyenLieu, nl.donViTinh, " +
                       "nl.soLuongTon, nl.soLuongToiThieu, ct.soLuong " +
                       "FROM CongThucMon ct " +
                       "JOIN MonAn ma ON ct.monAnId = ma.id " +
                       "JOIN NguyenLieu nl ON ct.nguyenLieuId = nl.id " +
                       "WHERE ma.tenMon = ?";

        String sqlUpdate = "UPDATE NguyenLieu SET soLuongTon = soLuongTon - ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            for (Map.Entry<String, Integer> entry : gioHang.entrySet()) {
                String tenMon  = entry.getKey();
                int    soLy    = entry.getValue();

                try (PreparedStatement psCT = conn.prepareStatement(sqlCT)) {
                    psCT.setString(1, tenMon);
                    ResultSet rs = psCT.executeQuery();

                    while (rs.next()) {
                        int    nlId       = rs.getInt("id");
                        String tenNL      = rs.getString("tenNguyenLieu");
                        String dvt        = rs.getString("donViTinh");
                        double tonHienTai = rs.getDouble("soLuongTon");
                        double toiThieu   = rs.getDouble("soLuongToiThieu");
                        double soLuongDung = rs.getDouble("soLuong") * soLy;

                        double conLai = Math.max(0, tonHienTai - soLuongDung);

                        try (PreparedStatement psUp = conn.prepareStatement(sqlUpdate)) {
                            psUp.setDouble(1, soLuongDung);
                            psUp.setInt(2, nlId);
                            psUp.executeUpdate();
                        }

                        if (conLai < toiThieu) {
                            canhBao.add(String.format("⚠ %s còn %.2f %s (tối thiểu %.2f %s)",
                                tenNL, conLai, dvt, toiThieu, dvt));
                        }
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("Lỗi trừ nguyên liệu: " + e.getMessage());
            canhBao.add("❌ Lỗi khi cập nhật kho: " + e.getMessage());
        }

        return canhBao;
    }

    private NguyenLieu map(ResultSet rs) throws SQLException {
        return new NguyenLieu(
            rs.getInt("id"),
            rs.getString("maNguyenLieu"),
            rs.getString("tenNguyenLieu"),
            rs.getString("donViTinh"),
            rs.getDouble("soLuongTon"),
            rs.getDouble("soLuongToiThieu"),
            rs.getString("ngayCapNhat"),
            rs.getString("ghiChu")
        );
    }
}
