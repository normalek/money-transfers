package com.revolut.transfers.dao;

import com.revolut.transfers.entity.Transfer;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransfersDAO {
    boolean deposit(Integer id, BigDecimal amount);

    boolean withdraw(Integer id, BigDecimal amount);

    Transfer transfer(Integer fromAccount, Integer toAccount, BigDecimal amount, String currency);

    Transfer findTransferById(UUID transId);
}
