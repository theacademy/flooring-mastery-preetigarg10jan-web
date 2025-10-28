package com.sg.FloorMaping.newproject.dao;

import com.sg.FloorMaping.newproject.Dao.TaxDaoFileImpl;
import com.sg.FloorMaping.newproject.model.Tax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaxDaoFileImplTest {

    private static final String TEST_FILE = "Taxes.txt";

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary Taxes.txt in the target/classes folder
        String data = "State,Abbreviation,TaxRate\n" +
                      "California,CA,7.25\n" +
                      "Texas,TX,6.25\n";
        Path path = Path.of(getClass().getClassLoader().getResource("").getPath(), TEST_FILE);
        Files.writeString(path, data);
    }

    @Test
    void testGetAllTaxes() throws IOException {
        TaxDaoFileImpl dao = new TaxDaoFileImpl();
        List<Tax> taxes = dao.getAllTaxes();

        assertEquals(2, taxes.size());

        Tax california = taxes.stream().filter(t -> t.getState().equals("California")).findFirst().orElse(null);
        assertNotNull(california);
        assertEquals("CA", california.getStateAbr());
        assertEquals(new BigDecimal("7.25"), california.getTaxRate());
    }

    @Test
    void testGetTaxByState() throws IOException {
        TaxDaoFileImpl dao = new TaxDaoFileImpl();
        Tax texas = dao.getTaxByState("Texas");

        assertNotNull(texas);
        assertEquals("TX", texas.getStateAbr());
        assertEquals(new BigDecimal("6.25"), texas.getTaxRate());

        Tax unknown = dao.getTaxByState("Florida");
        assertNull(unknown);
    }
}
