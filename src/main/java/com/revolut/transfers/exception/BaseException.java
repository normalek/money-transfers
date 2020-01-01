package com.revolut.transfers.exception;

public class BaseException extends RuntimeException {
    public BaseException() {
        super();
    }

    public BaseException(String s) {
        super(s);
    }

    public BaseException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public BaseException(Throwable throwable) {
        super(throwable);
    }
}
