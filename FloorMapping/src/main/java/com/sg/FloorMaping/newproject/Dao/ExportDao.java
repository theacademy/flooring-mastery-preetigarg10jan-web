package com.sg.FloorMaping.newproject.Dao;

import com.sg.FloorMaping.newproject.model.Order;

import java.time.LocalDate;
import java.util.Map;

public interface ExportDao {
    void exportData(Map<LocalDate, Map<Integer, Order>> orders) throws Exception;
}

