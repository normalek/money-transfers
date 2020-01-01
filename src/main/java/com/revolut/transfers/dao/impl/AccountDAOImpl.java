package com.revolut.transfers.dao.impl;

import com.revolut.transfers.config.JDBIConfig;
import com.revolut.transfers.dao.AccountDAO;
import com.revolut.transfers.dao.repository.AccountRepository;
import com.revolut.transfers.entity.Account;
import com.revolut.transfers.exception.account.AccountNotFoundException;
import com.revolut.transfers.exception.account.AccountStatusException;
import com.revolut.transfers.util.ErrorEnum;
import com.revolut.transfers.util.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class AccountDAOImpl implements AccountDAO {
    private Jdbi jdbi = JDBIConfig.getJdbi();

    @Override
    public Account getById(Integer id) {
        return jdbi.withExtension(AccountRepository.class,
                dao -> dao.getById(id)
                        .orElseThrow(() -> new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_FOUND)));
    }

    @Override
    public BigDecimal getBalanceById(Integer id) {
        return jdbi.withExtension(AccountRepository.class,
                dao -> dao.getById(id)
                        .orElseThrow(() -> new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_FOUND))).getBalance();
    }

    @Override
    public List<Account> getAll() {
        return jdbi.withExtension(AccountRepository.class, AccountRepository::getAll);
    }

    @Override
    public Integer createAccount(String alias, String type, BigDecimal balance, String currency) {
        return jdbi.withExtension(AccountRepository.class,
                rep -> rep.create(alias, type, balance, currency));
    }

    @Override
    public boolean deactivateAccount(Integer id) {
        try {
            return jdbi.withExtension(AccountRepository.class, rep ->
                    rep.changeStatusAccount(id, StatusEnum.Account.CLOSED.name()));
        } catch (Exception e) {
            log.error("Error while closing the account : {}", id);
            throw new AccountStatusException(ErrorEnum.ACCOUNT_CLOSING_ERROR);
        }
    }

    @Override
    public boolean activateAccount(Integer id) {
        try {
            return jdbi.withExtension(AccountRepository.class, rep ->
                    rep.changeStatusAccount(id, StatusEnum.Account.ACTIVE.name()));
        } catch (Exception e) {
            log.error("Error while reopen the account : {}", id);
            throw new AccountStatusException(ErrorEnum.ACCOUNT_REOPEN_ERROR);
        }
    }
}
