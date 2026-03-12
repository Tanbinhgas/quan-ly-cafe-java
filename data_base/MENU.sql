-- ============================================================
-- Cafe Manager – Script tạo 3 bảng mới
-- Database: cafe_manager (SQL Server)
-- ============================================================

USE cafe_manager;
GO

-- 1. Bảng Thực đơn (MonAn)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='MonAn' AND xtype='U')
CREATE TABLE MonAn (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    maMonAn     NVARCHAR(20)  NOT NULL UNIQUE,
    tenMonAn    NVARCHAR(150) NOT NULL,
    loai        NVARCHAR(50)  NOT NULL,          -- 'Đồ uống' | 'Thực phẩm'
    giaBan      FLOAT         NOT NULL DEFAULT 0,
    moTa        NVARCHAR(500),
    trangThai   NVARCHAR(30)  NOT NULL DEFAULT N'Còn bán'
);
GO

-- 2. Bảng Đơn hàng (DonHang)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='DonHang' AND xtype='U')
CREATE TABLE DonHang (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    maDonHang   NVARCHAR(30)  NOT NULL UNIQUE,
    banId       INT           NOT NULL REFERENCES [Table](id),
    ngayTao     NVARCHAR(30)  NOT NULL,
    tongTien    FLOAT         NOT NULL DEFAULT 0,
    trangThai   NVARCHAR(40)  NOT NULL DEFAULT N'Chờ thanh toán',
    ghiChu      NVARCHAR(500)
);
GO

-- 3. Bảng Kho nguyên liệu (NguyenLieu)
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

-- ============================================================
-- Dữ liệu mẫu (tuỳ chọn)
-- ============================================================

-- ============================================================
-- Thực đơn thực tế – Mô hình Cà Phê
-- ============================================================

-- ☕ Cà phê truyền thống
INSERT INTO MonAn (maMonAn, tenMonAn, loai, giaBan, moTa, trangThai) VALUES
(N'CPT001', N'Cà phê đen',        N'Cà phê truyền thống', 20000, N'Pha phin truyền thống', N'Còn bán'),
(N'CPT002', N'Cà phê nâu',        N'Cà phê truyền thống', 25000, N'Cà phê đen + sữa đặc', N'Còn bán'),
(N'CPT003', N'Cà phê bạc xỉu',   N'Cà phê truyền thống', 30000, N'Nhiều sữa ít cà phê', N'Còn bán'),
(N'CPT004', N'Cà phê cốt dừa',   N'Cà phê truyền thống', 35000, N'Cà phê phin + nước cốt dừa', N'Còn bán'),
(N'CPT005', N'Cà phê chuối dừa', N'Cà phê truyền thống', 35000, N'Cà phê kết hợp chuối và dừa', N'Còn bán');
GO

-- ☕ Cà phê máy
INSERT INTO MonAn (maMonAn, tenMonAn, loai, giaBan, moTa, trangThai) VALUES
(N'CPM001', N'Espresso',    N'Cà phê máy', 25000, N'Cà phê espresso nguyên chất', N'Còn bán'),
(N'CPM002', N'Americano',   N'Cà phê máy', 25000, N'Espresso pha loãng với nước nóng', N'Còn bán'),
(N'CPM003', N'Cappuccino',  N'Cà phê máy', 35000, N'Espresso + sữa nóng + foam', N'Còn bán'),
(N'CPM004', N'Latte',       N'Cà phê máy', 35000, N'Espresso + nhiều sữa nóng', N'Còn bán');
GO

-- 🥤 Soda
INSERT INTO MonAn (maMonAn, tenMonAn, loai, giaBan, moTa, trangThai) VALUES
(N'SOD001', N'Soda mojito',       N'Soda', 30000, N'Soda bạc hà chanh tươi mát', N'Còn bán'),
(N'SOD002', N'Soda chanh leo',    N'Soda', 30000, N'Soda vị chanh leo chua ngọt', N'Còn bán'),
(N'SOD003', N'Soda quýt bạc hà', N'Soda', 30000, N'Soda quýt kết hợp bạc hà', N'Còn bán');
GO

-- 🧊 Đá xay
INSERT INTO MonAn (maMonAn, tenMonAn, loai, giaBan, moTa, trangThai) VALUES
(N'DX001', N'Matcha đá xay',              N'Đá xay', 35000, N'Matcha Nhật xay mịn với đá', N'Còn bán'),
(N'DX002', N'Cookies đá xay',             N'Đá xay', 35000, N'Vị cookies kem béo ngậy', N'Còn bán'),
(N'DX003', N'Caramel bơ mặn đá xay',     N'Đá xay', 35000, N'Caramel bơ mặn ngọt hài hoà', N'Còn bán'),
(N'DX004', N'Sữa chua việt quất đá xay', N'Đá xay', 35000, N'Sữa chua việt quất thanh mát', N'Còn bán'),
(N'DX005', N'Sữa chua xoài đá xay',      N'Đá xay', 35000, N'Sữa chua xoài nhiệt đới', N'Còn bán');
GO

-- 🍵 Trà
INSERT INTO MonAn (maMonAn, tenMonAn, loai, giaBan, moTa, trangThai) VALUES
(N'TRA001', N'Trà đào',             N'Trà', 30000, N'Trà đào thơm ngọt dịu', N'Còn bán'),
(N'TRA002', N'Trà đào cam sả',      N'Trà', 35000, N'Trà đào kết hợp cam và sả tươi', N'Còn bán'),
(N'TRA003', N'Lục trà lê chi',      N'Trà', 30000, N'Lục trà vị lê và vải thiều', N'Còn bán'),
(N'TRA004', N'Lục trà chanh leo',   N'Trà', 30000, N'Lục trà kết hợp chanh leo chua ngọt', N'Còn bán'),
(N'TRA005', N'Trà xoài cam táo',    N'Trà', 35000, N'Trà trái cây xoài cam táo', N'Còn bán'),
(N'TRA006', N'Trà kiwi táo dứa',    N'Trà', 35000, N'Trà trái cây kiwi táo dứa', N'Còn bán');
GO

-- 🥤 Sinh tố & Nước ép
INSERT INTO MonAn (maMonAn, tenMonAn, loai, giaBan, moTa, trangThai) VALUES
(N'ST001', N'Sinh tố bơ',          N'Sinh tố & Nước ép', 40000, N'Bơ tươi xay kem béo ngậy', N'Còn bán'),
(N'ST002', N'Sinh tố xoài',        N'Sinh tố & Nước ép', 40000, N'Xoài chín xay mịn', N'Còn bán'),
(N'ST003', N'Nước ép dứa',         N'Sinh tố & Nước ép', 40000, N'Dứa tươi ép nguyên chất', N'Còn bán'),
(N'ST004', N'Nước ép dứa cần tây', N'Sinh tố & Nước ép', 40000, N'Dứa + cần tây thanh lọc cơ thể', N'Còn bán'),
(N'ST005', N'Nước ép ổi',          N'Sinh tố & Nước ép', 40000, N'Ổi tươi ép nguyên chất', N'Còn bán');
GO

-- Nguyên liệu mẫu
INSERT INTO NguyenLieu (maNguyenLieu, tenNguyenLieu, donViTinh, soLuongTon, soLuongToiThieu, ngayCapNhat, ghiChu) VALUES
(N'NL001', N'Cà phê Robusta', N'kg',   5.0,  2.0, N'12/03/2026', N'Mua từ Đắk Lắk'),
(N'NL002', N'Sữa đặc',        N'hộp', 20.0,  5.0, N'12/03/2026', NULL),
(N'NL003', N'Trà oolong',     N'gói',  8.0,  3.0, N'12/03/2026', NULL),
(N'NL004', N'Đường',          N'kg',   1.5,  3.0, N'12/03/2026', N'Sắp hết – cần nhập'),
(N'NL005', N'Trân châu đen',  N'kg',   2.0,  1.0, N'12/03/2026', NULL);
GO
