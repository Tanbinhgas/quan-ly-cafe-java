package com.quanlycafe.model;

public class NhanVien {
    private int id;
    private String maNhanVien;
    private String hoTen;
    private String chucVu;
    private double luongCoBan;
    private String ngayVaoLam;
    private String trangThai;

    public NhanVien(int id, String maNhanVien, String hoTen, String chucVu,
                    double luongCoBan, String ngayVaoLam, String trangThai) {
        this.id = id;
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.chucVu = chucVu;
        this.luongCoBan = luongCoBan;
        this.ngayVaoLam = ngayVaoLam;
        this.trangThai = trangThai;
    }

    public NhanVien(String maNhanVien, String hoTen, String chucVu,
                    double luongCoBan, String ngayVaoLam, String trangThai) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.chucVu = chucVu;
        this.luongCoBan = luongCoBan;
        this.ngayVaoLam = ngayVaoLam;
        this.trangThai = trangThai;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public double getLuongCoBan() {
        return luongCoBan;
    }

    public void setLuongCoBan(double luongCoBan) {
        this.luongCoBan = luongCoBan;
    }

    public String getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(String ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNhanVien='" + maNhanVien + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", chucVu='" + chucVu + '\'' +
                ", luongCoBan=" + luongCoBan +
                '}';
    }
}
