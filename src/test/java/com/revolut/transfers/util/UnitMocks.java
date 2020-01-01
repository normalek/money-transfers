package com.revolut.transfers.util;

import com.revolut.transfers.api.dto.rq.AccountAmountRq;
import com.revolut.transfers.api.dto.rq.AccountRq;
import com.revolut.transfers.api.dto.rq.TransferRq;
import com.revolut.transfers.entity.Account;
import com.revolut.transfers.entity.Transfer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.revolut.transfers.util.Constants.*;

public final class UnitMocks {
    public static AccountRq mockAccountNewRequest() {
        return AccountRq.builder()
                .alias("Mocking account")
                .type("Checking")
                .amount(TEST_AMOUNT)
                .currency(TEST_CURRENCY_EUR)
                .build();
    }

    public static List<Account> mockAccountList() {
        List<Account> accountList = new ArrayList<>();
        accountList.add(mockOneAccount());
        accountList.add(Account.builder()
                .id(TEST_ACCOUNT_ID)
                .alias("Test savings account")
                .balance(TEST_AMOUNT)
                .currency(TEST_CURRENCY_USD)
                .status(TEST_ACCOUNT_ACTIVE_STATUS)
                .type("Savings")
                .openedDate(LocalDateTime.now().toString())
                .build());
        return accountList;
    }

    public static Account mockOneAccount() {
        return Account.builder()
                .id(TEST_ACCOUNT_ID)
                .alias("Test checking account")
                .balance(TEST_AMOUNT)
                .currency(TEST_CURRENCY_USD)
                .status(TEST_ACCOUNT_ACTIVE_STATUS)
                .type("Checking")
                .openedDate(LocalDateTime.now().toString())
                .build();
    }

    public static AccountAmountRq mockAccountTransRequest() {
        return AccountAmountRq.builder()
                .accountNumber(TEST_ACCOUNT_ID)
                .amount(TEST_AMOUNT)
                .build();
    }

    public static TransferRq mockTransferReq() {
        return TransferRq.builder()
                .fromAccount(TEST_ACCOUNT_ID.toString())
                .toAccount(TEST_ANOTHER_ACCOUNT_ID.toString())
                .amount(TEST_AMOUNT)
                .currency(TEST_CURRENCY_USD)
                .build();
    }

    public static Transfer mockTransferRes() {
        return Transfer.builder()
                .transId(TEST_UUID)
                .status("SUCCESS")
                .transDate(LocalDateTime.now())
                .build();
    }
}
