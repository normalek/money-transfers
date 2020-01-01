package com.revolut.transfers.entity;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Account {
    private Integer id;
    private String status;
    private String openedDate;
    private String alias;
    private String type;
    private BigDecimal balance;
    private String currency;
}
