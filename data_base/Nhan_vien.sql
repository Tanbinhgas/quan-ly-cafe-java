CREATE TABLE NhanVien (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    maNhanVien   VARCHAR(20) UNIQUE NOT NULL,
    hoTen       NVARCHAR(100) NOT NULL,
    chucVu        NVARCHAR(50) NOT NULL,
    luongCoBan     DECIMAL(12, 2) DEFAULT 0.00,
    ngayVaoLam       DATE NOT NULL,
    trangThai          VARCHAR(20) NOT NULL DEFAULT 'Đang làm'
);
GO

INSERT INTO NhanVien(maNhanVien, hoTen, chucVu, luongCoBan, ngayVaoLam, trangThai)
VALUES 
    ('QL001', N'Nguyễn Văn Quản', N'Quản lý', 15000000.00, '2023-01-01', 'Đang làm'),
    ('PC001', N'Trần Thị Pha', N'Pha chế', 8000000.00, '2023-03-15', 'Đang làm'),
    ('PV001', N'Lê Văn Phục', N'Phục vụ', 7000000.00, '2023-06-01', 'Đang làm'),
    ('TN001', N'Hoàng Văn Thu', N'Thu ngân', 8500000.00, '2023-04-20', 'Đang làm'),
    ('PV002', N'Phạm Thị Vụ', N'Phục vụ', 7000000.00, '2024-01-10', 'Đang làm');
GO

SELECT * FROM NhanVien;
GO