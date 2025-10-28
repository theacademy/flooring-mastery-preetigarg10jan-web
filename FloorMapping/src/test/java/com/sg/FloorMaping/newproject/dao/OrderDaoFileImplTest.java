package com.sg.FloorMaping.newproject.dao;

import com.sg.FloorMaping.newproject.Dao.OrderDaoFileImpl;
import com.sg.FloorMaping.newproject.model.Order;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class OrderDaoFileImplTest {

    private static Path tempDir;
    private OrderDaoFileImpl dao;

    @BeforeAll
    static void setupClass() throws Exception {
        tempDir = Files.createTempDirectory("order_test_data");
    }

    @AfterAll
    static void cleanupClass() throws Exception {
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @BeforeEach
    void setUp() {
        System.setProperty("user.dir", tempDir.toString());
        dao = new OrderDaoFileImpl();
    }

    private Order createSampleOrder(LocalDate date, String customerName) {
        Order order = new Order();
        order.setOrderNumber(dao.getNextOrderNumber());
        order.setOrderDate(date);
        order.setCustomerName(customerName);
        order.setState("TX");
        order.setTaxRate(new BigDecimal("6.25"));
        order.setProductType("Wood");
        order.setArea(new BigDecimal("100"));
        order.setCostPerSquareFoot(new BigDecimal("5.15"));
        order.setLaborCostPerSquareFoot(new BigDecimal("4.75"));
        order.setMaterialCost(new BigDecimal("515.00"));
        order.setLaborCost(new BigDecimal("475.00"));
        order.setTax(new BigDecimal("61.88"));
        order.setTotal(new BigDecimal("1051.88"));
        return order;
    }

    @Test
    void testAddAndGetOrder() {
        LocalDate date = LocalDate.of(2025, 10, 29);
        Order order = createSampleOrder(date, "John Doe");

        dao.addOrder(order);

        Order fetched = dao.getOrder(date, order.getOrderNumber());
        assertNotNull(fetched);
        assertEquals("John Doe", fetched.getCustomerName());
        assertEquals(new BigDecimal("1051.88"), fetched.getTotal());
    }

    @Test
    void testGetOrdersForDate() {
        LocalDate date = LocalDate.of(2025, 10, 29);

        Order order1 = createSampleOrder(date, "Alice");
        Order order2 = createSampleOrder(date, "Bob");

        dao.addOrder(order1);
        dao.addOrder(order2);

        List<Order> orders = dao.getOrdersForDate(date);
        assertEquals(2, orders.size());
    }



}
