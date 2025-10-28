package com.sg.FloorMaping.newproject.Service;

import com.sg.FloorMaping.newproject.Dao.*;
import com.sg.FloorMaping.newproject.model.Order;
import com.sg.FloorMaping.newproject.model.Product;
import com.sg.FloorMaping.newproject.model.Tax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class ServiceLayerImpl implements ServiceLayer{
    private final OrderDao orderDao;
    private final ProductDao productDao;
    private final TaxDao taxDao;
    private final AuditDao auditDao;
    private final ExportDao exportDao;

    @Autowired
    public ServiceLayerImpl(OrderDao orderDao, ProductDao productDao, TaxDao taxDao, AuditDao auditDao, ExportDao exportDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxDao = taxDao;
        this.auditDao = auditDao;
        this.exportDao = exportDao;
    }

    @Override
    public int getNextOrderNumber() {
        return orderDao.getNextOrderNumber();
    }

    @Override
    public Order addOrder(Order order) {
        calculateOrder(order);
        orderDao.addOrder(order);
        try {
            auditDao.writeAuditEntry("Order added: " + order.getOrderNumber());
        } catch (Exception e) {
            /* ignore */ }
        return order;
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) {

        return orderDao.getOrder(date, orderNumber);
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) {
        return orderDao.getOrdersForDate(date);
    }

    @Override
    public Order editOrder(Order order) {
        // recalc then add (overwrite)
        calculateOrder(order);
        orderDao.addOrder(order);
        try {
            auditDao.writeAuditEntry("Order edited: " + order.getOrderNumber());
        } catch (Exception e) {

        }
        return order;
    }

    @Override
    public void removeOrder(LocalDate date, int orderNumber) {
        orderDao.removeOrder(date, orderNumber);
        try { auditDao.writeAuditEntry("Order removed: " + orderNumber); } catch (Exception e) { }
    }

    @Override
    public List<Product> getProducts() {
        return productDao.getAllProducts();
    }

    @Override
    public List<Tax> getTaxes() { return taxDao.getAllTaxes(); }

    @Override
    public void exportData() throws Exception {
        exportDao.exportData(orderDao.getAllOrders());
    }

    /**
     * Calculates material, labor, tax, total and populates the order fields.
     */
    private void calculateOrder(Order order) {
        // fetch product and tax details
        Product prod = productDao.getProductByType(order.getProductType());
        Tax tax = taxDao.getTaxByState(order.getState());

        if (prod == null) throw new IllegalArgumentException("Unknown product type: " + order.getProductType());
        if (tax == null) throw new IllegalArgumentException("Unknown tax state: " + order.getState());

        order.setCostPerSquareFoot(prod.getCostPerSquareFoot());
        order.setLaborCostPerSquareFoot(prod.getLaborCostPerSquareFoot());
        order.setTaxRate(tax.getTaxRate());

        BigDecimal area = order.getArea();

        BigDecimal materialCost = prod.getCostPerSquareFoot().multiply(area).setScale(2, RoundingMode.HALF_UP);
        BigDecimal laborCost = prod.getLaborCostPerSquareFoot().multiply(area).setScale(2, RoundingMode.HALF_UP);
        BigDecimal subTotal = materialCost.add(laborCost);

        BigDecimal taxAmount = subTotal.multiply(tax.getTaxRate()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = subTotal.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        order.setMaterialCost(materialCost);
        order.setLaborCost(laborCost);
        order.setTax(taxAmount);
        order.setTotal(total);
    }
}
