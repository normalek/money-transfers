package com.revolut.transfers.exception;

import com.revolut.transfers.util.ErrorEnum;

public class DataValidationException extends TransfersBaseException {
    public DataValidationException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage(), errorEnum.getCode(), errorEnum.getType());
    }
}
