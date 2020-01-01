package com.revolut.transfers.exception.account;

import com.revolut.transfers.exception.TransfersBaseException;
import com.revolut.transfers.util.ErrorEnum;

public class AccountBalanceException extends TransfersBaseException {
    public AccountBalanceException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage(), errorEnum.getCode(), errorEnum.getType());
    }
}
