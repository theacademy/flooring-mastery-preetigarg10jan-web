package com.sg.FloorMaping.newproject.Dao;

import com.sg.FloorMaping.newproject.model.Order;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Repository
public class OrderDaoFileImpl implements OrderDao {

    private static final String DATA_DIR = "Data";
    private static final String DELIM = ",";
    private final Map<LocalDate, Map<Integer, Order>> loadedDates = new HashMap<>();
    private int highestOrderNumber = 0; // next order number

    public OrderDaoFileImpl() {
        // No scanning at startup (lazy loading)
    }

    /** -------------------- Lazy Loading per Date -------------------- */
    private void loadOrdersForDate(LocalDate date) throws IOException {
        if (loadedDates.containsKey(date)) return;

        Map<Integer, Order> orders = new HashMap<>();
        File file = new File(DATA_DIR + "/Orders_" + date + ".txt");
        if (!file.exists()) {
            loadedDates.put(date, orders);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] t = line.split(DELIM);
                if (t.length < 13) continue;

                int orderNum = Integer.parseInt(t[1].trim());
                String customer = t[2].trim();
                String state = t[3].trim();
                BigDecimal taxRate = new BigDecimal(t[4].trim());
                String productType = t[5].trim();
                BigDecimal area = new BigDecimal(t[6].trim());
                BigDecimal costPSF = new BigDecimal(t[7].trim());
                BigDecimal laborPSF = new BigDecimal(t[8].trim());
                BigDecimal materialCost = new BigDecimal(t[9].trim());
                BigDecimal laborCost = new BigDecimal(t[10].trim());
                BigDecimal tax = new BigDecimal(t[11].trim());
                BigDecimal total = new BigDecimal(t[12].trim());

                Order o = new Order();
                o.setOrderDate(date);
                o.setOrderNumber(orderNum);
                o.setCustomerName(customer);
                o.setState(state);
                o.setTaxRate(taxRate);
                o.setProductType(productType);
                o.setArea(area);
                o.setCostPerSquareFoot(costPSF);
                o.setLaborCostPerSquareFoot(laborPSF);
                o.setMaterialCost(materialCost);
                o.setLaborCost(laborCost);
                o.setTax(tax);
                o.setTotal(total);

                orders.put(orderNum, o);

                if (orderNum > highestOrderNumber)
                    highestOrderNumber = orderNum;
            }
        }
        loadedDates.put(date, orders);
    }

    /** -------------------- Write Orders for a Date -------------------- */
    private void writeOrdersForDate(LocalDate date) throws IOException {
        Map<Integer, Order> orders = loadedDates.get(date);
        if (orders == null) return;

        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(DATA_DIR + "/Orders_" + date + ".txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("OrderDate,OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total");
            for (Order o : orders.values()) {
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

    private String escape(String s) {
        return s.replace(",", ";"); // simple CSV escape
    }

    /** -------------------- OrderDao Interface Methods -------------------- */
    @Override
    public int getNextOrderNumber() {
        return ++highestOrderNumber;
    }

    @Override
    public void addOrder(Order order) {
        try {
            loadOrdersForDate(order.getOrderDate());
            loadedDates.get(order.getOrderDate()).put(order.getOrderNumber(), order);
            writeOrdersForDate(order.getOrderDate());
        } catch (IOException e) {
            throw new RuntimeException("Could not save order", e);
        }
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) {
        try {
            loadOrdersForDate(date);
        } catch (IOException e) {
            throw new RuntimeException("Could not load orders for " + date, e);
        }
        Map<Integer, Order> map = loadedDates.get(date);
        return (map == null) ? null : map.get(orderNumber);
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) {
        try {
            loadOrdersForDate(date);
        } catch (IOException e) {
            throw new RuntimeException("Could not load orders for " + date, e);
        }
        Map<Integer, Order> map = loadedDates.get(date);
        if (map == null) return Collections.emptyList();
        return new ArrayList<>(map.values());
    }

    @Override
    public void removeOrder(LocalDate date, int orderNumber) {
        try {
            loadOrdersForDate(date);
            Map<Integer, Order> map = loadedDates.get(date);
            if (map != null) {
                map.remove(orderNumber);
                if (map.isEmpty()) loadedDates.remove(date);
                writeOrdersForDate(date);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not remove order", e);
        }
    }

    @Override
    public Map<LocalDate, Map<Integer, Order>> getAllOrders() {
        Map<LocalDate, Map<Integer, Order>> allOrders = new HashMap<>();

        File dir = new File(DATA_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            return allOrders; // empty map if no data folder
        }

        File[] files = dir.listFiles((d, name) -> name.startsWith("Orders") && name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            return allOrders;
        }

        Arrays.sort(files);

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String header = br.readLine();
                String line;
                while ((line = br.readLine()) != null) {
                    String[] t = line.split(",");
                    if (t.length < 13) continue;

                    LocalDate date = LocalDate.parse(t[0].trim());
                    int orderNum = Integer.parseInt(t[1].trim());
                    String customer = t[2].trim();
                    String state = t[3].trim();
                    BigDecimal taxRate = new BigDecimal(t[4].trim());
                    String productType = t[5].trim();
                    BigDecimal area = new BigDecimal(t[6].trim());
                    BigDecimal costPSF = new BigDecimal(t[7].trim());
                    BigDecimal laborPSF = new BigDecimal(t[8].trim());
                    BigDecimal materialCost = new BigDecimal(t[9].trim());
                    BigDecimal laborCost = new BigDecimal(t[10].trim());
                    BigDecimal tax = new BigDecimal(t[11].trim());
                    BigDecimal total = new BigDecimal(t[12].trim());

                    Order o = new Order();
                    o.setOrderDate(date);
                    o.setOrderNumber(orderNum);
                    o.setCustomerName(customer);
                    o.setState(state);
                    o.setTaxRate(taxRate);
                    o.setProductType(productType);
                    o.setArea(area);
                    o.setCostPerSquareFoot(costPSF);
                    o.setLaborCostPerSquareFoot(laborPSF);
                    o.setMaterialCost(materialCost);
                    o.setLaborCost(laborCost);
                    o.setTax(tax);
                    o.setTotal(total);

                    // add to map
                    allOrders.computeIfAbsent(date, k -> new HashMap<>()).put(orderNum, o);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load orders from file: " + file.getName(), e);
            }
        }

        return allOrders;
    }


}
