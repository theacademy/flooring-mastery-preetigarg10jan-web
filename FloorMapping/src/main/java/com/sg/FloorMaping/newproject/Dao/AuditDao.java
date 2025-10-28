package com.sg.FloorMaping.newproject.Dao;

public interface AuditDao {
    void writeAuditEntry(String entry) throws Exception;
}
