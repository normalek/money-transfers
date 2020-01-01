package com.revolut.transfers.util;

public class StatusEnum {
    public enum Transfer {
        COMPLETED, FAILED, SCHEDULED
    }

    public enum Account {
        ACTIVE, CLOSED
    }

    public enum Transaction {
        SUCCESS
    }
}
