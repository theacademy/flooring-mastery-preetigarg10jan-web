package com.sg.FloorMaping.newproject.Dao;

import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class AuditDaoFileImpl implements AuditDao{
    private static final String AUDIT_FILE = "Data/Audit.txt";
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void writeAuditEntry(String entry) throws Exception {
        File dir = new File("Data");
        if (!dir.exists()) dir.mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(AUDIT_FILE, true))) {
            pw.println(F.format(LocalDateTime.now()) + " : " + entry);
        }
    }
}

