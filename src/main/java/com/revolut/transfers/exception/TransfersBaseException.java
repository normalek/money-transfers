package com.revolut.transfers.exception;

import lombok.Getter;

/**
 * A Base exception for all types of validation exception which
 * different exception sub types inherits.
 */
@Getter
public class TransfersBaseException extends BaseException {
    private final int errorCode;
    private final String type;

    public TransfersBaseException(String message, int errorCode, String type) {
        super(message);
        this.errorCode = errorCode;
        this.type = type;
    }
}