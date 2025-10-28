package com.sg.FloorMaping.newproject.Dao;

import com.sg.FloorMaping.newproject.model.Product;
import com.sg.FloorMaping.newproject.model.Tax;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public interface TaxDao {
    List<Tax> getAllTaxes();
    Tax getTaxByState(String state);
}

