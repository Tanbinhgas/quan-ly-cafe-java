package com.quanlycafe.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String SERVER_NAME = "LAPTOP100TOI\\SQL_PROJECT";
    private static final String DATABASE_NAME = "cafe_manager";

    private static final String CONNECTION_URL = 
        "jdbc:sqlserver://" + SERVER_NAME + ":1433;" +
        "databaseName=" + DATABASE_NAME + ";" +
        "user=sa;" +
        "password=MatKhauMoi2026;" +
        "encrypt=false;" +
        "trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Kết nối SQL Server thành công!");
            System.out.println("Database: " + conn.getCatalog());
            System.out.println("Server: " + conn.getMetaData().getURL());
        } catch (SQLException e) {
            System.err.println("Kết nối thất bại: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
