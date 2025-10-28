package com.sg.FloorMaping.newproject.Service;

import com.sg.FloorMaping.newproject.model.Order;
import com.sg.FloorMaping.newproject.model.Product;
import com.sg.FloorMaping.newproject.model.Tax;

import java.time.LocalDate;
import java.util.List;

public interface ServiceLayer {
    int getNextOrderNumber();
    Order addOrder(Order order);
    Order getOrder(LocalDate date, int orderNumber);
    java.util.List<Order> getOrdersForDate(LocalDate date);
    Order editOrder(Order order);
    void removeOrder(LocalDate date, int orderNumber);
    java.util.List<Product> getProducts();
    java.util.List<Tax> getTaxes();
    void exportData() throws Exception;
}

