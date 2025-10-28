package com.sg.FloorMaping.newproject.model;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Tax {

    private String state;
    private String stateAbr;
    private BigDecimal taxRate;

}

