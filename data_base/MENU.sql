USE cafe_manager;
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='NguyenLieu' AND xtype='U')
CREATE TABLE NguyenLieu (
    id                INT IDENTITY(1,1) PRIMARY KEY,
    maNguyenLieu      NVARCHAR(20)  NOT NULL UNIQUE,
    tenNguyenLieu     NVARCHAR(150) NOT NULL,
    donViTinh         NVARCHAR(20)  NOT NULL,
    soLuongTon        FLOAT         NOT NULL DEFAULT 0,
    soLuongToiThieu   FLOAT         NOT NULL DEFAULT 0,
    ngayCapNhat       NVARCHAR(20),
    ghiChu            NVARCHAR(500)
);
GO

INSERT INTO NguyenLieu (maNguyenLieu, tenNguyenLieu, donViTinh, soLuongTon, soLuongToiThieu, ngayCapNhat, ghiChu) VALUES
(N'NL001', N'Cà phê Robusta', N'kg',   5.0,  2.0, N'12/03/2026', N'Mua từ Đắk Lắk'),
(N'NL002', N'Sữa đặc',        N'hộp', 20.0,  5.0, N'12/03/2026', NULL),
(N'NL003', N'Trà oolong',     N'gói',  8.0,  3.0, N'12/03/2026', NULL),
(N'NL004', N'Đường',          N'kg',   1.5,  3.0, N'12/03/2026', N'Sắp hết – cần nhập'),
(N'NL005', N'Trân châu đen',  N'kg',   2.0,  1.0, N'12/03/2026', NULL);
GO