package com.revolut.transfers.validator;

import com.google.inject.Inject;
import com.revolut.transfers.api.dto.rq.AccountRq;
import com.revolut.transfers.dao.AccountDAO;
import com.revolut.transfers.entity.Account;
import com.revolut.transfers.exception.DataValidationException;
import com.revolut.transfers.util.ErrorEnum;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class AccountValidator {
    private final AccountDAO accountDAO;
    private static final List<String> ACCT_TYPES = Arrays.asList("Checking", "Savings");

    @Inject
    public AccountValidator(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public void validateAccount(Integer inputAccount) {
        Account account = accountDAO.getById(inputAccount);
        if ("CLOSED".equals(account.getStatus())) {
            throw new DataValidationException(ErrorEnum.INVALID_ACCOUNT_STATUS);
        }
    }

    public void validateAccountRequest(AccountRq request) {
        //Data Validation
        if (request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new DataValidationException(ErrorEnum.INITIAL_BALANCE_NEGATIVE);
        }
        if (!ACCT_TYPES.contains(request.getType())) {
            throw new DataValidationException(ErrorEnum.INVALID_ACCOUNT_TYPE);
        }
    }
}
