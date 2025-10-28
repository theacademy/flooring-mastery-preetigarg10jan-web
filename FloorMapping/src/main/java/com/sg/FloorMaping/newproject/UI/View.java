package com.sg.FloorMaping.newproject.UI;

import com.sg.FloorMaping.newproject.Utils.GenericTablePrinter;
import com.sg.FloorMaping.newproject.model.Order;
import com.sg.FloorMaping.newproject.model.Product;
import com.sg.FloorMaping.newproject.model.Tax;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class View {
    private final UserIO io;

    public View(UserIO io) {
        this.io = io;
    }

    // -----------------------------
    // Generic display & input
    // -----------------------------
    public void displayMessage(String message) {
        io.print(message);
    }

    public String readString(String prompt) {
        return io.readString(prompt);
    }

    // -----------------------------
    // Menu
    // -----------------------------
    public int printMenuAndGetSelection() {
        io.print("\n=== Flooring Program ===");
        io.print("1. Display Orders for a Date");
        io.print("2. Add an Order");
        io.print("3. Edit an Order");
        io.print("4. Remove an Order");
        io.print("5. Export All Data");
        io.print("6. Exit");
        return io.readInt("Select an option: ", 1, 6);
    }

    // -----------------------------
    // Display Orders
    // -----------------------------
    public void displayOrders(List<Order> orders) {
        GenericTablePrinter.printList(orders);
    }

    // -----------------------------
    // Date & Order Number
    // -----------------------------
    public LocalDate readOrderDate() {
        String s = io.readString("Enter date (yyyy-MM-dd): ");
        return LocalDate.parse(s);
    }

    // ✅ This method name matches the controller call
    public LocalDate getOrderDate() {
        return readOrderDate();
    }

    public int readOrderNumber() {
        return Integer.parseInt(io.readString("Enter order number: "));
    }

    public int getOrderNumber() {
        return readOrderNumber();
    }

    // -----------------------------
    // Add Order
    // -----------------------------
    public Order getAddOrderInput(List<Tax> taxes, List<Product> products) {
        Order o = new Order();
        o.setOrderDate(readOrderDate());
        o.setCustomerName(io.readString("Customer name: "));

        io.print("\nAvailable states:");
        for (Tax t : taxes) io.print(" - " + t.getState());
        o.setState(io.readString("State (exact): "));

        io.print("\nAvailable products:");
        for (Product p : products) io.print(" - " + p.getProductType());
        o.setProductType(io.readString("Product type (exact): "));

        String areaStr = io.readString("Area (in sq ft): ");
        o.setArea(new BigDecimal(areaStr));

        return o;
    }

    // -----------------------------
    // Summaries
    // -----------------------------
    public void displayOrderSummary(Order o) {
        io.print("\nOrder summary:");
        GenericTablePrinter.printObject(o);
    }

    // -----------------------------
    // Confirmation
    // -----------------------------
    public boolean confirm(String message) {
        String resp = io.readString(message + " (Y/N): ");
        return resp.equalsIgnoreCase("Y") || resp.equalsIgnoreCase("YES");
    }
}



