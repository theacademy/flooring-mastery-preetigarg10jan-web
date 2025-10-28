package com.sg.FloorMaping.newproject.dao;

import com.sg.FloorMaping.newproject.Dao.ExportDaoFileImpl;
import com.sg.FloorMaping.newproject.model.Order;
import org.junit.jupiter.api.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ExportDaoFileImplTest {

    private ExportDaoFileImpl exportDao;
    private final String backupDir = "Backup";

    @BeforeEach
    void setUp() throws IOException {
        exportDao = new ExportDaoFileImpl();

        // Clean up old backup files before each test
        Path backupPath = Paths.get(backupDir);
        if (Files.exists(backupPath)) {
            Files.walk(backupPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        Files.createDirectories(backupPath);
    }

    @Test
    void testExportData_createsFileAndContainsOrders() throws Exception {
        // Arrange
        Map<LocalDate, Map<Integer, Order>> orders = new HashMap<>();
        LocalDate date1 = LocalDate.of(2025, 10, 29);
        LocalDate date2 = LocalDate.of(2025, 10, 28);

        Order order1 = makeOrder(1, date1, "Alice", "CA", "Tile");
        Order order2 = makeOrder(2, date1, "Bob", "TX", "Wood");
        Order order3 = makeOrder(3, date2, "Charlie", "NY", "Carpet");

        orders.put(date1, new HashMap<>(Map.of(1, order1, 2, order2)));
        orders.put(date2, new HashMap<>(Map.of(3, order3)));

        // Act
        exportDao.exportData(orders);

        // Assert
        File backupFolder = new File(backupDir);
        File[] exportedFiles = backupFolder.listFiles((dir, name) -> name.startsWith("ExportData_") && name.endsWith(".txt"));
        assertNotNull(exportedFiles, "Backup folder should exist");
        assertTrue(exportedFiles.length > 0, "Export file should be created");

        // Read file contents
        File exportFile = exportedFiles[0];
        List<String> lines = Files.readAllLines(exportFile.toPath());

        // Basic structure check
        assertTrue(lines.size() >= 2, "File should contain header + data lines");
        assertTrue(lines.get(0).contains("OrderDate,OrderNumber"), "Header should exist");

        // Check data is written for at least one order
        String sampleLine = lines.get(1);
        assertTrue(sampleLine.contains("Alice") || sampleLine.contains("Bob") || sampleLine.contains("Charlie"),
                "Exported file should contain customer names");
    }

    @Test
    void testExportData_emptyMap_createsFileWithOnlyHeader() throws Exception {
        // Arrange
        Map<LocalDate, Map<Integer, Order>> emptyOrders = new HashMap<>();

        // Act
        exportDao.exportData(emptyOrders);

        // Assert
        File backupFolder = new File(backupDir);
        File[] exportedFiles = backupFolder.listFiles((dir, name) -> name.startsWith("ExportData_") && name.endsWith(".txt"));
        assertNotNull(exportedFiles);
        assertTrue(exportedFiles.length > 0, "Export file should still be created");

        // Check header only
        List<String> lines = Files.readAllLines(exportedFiles[0].toPath());
        assertEquals(1, lines.size(), "File should contain only header for empty data");
    }

    /** Helper to create consistent test orders **/
    private Order makeOrder(int id, LocalDate date, String customer, String state, String productType) {
        Order o = new Order();
        o.setOrderNumber(id);
        o.setOrderDate(date);
        o.setCustomerName(customer);
        o.setState(state);
        o.setTaxRate(BigDecimal.valueOf(5.00));
        o.setProductType(productType);
        o.setArea(BigDecimal.valueOf(100));
        o.setCostPerSquareFoot(BigDecimal.valueOf(2.50));
        o.setLaborCostPerSquareFoot(BigDecimal.valueOf(1.75));
        o.setMaterialCost(BigDecimal.valueOf(250));
        o.setLaborCost(BigDecimal.valueOf(175));
        o.setTax(BigDecimal.valueOf(21.25));
        o.setTotal(BigDecimal.valueOf(446.25));
        return o;
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up backup folder after each test
        Path backupPath = Paths.get(backupDir);
        if (Files.exists(backupPath)) {
            Files.walk(backupPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
