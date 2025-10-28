package com.sg.FloorMaping.newproject.Dao;

import com.sg.FloorMaping.newproject.model.Product;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Repository
public class ProductDaoFileImpl implements ProductDao{

    private static final String PRODUCT_FILE = "Products.txt";
    private static final String DELIM = ",";
    private final Map<String, Product> allProducts = new HashMap<>();

    public ProductDaoFileImpl() throws IOException {
        loadProducts();
    }

    private void loadProducts() throws IOException {
        // Load from classpath
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(PRODUCT_FILE)))) {

            if (br == null) {
                // File not found in resources
                return;
            }

            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(DELIM);
                if (tokens.length < 3) continue;
                String type = tokens[0].trim();
                BigDecimal cost = new BigDecimal(tokens[1].trim());
                BigDecimal labor = new BigDecimal(tokens[2].trim());
                allProducts.put(type, new Product(type, cost, labor));
            }
        }
    }


    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(allProducts.values());
    }

    @Override
    public Product getProductByType(String productType) {
        return allProducts.get(productType);
    }}