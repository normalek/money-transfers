package com.revolut.transfers.validator;

import com.google.inject.Inject;
import com.revolut.transfers.api.dto.rq.TransferRq;
import com.revolut.transfers.dao.AccountDAO;
import com.revolut.transfers.entity.Account;
import com.revolut.transfers.exception.DataValidationException;
import com.revolut.transfers.util.ErrorEnum;

import java.math.BigDecimal;

public class TransferValidator {

    private final AccountDAO accountDAO;

    @Inject
    public TransferValidator(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public void validateTransferRequest(TransferRq request) {
        //Data Validation
        try {
            Integer.valueOf(request.getFromAccount());
            Integer.valueOf(request.getToAccount());
        } catch (NumberFormatException e) {
            throw new DataValidationException(ErrorEnum.INVALID_ACCOUNT_FORMAT);
        }
        if (request.getFromAccount().equalsIgnoreCase(request.getToAccount())) {
            throw new DataValidationException(ErrorEnum.FROM_TO_ACCOUNT_IS_SAME);
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new DataValidationException(ErrorEnum.TRANSFER_AMOUNT_ZERO_OR_NEGATIVE);
        }

        //Domain Validation
        Account fromAccount = accountDAO.getById(Integer.valueOf(request.getFromAccount()));
        if ("CLOSED".equals(fromAccount.getStatus())) {
            throw new DataValidationException(ErrorEnum.INVALID_ACCOUNT_STATUS);
        }
        if (!request.getCurrency().equals(fromAccount.getCurrency())) {
            throw new DataValidationException(ErrorEnum.INVALID_CURRENCY);
        }
        Account toAccount = accountDAO.getById(Integer.valueOf(request.getToAccount()));
        if ("CLOSED".equals(toAccount.getStatus())) {
            throw new DataValidationException(ErrorEnum.INVALID_ACCOUNT_STATUS);
        }
        if (!request.getCurrency().equals(toAccount.getCurrency())) {
            throw new DataValidationException(ErrorEnum.INVALID_CURRENCY);
        }
    }
}
