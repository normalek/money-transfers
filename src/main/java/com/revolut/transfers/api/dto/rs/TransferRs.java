package com.revolut.transfers.api.dto.rs;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransferRs {
    private String status;
    private UUID transactionId;
    private LocalDateTime transactionDate;
    private Integer fromAccount;
    private Integer toAccount;
    private BigDecimal amount;
    private String currency;
}
