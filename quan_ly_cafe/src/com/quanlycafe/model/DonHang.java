package com.quanlycafe.model;

public class DonHang {
    private int    id;
    private String tenBan;
    private String loaiDon;
    private long   tongTien;
    private String ghiChu;
    private String thoiGian;
    private String trangThai;

    public DonHang() {}

    public DonHang(int id, String tenBan, String loaiDon,
                   long tongTien, String ghiChu,
                   String thoiGian, String trangThai) {
        this.id = id; this.tenBan = tenBan; this.loaiDon = loaiDon;
        this.tongTien = tongTien; this.ghiChu = ghiChu;
        this.thoiGian = thoiGian; this.trangThai = trangThai;
    }

    public int    getId()        { return id; }
    public String getTenBan()    { return tenBan; }
    public String getLoaiDon()   { return loaiDon; }
    public long   getTongTien()  { return tongTien; }
    public String getGhiChu()    { return ghiChu; }
    public String getThoiGian()  { return thoiGian; }
    public String getTrangThai() { return trangThai; }

    public void setId(int v)         { this.id = v; }
    public void setTenBan(String v)  { this.tenBan = v; }
    public void setLoaiDon(String v) { this.loaiDon = v; }
    public void setTongTien(long v)  { this.tongTien = v; }
    public void setGhiChu(String v)  { this.ghiChu = v; }
    public void setThoiGian(String v){ this.thoiGian = v; }
    public void setTrangThai(String v){ this.trangThai = v; }
}
