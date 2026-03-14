package com.quanlycafe.model;

public class NguyenLieu {
    private int    id;
    private String maNguyenLieu;
    private String tenNguyenLieu;
    private String donViTinh;
    private double soLuongTon;
    private double soLuongToiThieu;
    private String ngayCapNhat;
    private String ghiChu;

    // Constructor rỗng (bắt buộc cho NguyenLieuDAO.map())
    public NguyenLieu() {}

    // Constructor đầy đủ (có id)
    public NguyenLieu(int id, String maNguyenLieu, String tenNguyenLieu,
                      String donViTinh, double soLuongTon, double soLuongToiThieu,
                      String ngayCapNhat, String ghiChu) {
        this.id = id;
        this.maNguyenLieu = maNguyenLieu;
        this.tenNguyenLieu = tenNguyenLieu;
        this.donViTinh = donViTinh;
        this.soLuongTon = soLuongTon;
        this.soLuongToiThieu = soLuongToiThieu;
        this.ngayCapNhat = ngayCapNhat;
        this.ghiChu = ghiChu;
    }

    // Constructor không id (dùng khi thêm mới)
    public NguyenLieu(String maNguyenLieu, String tenNguyenLieu,
                      String donViTinh, double soLuongTon, double soLuongToiThieu,
                      String ngayCapNhat, String ghiChu) {
        this.maNguyenLieu = maNguyenLieu;
        this.tenNguyenLieu = tenNguyenLieu;
        this.donViTinh = donViTinh;
        this.soLuongTon = soLuongTon;
        this.soLuongToiThieu = soLuongToiThieu;
        this.ngayCapNhat = ngayCapNhat;
        this.ghiChu = ghiChu;
    }

    public int    getId()                      { return id; }
    public void   setId(int id)                { this.id = id; }
    public String getMaNguyenLieu()            { return maNguyenLieu; }
    public void   setMaNguyenLieu(String v)    { this.maNguyenLieu = v; }
    public String getTenNguyenLieu()           { return tenNguyenLieu; }
    public void   setTenNguyenLieu(String v)   { this.tenNguyenLieu = v; }
    public String getDonViTinh()               { return donViTinh; }
    public void   setDonViTinh(String v)       { this.donViTinh = v; }
    public double getSoLuongTon()              { return soLuongTon; }
    public void   setSoLuongTon(double v)      { this.soLuongTon = v; }
    public double getSoLuongToiThieu()         { return soLuongToiThieu; }
    public void   setSoLuongToiThieu(double v) { this.soLuongToiThieu = v; }
    public String getNgayCapNhat()             { return ngayCapNhat; }
    public void   setNgayCapNhat(String v)     { this.ngayCapNhat = v; }
    public String getGhiChu()                  { return ghiChu; }
    public void   setGhiChu(String v)          { this.ghiChu = v; }

    public boolean isTonThap() { return soLuongTon < soLuongToiThieu; }
}
