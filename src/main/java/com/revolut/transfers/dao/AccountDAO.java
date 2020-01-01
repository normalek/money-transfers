package com.revolut.transfers.dao;

import com.revolut.transfers.entity.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {
    Account getById(Integer id);

    BigDecimal getBalanceById(Integer id);

    List<Account> getAll();

    Integer createAccount(String alias, String type, BigDecimal balance, String currency);

    boolean deactivateAccount(Integer id);

    boolean activateAccount(Integer id);
}
