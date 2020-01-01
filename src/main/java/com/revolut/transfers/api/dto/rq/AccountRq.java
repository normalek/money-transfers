package com.revolut.transfers.api.dto.rq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRq {
    private String accountNumber;
    private String alias;
    private String type;
    private BigDecimal amount;
    private String currency;
}
