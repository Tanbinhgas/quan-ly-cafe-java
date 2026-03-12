USE cafe_manager;
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Ban' AND xtype='U')
CREATE TABLE Ban (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    table_name  NVARCHAR(50)  NOT NULL,
    status      NVARCHAR(20)  NOT NULL DEFAULT N'Trống'
);
GO

IF NOT EXISTS (SELECT 1 FROM Ban)
BEGIN
    INSERT INTO Ban (table_name, status) VALUES
    (N'Bàn 1',  N'Trống'),
    (N'Bàn 2',  N'Trống'),
    (N'Bàn 3',  N'Trống'),
    (N'Bàn 4',  N'Trống'),
    (N'Bàn 5',  N'Trống'),
    (N'Bàn 6',  N'Trống'),
    (N'Bàn 7',  N'Trống'),
    (N'Bàn 8',  N'Trống'),
    (N'Bàn 9',  N'Trống'),
    (N'Bàn 10', N'Trống'),
    (N'Bàn 11', N'Trống'),
    (N'Bàn 12', N'Trống');
END
GO

SELECT * FROM Ban;
GO
