package com.revolut.transfers.exception;

import com.revolut.transfers.util.ErrorEnum;

public class TransactionException extends TransfersBaseException {
    public TransactionException(ErrorEnum errorEnum, String message) {
        super(message, errorEnum.getCode(), errorEnum.getType());
    }
}
