package com.revolut.transfers.util;

public enum ErrorEnum {
    //Common
    INVALID_ACCOUNT_FORMAT("Account format is not valid", 400, Constants.DATA_VALIDATION),
    FROM_TO_ACCOUNT_IS_SAME("From and To account can't be the same", 400, Constants.DATA_VALIDATION),
    TRANSACTION_EXCEPTION("Exception while performing the transaction", 402, "TRANSACTION_ERROR"),

    //Account
    INITIAL_BALANCE_NEGATIVE("Account balance can't be negative at open", 400, Constants.DATA_VALIDATION),
    INVALID_ACCOUNT_TYPE("Account type should be Checking or Savings", 400, Constants.DATA_VALIDATION),
    INVALID_ACCOUNT_STATUS("Account must be active for further transactions", 400, Constants.DATA_VALIDATION),
    ACCOUNT_CLOSING_ERROR("Unable to close the account.", 402, "ACCOUNT_CLOSING_ERROR"),
    ACCOUNT_REOPEN_ERROR("Unable to reopen the account.", 402, "ACCOUNT_REOPEN_ERROR"),

    //Transfer
    INVALID_CURRENCY("Account must have the same currency for transfer", 400, Constants.DATA_VALIDATION),
    TRANSFER_AMOUNT_ZERO_OR_NEGATIVE("Transfer amount can't be negative", 400, Constants.DATA_VALIDATION),
    ACCOUNT_NOT_FOUND("Account doesn't exists", 404, Constants.DATA_VALIDATION),
    AMOUNT_OVERDRAWN("Transfer amount exceeds available balance", 402, "AMOUNT_OVERDRAWN");

    private String message;
    private int code;
    private String type;

    ErrorEnum(String message, int code, String type) {
        this.message = message;
        this.code = code;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    private static class Constants {
        public static final String DATA_VALIDATION = "DATA_VALIDATION";
    }
}
