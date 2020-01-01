package com.revolut.transfers.util;

import java.math.BigDecimal;
import java.util.UUID;

public final class Constants {
    // Accounts
    public static final Integer TEST_ACCOUNT_ID = 100010;
    public static final Integer TEST_ANOTHER_ACCOUNT_ID = 100020;
    public static final String TEST_ACCOUNT_ACTIVE_STATUS = "ACTIVE";
    public static final String TEST_ACCOUNT_CLOSED_STATUS = "CLOSED";
    public static final String TEST_CURRENCY_USD = "USD";
    public static final String TEST_CURRENCY_EUR = "EUR";
    public static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(20000);
    public static final BigDecimal TEST_AMOUNT_MINUS = BigDecimal.valueOf(-20000);

    // Transfers
    public static final UUID TEST_UUID = UUID.randomUUID();
    public static final String TEST_STATUS_COMPLETED = "COMPLETED";
    public static final String TEST_STATUS_SUCCESS = "SUCCESS";

    // Integration Accounts
    public static final Integer TEST_PORT_ACCOUNTS = 8089;
    public static final String TEST_ADD_ACCOUNT_FULL_URL = "http://localhost:" + TEST_PORT_ACCOUNTS + "/v1/account/add";
    public static final String TEST_ADD_ACCOUNT_URL = "/v1/account/add";
    public static final String TEST_GET_ALL_ACCOUNTS_FULL_URL = "http://localhost:" + TEST_PORT_ACCOUNTS + "/v1/account/all";
    public static final String TEST_GET_ALL_ACCOUNTS_URL = "/v1/account/all";
    public static final String TEST_WITHDRAW_FULL_URL = "http://localhost:" + TEST_PORT_ACCOUNTS + "/v1/account/withdraw";
    public static final String TEST_WITHDRAW_URL = "/v1/account/withdraw";
    public static final String TEST_DEACTIVATE_FULL_URL = "http://localhost:" + TEST_PORT_ACCOUNTS + "/v1/account/" + TEST_ACCOUNT_ID + "deactivate";
    public static final String TEST_DEACTIVATE_URL = "/v1/account/" + TEST_ACCOUNT_ID + "deactivate";
    public static final String TEST_ACTIVATE_FULL_URL = "http://localhost:" + TEST_PORT_ACCOUNTS + "/v1/account/" + TEST_ACCOUNT_ID + "activate";
    public static final String TEST_ACTIVATE_URL = "/v1/account/" + TEST_ACCOUNT_ID + "activate";

    // Integration Accounts
    public static final Integer TEST_PORT_TRANSFERS = 8090;
    public static final String TEST_TRANSFER_FULL_URL = "http://localhost:" + TEST_PORT_TRANSFERS + "/v1/transfer";
    public static final String TEST_TRANSFER_URL = "/v1/transfer";

}
