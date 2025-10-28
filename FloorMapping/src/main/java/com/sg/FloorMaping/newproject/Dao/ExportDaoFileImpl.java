package com.sg.FloorMaping.newproject.Dao;

import com.sg.FloorMaping.newproject.model.Order;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Repository
public class ExportDaoFileImpl implements ExportDao{
    @Override
    public void exportData(Map<LocalDate, Map<Integer, Order>> orders) throws Exception {
        // Create Backup folder if it doesn't exist
        File backupDir = new File("Backup");
        if (!backupDir.exists()) backupDir.mkdirs();

        // Generate timestamped filename
        String fname = "Backup/ExportData_" +
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(fname))) {
            // CSV header
            pw.println("OrderDate,OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total");

            // Sort dates for consistency
            List<LocalDate> sortedDates = new ArrayList<>(orders.keySet());
            sortedDates.sort(Comparator.naturalOrder());

            for (LocalDate d : sortedDates) {
                Map<Integer, Order> dateOrders = orders.get(d);
                // Sort orders by order number for consistency
                List<Integer> orderNumbers = new ArrayList<>(dateOrders.keySet());
                orderNumbers.sort(Integer::compareTo);

                for (Integer orderNum : orderNumbers) {
                    Order o = dateOrders.get(orderNum);
                    pw.printf("%s,%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                            o.getOrderDate(),
                            o.getOrderNumber(),
                            escape(o.getCustomerName()),
                            o.getState(),
                            o.getTaxRate(),
                            o.getProductType(),
                            o.getArea(),
                            o.getCostPerSquareFoot(),
                            o.getLaborCostPerSquareFoot(),
                            o.getMaterialCost(),
                            o.getLaborCost(),
                            o.getTax(),
                            o.getTotal()
                    );
                }
            }
        }

        System.out.println("Export completed: " + fname);
    }

    // Simple escape to replace commas in customer name
    private String escape(String s) {
        return s.replace(",", ";");
    }



}
