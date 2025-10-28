package com.sg.FloorMaping.newproject.Dao;

import com.sg.FloorMaping.newproject.model.Tax;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TaxDaoFileImpl implements TaxDao{

    private static final String TAX_FILE = "Taxes.txt";
    private static final String DELIM = ",";
    private final Map<String, Tax> taxes = new HashMap<>();

    public TaxDaoFileImpl() throws IOException {
        loadTaxes();
    }

    private void loadTaxes() throws IOException {
        // Load from classpath
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(TAX_FILE)))) {

            if (br == null) {
                // File not found in resources
                return;
            }

            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(DELIM);
                if (tokens.length < 3) continue;
                String state = tokens[0].trim();
                String abr = tokens[1].trim();
                BigDecimal rate = new BigDecimal(tokens[2].trim());
                taxes.put(state, new Tax(state, abr, rate));
            }
        }
    }


    @Override
    public List<Tax> getAllTaxes() {
        return new ArrayList<>(taxes.values());
    }

    @Override
    public Tax getTaxByState(String state) {
        return taxes.get(state);
    }

}
