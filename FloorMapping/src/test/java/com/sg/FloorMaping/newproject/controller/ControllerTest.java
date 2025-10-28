package com.sg.FloorMaping.newproject.controller;

import com.sg.FloorMaping.newproject.Controller.Controller;
import com.sg.FloorMaping.newproject.Service.ServiceLayer;
import com.sg.FloorMaping.newproject.UI.View;
import com.sg.FloorMaping.newproject.model.Order;
import com.sg.FloorMaping.newproject.model.Product;
import com.sg.FloorMaping.newproject.model.Tax;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;

class ControllerTest {

    @Mock
    private ServiceLayer service;

    @Mock
    private View view;

    @InjectMocks
    private Controller controller;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // -------------------------------------------------------------
    // 1. displayOrders()
    // -------------------------------------------------------------
    @Test
    void testDisplayOrders_success() {
        LocalDate date = LocalDate.of(2025, 10, 29);
        List<Order> mockOrders = List.of(makeOrder("Alice", date));

        when(view.getOrderDate()).thenReturn(date);
        when(service.getOrdersForDate(date)).thenReturn(mockOrders);

        // Execute private method via reflection (since run() is interactive)
        invokePrivate(controller, "displayOrders");

        verify(view).displayOrders(mockOrders);
    }

    @Test
    void testDisplayOrders_failure() {
        LocalDate date = LocalDate.of(2025, 10, 29);
        when(view.getOrderDate()).thenReturn(date);
        when(service.getOrdersForDate(date)).thenThrow(new RuntimeException("Database error"));

        invokePrivate(controller, "displayOrders");

        verify(view).displayMessage(contains("Error displaying orders"));
    }

    // -------------------------------------------------------------
    // 2. addOrder()
    // -------------------------------------------------------------
    @Test
    void testAddOrder_success() {
        List<Tax> taxes = List.of(new Tax("California", "CA", BigDecimal.valueOf(6.25)));
        List<Product> products = List.of(new Product("Tile", BigDecimal.TEN, BigDecimal.ONE));
        Order order = makeOrder("Bob", LocalDate.of(2025, 10, 29));

        when(service.getTaxes()).thenReturn(taxes);
        when(service.getProducts()).thenReturn(products);
        when(view.getAddOrderInput(taxes, products)).thenReturn(order);
        when(service.getNextOrderNumber()).thenReturn(100);

        invokePrivate(controller, "addOrder");

        verify(service).addOrder(order);
        verify(view).displayOrderSummary(order);
        verify(view).displayMessage(contains("Order added successfully"));
    }

    @Test
    void testAddOrder_failure() {
        when(service.getTaxes()).thenThrow(new RuntimeException("File missing"));

        invokePrivate(controller, "addOrder");

        verify(view).displayMessage(contains("Error adding order"));
    }

    // -------------------------------------------------------------
    // 3. editOrder()
    // -------------------------------------------------------------
    @Test
    void testEditOrder_success() {
        LocalDate date = LocalDate.of(2025, 10, 29);
        Order existing = makeOrder("Charlie", date);

        when(view.getOrderDate()).thenReturn(date);
        when(view.getOrderNumber()).thenReturn(10);
        when(service.getOrder(date, 10)).thenReturn(existing);
        when(view.confirm(anyString())).thenReturn(true);
        when(view.readString(anyString())).thenReturn("David", "", "", "");

        invokePrivate(controller, "editOrder");

        verify(service).editOrder(existing);
        verify(view).displayMessage(contains("updated successfully"));
    }

    @Test
    void testEditOrder_notFound() {
        LocalDate date = LocalDate.of(2025, 10, 29);

        when(view.getOrderDate()).thenReturn(date);
        when(view.getOrderNumber()).thenReturn(123);
        when(service.getOrder(date, 123)).thenReturn(null);

        invokePrivate(controller, "editOrder");

        verify(view).displayMessage("Order not found.");
    }

    @Test
    void testEditOrder_failure() {
        when(view.getOrderDate()).thenThrow(new RuntimeException("Invalid date"));

        invokePrivate(controller, "editOrder");

        verify(view).displayMessage(contains("Error editing order"));
    }

    // -------------------------------------------------------------
    // 4. removeOrder()
    // -------------------------------------------------------------
    @Test
    void testRemoveOrder_success() {
        LocalDate date = LocalDate.of(2025, 10, 29);
        Order existing = makeOrder("Eve", date);

        when(view.getOrderDate()).thenReturn(date);
        when(view.getOrderNumber()).thenReturn(5);
        when(service.getOrder(date, 5)).thenReturn(existing);
        when(view.confirm(anyString())).thenReturn(true);

        invokePrivate(controller, "removeOrder");

        verify(service).removeOrder(date, 5);
        verify(view).displayMessage("Order removed.");
    }

    @Test
    void testRemoveOrder_cancelled() {
        LocalDate date = LocalDate.of(2025, 10, 29);
        Order existing = makeOrder("Eve", date);

        when(view.getOrderDate()).thenReturn(date);
        when(view.getOrderNumber()).thenReturn(5);
        when(service.getOrder(date, 5)).thenReturn(existing);
        when(view.confirm(anyString())).thenReturn(false);

        invokePrivate(controller, "removeOrder");

        verify(view).displayMessage("Deletion cancelled.");
        verify(service, never()).removeOrder(any(), anyInt());
    }

    @Test
    void testRemoveOrder_failure() {
        when(view.getOrderDate()).thenThrow(new RuntimeException("IO error"));

        invokePrivate(controller, "removeOrder");

        verify(view).displayMessage(contains("Error removing order"));
    }

    // -------------------------------------------------------------
    // 5. exportData()
    // -------------------------------------------------------------
    @Test
    void testExportData_success() throws Exception {
        doNothing().when(service).exportData();

        invokePrivate(controller, "exportData");

        verify(service).exportData();
        verify(view).displayMessage("All data exported successfully!");
    }

    @Test
    void testExportData_failure() throws Exception {
        doThrow(new RuntimeException("Disk full")).when(service).exportData();

        invokePrivate(controller, "exportData");

        verify(view).displayMessage(contains("Error exporting data"));
    }

    // -------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------
    private Order makeOrder(String name, LocalDate date) {
        Order o = new Order();
        o.setOrderNumber(1);
        o.setOrderDate(date);
        o.setCustomerName(name);
        o.setState("TX");
        o.setProductType("Wood");
        o.setArea(BigDecimal.TEN);
        o.setCostPerSquareFoot(BigDecimal.ONE);
        o.setLaborCostPerSquareFoot(BigDecimal.ONE);
        o.setMaterialCost(BigDecimal.TEN);
        o.setLaborCost(BigDecimal.TEN);
        o.setTax(BigDecimal.ONE);
        o.setTotal(BigDecimal.valueOf(21));
        return o;
    }

    /** Utility to invoke private methods using reflection */
    private void invokePrivate(Object target, String methodName, Object... args) {
        try {
            var method = Arrays.stream(target.getClass().getDeclaredMethods())
                    .filter(m -> m.getName().equals(methodName))
                    .findFirst().orElseThrow();
            method.setAccessible(true);
            method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
