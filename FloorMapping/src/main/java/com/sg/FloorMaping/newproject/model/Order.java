package com.sg.FloorMaping.newproject.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Order {

        private int orderNumber;
        private String customerName;
        private String state;
        private BigDecimal taxRate;
        private String productType;
        private BigDecimal costPerSquareFoot;
        private BigDecimal laborCostPerSquareFoot;
        private BigDecimal materialCost;
        private BigDecimal laborCost;
        private BigDecimal total;
        private BigDecimal area;

        private BigDecimal tax;
        private BigDecimal laborCostTotal;

        private BigDecimal laborCostPerSquareFootValue;
        private BigDecimal costPerSquareFootValue;
        private LocalDate orderDate;


}