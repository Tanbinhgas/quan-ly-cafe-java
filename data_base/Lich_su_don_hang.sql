USE cafe_manager;
GO
 
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='DonHang' AND xtype='U')
CREATE TABLE DonHang (
    id        INT IDENTITY(1,1) PRIMARY KEY,
    tenBan    NVARCHAR(50)  NOT NULL DEFAULT N'Mang về',
    loaiDon   NVARCHAR(20)  NOT NULL DEFAULT N'Uống tại chỗ',
    tongTien  BIGINT        NOT NULL DEFAULT 0,
    ghiChu    NVARCHAR(500),
    thoiGian  DATETIME      NOT NULL DEFAULT GETDATE(),
    trangThai NVARCHAR(20)  NOT NULL DEFAULT N'Hoàn thành'
);
GO
 
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ChiTietDonHang' AND xtype='U')
CREATE TABLE ChiTietDonHang (
    id        INT IDENTITY(1,1) PRIMARY KEY,
    donHangId INT           NOT NULL REFERENCES DonHang(id) ON DELETE CASCADE,
    tenMon    NVARCHAR(100) NOT NULL,
    donGia    BIGINT        NOT NULL,
    soLuong   INT           NOT NULL DEFAULT 1,
    thanhTien BIGINT        NOT NULL
);
GO
 
PRINT N'Tạo bảng DonHang và ChiTietDonHang thành công!';
GO