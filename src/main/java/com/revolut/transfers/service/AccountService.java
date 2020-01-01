package com.revolut.transfers.service;

import com.revolut.transfers.api.dto.rq.AccountAmountRq;
import com.revolut.transfers.api.dto.rq.AccountRq;
import com.revolut.transfers.api.dto.rs.AccountPartialRs;
import com.revolut.transfers.api.dto.rs.AccountRs;
import com.revolut.transfers.api.dto.rs.AccountRsList;

public interface AccountService {
    AccountRsList getAllAccounts();

    AccountRs getAccountById(Integer id);

    AccountPartialRs deposit(AccountAmountRq account);

    AccountPartialRs withdraw(AccountAmountRq account);

    AccountPartialRs createAccount(AccountRq account);

    AccountPartialRs deactivate(Integer accountNumber);

    AccountPartialRs activate(Integer accountNumber);
}
