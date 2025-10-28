package com.sg.FloorMaping.newproject.Dao;

import com.sg.FloorMaping.newproject.model.Order;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderDao {
    int getNextOrderNumber();
    void addOrder(Order order);
    Order getOrder(LocalDate date, int orderNumber);
    List<Order> getOrdersForDate(LocalDate date);
    void removeOrder(LocalDate date, int orderNumber);
    Map<LocalDate, Map<Integer, Order>> getAllOrders();
}







