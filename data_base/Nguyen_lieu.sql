USE cafe_manager;
GO

DELETE FROM NguyenLieu;
GO

INSERT INTO NguyenLieu (maNguyenLieu, tenNguyenLieu, donViTinh, soLuongTon, soLuongToiThieu, ngayCapNhat, ghiChu) VALUES
(N'NL001', N'Cà phê rang xay',  N'kg',   5.0,  2.0, N'14/03/2026', N'Mua từ Đắk Lắk'),
(N'NL002', N'Sữa đặc',          N'hộp', 20.0,  5.0, N'14/03/2026', NULL),
(N'NL003', N'Sữa tươi',         N'lít', 15.0,  5.0, N'14/03/2026', NULL),
(N'NL004', N'Đường',            N'kg',   1.5,  3.0, N'14/03/2026', N'Sắp hết – cần nhập'),
(N'NL005', N'Trân châu đen',    N'kg',   2.0,  1.0, N'14/03/2026', NULL),
(N'NL006', N'Bột matcha',       N'kg',   1.0,  0.5, N'14/03/2026', NULL),
(N'NL007', N'Mật ong',          N'chai', 5.0,  2.0, N'14/03/2026', NULL),
(N'NL008', N'Trà xanh',         N'gói', 10.0,  3.0, N'14/03/2026', NULL),
(N'NL009', N'Chanh tươi',       N'kg',   3.0,  1.0, N'14/03/2026', NULL),
(N'NL010', N'Đào ngâm',         N'hũ',   8.0,  3.0, N'14/03/2026', NULL),
(N'NL011', N'Cam tươi',         N'kg',   4.0,  1.5, N'14/03/2026', NULL),
(N'NL012', N'Sả',               N'kg',   2.0,  0.5, N'14/03/2026', NULL),
(N'NL013', N'Đá viên',          N'kg',  20.0,  5.0, N'14/03/2026', NULL);
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

IF EXISTS (SELECT * FROM sysobjects WHERE name='MonNuoc' AND xtype='U')
    DROP TABLE MonNuoc;
GO

CREATE TABLE MonNuoc (
    id             INT IDENTITY(1,1) PRIMARY KEY,
    tenMon         NVARCHAR(100) NOT NULL,
    maNguyenLieu   NVARCHAR(20)  NOT NULL REFERENCES NguyenLieu(maNguyenLieu),
    dinhMucMoiLy   FLOAT         NOT NULL DEFAULT 0
);
GO

INSERT INTO MonNuoc (tenMon, maNguyenLieu, dinhMucMoiLy) VALUES
(N'Cà phê phin', N'NL001', 0.02),
(N'Cà phê phin', N'NL002', 0.05);

INSERT INTO MonNuoc (tenMon, maNguyenLieu, dinhMucMoiLy) VALUES
(N'Cappuccino', N'NL001', 0.02),
(N'Cappuccino', N'NL003', 0.15);

INSERT INTO MonNuoc (tenMon, maNguyenLieu, dinhMucMoiLy) VALUES
(N'Latte', N'NL001', 0.02),
(N'Latte', N'NL003', 0.20);

INSERT INTO MonNuoc (tenMon, maNguyenLieu, dinhMucMoiLy) VALUES
(N'Matcha Mật Mè', N'NL006', 0.015),
(N'Matcha Mật Mè', N'NL003', 0.15),
(N'Matcha Mật Mè', N'NL007', 0.02),
(N'Matcha Mật Mè', N'NL013', 0.10);

INSERT INTO MonNuoc (tenMon, maNguyenLieu, dinhMucMoiLy) VALUES
(N'Trà Chanh Giã Tay', N'NL008', 0.01),
(N'Trà Chanh Giã Tay', N'NL009', 0.05),
(N'Trà Chanh Giã Tay', N'NL004', 0.02),
(N'Trà Chanh Giã Tay', N'NL013', 0.10);

INSERT INTO MonNuoc (tenMon, maNguyenLieu, dinhMucMoiLy) VALUES
(N'Trà Đào Cam Sả', N'NL008', 0.01),
(N'Trà Đào Cam Sả', N'NL010', 0.05),
(N'Trà Đào Cam Sả', N'NL011', 0.05),
(N'Trà Đào Cam Sả', N'NL012', 0.01),
(N'Trà Đào Cam Sả', N'NL013', 0.10);
GO

SELECT 'NguyenLieu' AS Bang, COUNT(*) AS SoBanGhi FROM NguyenLieu
UNION ALL
SELECT 'DonHang',       COUNT(*) FROM DonHang
UNION ALL
SELECT 'ChiTietDonHang',COUNT(*) FROM ChiTietDonHang
UNION ALL
SELECT 'MonNuoc',       COUNT(*) FROM MonNuoc;
GO

PRINT N'Cập nhật thành công.';
GO
