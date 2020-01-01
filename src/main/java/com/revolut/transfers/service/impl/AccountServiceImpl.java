package com.revolut.transfers.service.impl;

import com.google.inject.Inject;
import com.revolut.transfers.api.dto.rq.AccountAmountRq;
import com.revolut.transfers.api.dto.rq.AccountRq;
import com.revolut.transfers.api.dto.rs.AccountPartialRs;
import com.revolut.transfers.api.dto.rs.AccountRs;
import com.revolut.transfers.api.dto.rs.AccountRsList;
import com.revolut.transfers.dao.AccountDAO;
import com.revolut.transfers.dao.TransfersDAO;
import com.revolut.transfers.entity.Account;
import com.revolut.transfers.service.AccountService;
import com.revolut.transfers.util.StatusEnum;
import com.revolut.transfers.validator.AccountValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AccountServiceImpl implements AccountService {

    private final TransfersDAO transfersDAO;
    private final AccountDAO accountDAO;
    private final AccountValidator accountValidator;

    @Inject
    public AccountServiceImpl(TransfersDAO transfersDAO, AccountDAO accountDAO, AccountValidator accountValidator) {
        this.transfersDAO = transfersDAO;
        this.accountDAO = accountDAO;
        this.accountValidator = accountValidator;
    }

    @Override
    public AccountRsList getAllAccounts() {
        List<Account> accounts = accountDAO.getAll();
        List<AccountRs> responses =
                accounts.stream().map(this::mapObject).collect(Collectors.toList());
        return AccountRsList.builder().accounts(responses).build();
    }

    @Override
    public AccountRs getAccountById(Integer id) {
        Account account = accountDAO.getById(id);
        return mapObject(account);
    }

    @Override
    public AccountPartialRs deposit(AccountAmountRq account) {
        accountValidator.validateAccount(account.getAccountNumber());
        transfersDAO.deposit(account.getAccountNumber(), account.getAmount());
        return AccountPartialRs.builder()
                .accountNumber(account.getAccountNumber())
                .status(StatusEnum.Transaction.SUCCESS.name())
                .build();
    }

    @Override
    public AccountPartialRs withdraw(AccountAmountRq account) {
        accountValidator.validateAccount(account.getAccountNumber());
        transfersDAO.withdraw(account.getAccountNumber(), account.getAmount());
        return AccountPartialRs.builder()
                .accountNumber(account.getAccountNumber())
                .status(StatusEnum.Transaction.SUCCESS.name())
                .build();
    }

    @Override
    public AccountPartialRs createAccount(AccountRq account) {
        accountValidator.validateAccountRequest(account);
        Integer accountNumber = accountDAO
                .createAccount(account.getAlias(), account.getType(),
                        account.getAmount(), account.getCurrency());

        log.info("Account created successfully. Account Number : {} ", accountNumber);

        return AccountPartialRs.builder()
                .status(StatusEnum.Account.ACTIVE.name())
                .accountNumber(accountNumber).build();
    }

    @Override
    public AccountPartialRs deactivate(Integer accountNumber) {
        accountDAO.deactivateAccount(accountNumber);

        log.info("Account deactivated successfully. Account Number : {} ", accountNumber);

        return AccountPartialRs.builder()
                .accountNumber(accountNumber)
                .status(StatusEnum.Account.CLOSED.name())
                .build();
    }

    @Override
    public AccountPartialRs activate(Integer accountNumber) {
        accountDAO.activateAccount(accountNumber);

        log.info("Account reactivated successfully. Account Number : {} ", accountNumber);

        return AccountPartialRs.builder()
                .accountNumber(accountNumber)
                .status(StatusEnum.Account.ACTIVE.name())
                .build();
    }

    private AccountRs mapObject(Account source) {
        return AccountRs.builder()
                .accountNumber(source.getId())
                .status(source.getStatus())
                .alias(source.getAlias())
                .type(source.getType())
                .openedDate(source.getOpenedDate())
                .balance(source.getBalance())
                .currency(source.getCurrency()).build();
    }
}
