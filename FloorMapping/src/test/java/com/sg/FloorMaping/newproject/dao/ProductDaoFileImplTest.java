package com.sg.FloorMaping.newproject.dao;

import com.sg.FloorMaping.newproject.Dao.ProductDaoFileImpl;
import com.sg.FloorMaping.newproject.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductDaoFileImplTest {

    private static final String TEST_FILE = "Products.txt";

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary Products.txt in the target/classes folder
        String data = "ProductType,CostPerSquareFoot,LaborCostPerSquareFoot\n" +
                      "Wood,5.15,4.75\n" +
                      "Tile,3.50,4.15\n";
        Path path = Path.of(getClass().getClassLoader().getResource("").getPath(), TEST_FILE);
        Files.writeString(path, data);
    }

    @Test
    void testGetAllProducts() throws IOException {
        ProductDaoFileImpl dao = new ProductDaoFileImpl();
        List<Product> products = dao.getAllProducts();

        assertEquals(2, products.size());

        Product wood = products.stream().filter(p -> p.getProductType().equals("Wood")).findFirst().orElse(null);
        assertNotNull(wood);
        assertEquals(new BigDecimal("5.15"), wood.getCostPerSquareFoot());
        assertEquals(new BigDecimal("4.75"), wood.getLaborCostPerSquareFoot());
    }

    @Test
    void testGetProductByType() throws IOException {
        ProductDaoFileImpl dao = new ProductDaoFileImpl();
        Product tile = dao.getProductByType("Tile");

        assertNotNull(tile);
        assertEquals(new BigDecimal("3.50"), tile.getCostPerSquareFoot());
        assertEquals(new BigDecimal("4.15"), tile.getLaborCostPerSquareFoot());

        Product unknown = dao.getProductByType("Carpet");
        assertNull(unknown);
    }
}
