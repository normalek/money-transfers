package com.revolut.transfers.exception.account;

import com.revolut.transfers.exception.TransfersBaseException;
import com.revolut.transfers.util.ErrorEnum;

public class AmountOverdrawnException extends TransfersBaseException {
    public AmountOverdrawnException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage(), errorEnum.getCode(), errorEnum.getType());
    }
}
