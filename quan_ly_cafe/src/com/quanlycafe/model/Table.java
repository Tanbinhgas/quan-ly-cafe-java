package com.quanlycafe.model;

public class Table {

    private int id;
    private String tableName;
    private String status;

    public Table(int id, String tableName, String status) {
        this.id = id;
        this.tableName = tableName;
        this.status = status;
    }

    public int getId() { return id; }
    public String getTableName() { return tableName; }
    public String getStatus() { return status; }

    public void setTableName(String tableName) { this.tableName = tableName; }
    public void setStatus(String status) { this.status = status; }
}
