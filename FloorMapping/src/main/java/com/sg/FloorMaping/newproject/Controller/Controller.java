package com.sg.FloorMaping.newproject.Controller;

import com.sg.FloorMaping.newproject.Service.ServiceLayer;
import com.sg.FloorMaping.newproject.UI.View;
import com.sg.FloorMaping.newproject.model.Order;
import com.sg.FloorMaping.newproject.model.Product;
import com.sg.FloorMaping.newproject.model.Tax;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
@Component
public class Controller {
    private final View view;
    private final ServiceLayer service;

    public Controller(ServiceLayer service, View view) {
        this.service = service;
        this.view = view;
    }

    // -----------------------------
    // Main Program Loop
    // -----------------------------
    public void run() {
        boolean keepGoing = true;

        while (keepGoing) {
            int choice = view.printMenuAndGetSelection();

            switch (choice) {
                case 1 -> displayOrders();
                case 2 -> addOrder();
                case 3 -> editOrder();
                case 4 -> removeOrder();
                case 5 -> exportData();
                case 6 -> {
                    view.displayMessage("Exiting program...");
                    keepGoing = false;
                }
                default -> view.displayMessage("Unknown command.");
            }
        }
    }

    // -----------------------------
    // 1. Display Orders
    // -----------------------------
    private void displayOrders() {
        try {
            LocalDate date = view.getOrderDate();
            List<Order> orders = service.getOrdersForDate(date);
            view.displayOrders(orders);
        } catch (Exception e) {
            view.displayMessage("Error displaying orders: " + e.getMessage());
        }
    }

    // -----------------------------
    // 2. Add Order
    // -----------------------------
    private void addOrder() {
        try {
            List<Tax> taxes = service.getTaxes();
            List<Product> products = service.getProducts();
            Order newOrder = view.getAddOrderInput(taxes, products);

            // Calculate fields (service should handle this logic)
            newOrder.setOrderNumber(service.getNextOrderNumber());
            service.addOrder(newOrder);

            view.displayOrderSummary(newOrder);
            view.displayMessage("Order added successfully!");
        } catch (Exception e) {
            view.displayMessage("Error adding order: " + e.getMessage());
        }
    }

    // -----------------------------
    // 3. Edit Order
    // -----------------------------
    private void editOrder() {
        try {
            LocalDate date = view.getOrderDate();
            int orderNumber = view.getOrderNumber();
            Order existing = service.getOrder(date, orderNumber);

            if (existing == null) {
                view.displayMessage("Order not found.");
                return;
            }

            view.displayOrderSummary(existing);

            if (!view.confirm("Edit this order?")) return;

            // Prompt user for new info
            String newName = view.readString("New customer name (blank to keep): ");
            if (!newName.isBlank()) existing.setCustomerName(newName);

            String newState = view.readString("New state (blank to keep): ");
            if (!newState.isBlank()) existing.setState(newState);

            String newProduct = view.readString("New product type (blank to keep): ");
            if (!newProduct.isBlank()) existing.setProductType(newProduct);

            String newArea = view.readString("New area (blank to keep): ");
            if (!newArea.isBlank()) existing.setArea(new java.math.BigDecimal(newArea));

            service.editOrder(existing);

            view.displayOrderSummary(existing);
            view.displayMessage("Order updated successfully!");

        } catch (Exception e) {
            view.displayMessage("Error editing order: " + e.getMessage());
        }
    }

    // -----------------------------
    // 4. Remove Order
    // -----------------------------
    private void removeOrder() {
        try {
            LocalDate date = view.getOrderDate();
            int orderNumber = view.getOrderNumber();

            Order existing = service.getOrder(date, orderNumber);
            if (existing == null) {
                view.displayMessage("Order not found.");
                return;
            }

            view.displayOrderSummary(existing);

            if (view.confirm("Are you sure you want to delete this order?")) {
                service.removeOrder(date, orderNumber);
                view.displayMessage("Order removed.");
            } else {
                view.displayMessage("Deletion cancelled.");
            }
        } catch (Exception e) {
            view.displayMessage("Error removing order: " + e.getMessage());
        }
    }

    // -----------------------------
    // 5. Export Data
    // -----------------------------
    private void exportData() {
        try {
            service.exportData();
            view.displayMessage("All data exported successfully!");
        } catch (Exception e) {
            view.displayMessage("Error exporting data: " + e.getMessage());
        }
    }
}