package com.sg.FloorMaping.newproject.service;

import com.sg.FloorMaping.newproject.Dao.*;
import com.sg.FloorMaping.newproject.Service.ServiceLayerImpl;
import com.sg.FloorMaping.newproject.model.Order;
import com.sg.FloorMaping.newproject.model.Product;
import com.sg.FloorMaping.newproject.model.Tax;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceLayerImplTest {

    @Mock
    private OrderDao orderDao;
    @Mock
    private ProductDao productDao;
    @Mock
    private TaxDao taxDao;
    @Mock
    private AuditDao auditDao;
    @Mock
    private ExportDao exportDao;

    @InjectMocks
    private ServiceLayerImpl service;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {

        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // --------------------------------------------------------------------
    // getNextOrderNumber
    // --------------------------------------------------------------------
    @Test
    void testGetNextOrderNumber() {
        when(orderDao.getNextOrderNumber()).thenReturn(42);
        assertEquals(42, service.getNextOrderNumber());
        verify(orderDao).getNextOrderNumber();
    }

    // --------------------------------------------------------------------
    // addOrder (positive)
    // --------------------------------------------------------------------
    @Test
    void testAddOrder_success() {
        Order order = makeOrder();
        Product product = makeProduct();
        Tax tax = makeTax();

        when(productDao.getProductByType("Wood")).thenReturn(product);
        when(taxDao.getTaxByState("TX")).thenReturn(tax);

        service.addOrder(order);

        verify(orderDao).addOrder(order);
        assertEquals(new BigDecimal("21.50"), order.getTotal());
        assertEquals(new BigDecimal("1.50"), order.getTax());
    }

    // --------------------------------------------------------------------
    // addOrder (negative: unknown product)
    // --------------------------------------------------------------------
    @Test
    void testAddOrder_unknownProduct_throwsException() {
        Order order = makeOrder();
        when(productDao.getProductByType("Wood")).thenReturn(null);
        when(taxDao.getTaxByState("TX")).thenReturn(makeTax());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.addOrder(order));
        assertTrue(ex.getMessage().contains("Unknown product type"));
    }

    // --------------------------------------------------------------------
    // addOrder (negative: unknown tax)
    // --------------------------------------------------------------------
    @Test
    void testAddOrder_unknownTax_throwsException() {
        Order order = makeOrder();
        when(productDao.getProductByType("Wood")).thenReturn(makeProduct());
        when(taxDao.getTaxByState("TX")).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.addOrder(order));
        assertTrue(ex.getMessage().contains("Unknown tax state"));
    }

    // --------------------------------------------------------------------
    // editOrder (positive)
    // --------------------------------------------------------------------
    @Test
    void testEditOrder_success() {
        Order order = makeOrder();
        when(productDao.getProductByType("Wood")).thenReturn(makeProduct());
        when(taxDao.getTaxByState("TX")).thenReturn(makeTax());

        service.editOrder(order);

        verify(orderDao).addOrder(order);
        assertEquals(new BigDecimal("21.50"), order.getTotal());
    }

    // --------------------------------------------------------------------
    // removeOrder
    // --------------------------------------------------------------------
    @Test
    void testRemoveOrder_success() {
        LocalDate date = LocalDate.of(2025, 10, 29);
        service.removeOrder(date, 5);

        verify(orderDao).removeOrder(date, 5);
    }

    // --------------------------------------------------------------------
    // getOrder / getOrdersForDate
    // --------------------------------------------------------------------
    @Test
    void testGetOrder() {
        LocalDate date = LocalDate.of(2025, 10, 29);
        Order expected = makeOrder();
        when(orderDao.getOrder(date, 10)).thenReturn(expected);

        Order actual = service.getOrder(date, 10);
        assertEquals(expected, actual);
    }

    @Test
    void testGetOrdersForDate() {
        LocalDate date = LocalDate.of(2025, 10, 29);
        List<Order> orders = List.of(makeOrder());
        when(orderDao.getOrdersForDate(date)).thenReturn(orders);

        List<Order> actual = service.getOrdersForDate(date);
        assertEquals(orders, actual);
    }

    // --------------------------------------------------------------------
    // getProducts / getTaxes
    // --------------------------------------------------------------------
    @Test
    void testGetProducts() {
        List<Product> list = List.of(makeProduct());
        when(productDao.getAllProducts()).thenReturn(list);
        assertEquals(list, service.getProducts());
    }

    @Test
    void testGetTaxes() {
        List<Tax> list = List.of(makeTax());
        when(taxDao.getAllTaxes()).thenReturn(list);
        assertEquals(list, service.getTaxes());
    }

    // --------------------------------------------------------------------
    // exportData
    // --------------------------------------------------------------------
    @Test
    void testExportData_success() throws Exception {
        Map<LocalDate, Map<Integer, Order>> orders = new HashMap<>();
        when(orderDao.getAllOrders()).thenReturn(orders);

        service.exportData();

        verify(exportDao).exportData(orders);
    }

    @Test
    void testExportData_failure() throws Exception {
        when(orderDao.getAllOrders()).thenThrow(new RuntimeException("IO error"));

        assertThrows(RuntimeException.class, () -> service.exportData());
    }

    // --------------------------------------------------------------------
    // Helpers
    // --------------------------------------------------------------------
    private Order makeOrder() {
        Order o = new Order();
        o.setOrderNumber(1);
        o.setCustomerName("Alice");
        o.setState("TX");
        o.setProductType("Wood");
        o.setArea(new BigDecimal("10"));
        o.setOrderDate(LocalDate.of(2025, 10, 29));
        return o;
    }

    private Product makeProduct() {
        Product p = new Product();
        p.setProductType("Wood");
        p.setCostPerSquareFoot(new BigDecimal("1.00"));
        p.setLaborCostPerSquareFoot(new BigDecimal("1.00"));
        return p;
    }

    private Tax makeTax() {
        Tax t = new Tax();
        t.setState("TX");
        t.setTaxRate(new BigDecimal("7.50"));
        return t;
    }
}
