package com.sg.FloorMaping.newproject.Dao;

import com.sg.FloorMaping.newproject.model.Product;

import java.util.List;

public interface ProductDao {
    List<Product> getAllProducts();
    Product getProductByType(String productType);
}
