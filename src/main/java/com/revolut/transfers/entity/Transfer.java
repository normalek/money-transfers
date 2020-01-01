package com.revolut.transfers.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {
    private Integer fromAcc;
    private Integer toAcc;
    private String status;
    private UUID transId;
    private LocalDateTime transDate;
    private BigDecimal amount;
    private String currency;
}
