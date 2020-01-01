package com.revolut.transfers.api.dto.rq;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferRq {
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String currency;
}
