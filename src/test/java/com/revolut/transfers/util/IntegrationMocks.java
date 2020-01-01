package com.revolut.transfers.util;

import com.revolut.transfers.api.dto.rq.AccountRq;
import com.revolut.transfers.api.dto.rq.TransferRq;
import com.revolut.transfers.api.dto.rs.AccountPartialRs;
import com.revolut.transfers.api.dto.rs.AccountRs;
import com.revolut.transfers.api.dto.rs.ErrorRs;
import com.revolut.transfers.api.dto.rs.TransferRs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.revolut.transfers.util.Constants.*;

public final class IntegrationMocks {
    public static AccountRq mockAccountRq() {
        return AccountRq.builder()
                .alias("Test Checking Account")
                .type("Checking")
                .amount(Constants.TEST_AMOUNT)
                .currency(TEST_CURRENCY_USD)
                .build();
    }

    public static AccountRq mockAccountRqWrongBalance() {
        return AccountRq.builder()
                .alias("Test Checking Account")
                .type("Checking")
                .amount(Constants.TEST_AMOUNT_MINUS)
                .currency(TEST_CURRENCY_USD)
                .build();
    }

    public static AccountRq mockAccountRqWrongType() {
        return AccountRq.builder()
                .alias("Test Checking Account")
                .type("Business")
                .amount(Constants.TEST_AMOUNT_MINUS)
                .currency(TEST_CURRENCY_USD)
                .build();
    }

    public static AccountPartialRs mockAccountActiveRs() {
        return AccountPartialRs.builder()
                .accountNumber(TEST_ACCOUNT_ID)
                .status(Constants.TEST_ACCOUNT_ACTIVE_STATUS)
                .build();
    }

    public static AccountPartialRs mockAccountClosedRs() {
        return AccountPartialRs.builder()
                .accountNumber(TEST_ACCOUNT_ID)
                .status(Constants.TEST_ACCOUNT_CLOSED_STATUS)
                .build();
    }

    public static List<AccountRs> mockAccountRsList() {
        List<AccountRs> accountList = new ArrayList<>();
        accountList.add(AccountRs.builder()
                .accountNumber(TEST_ACCOUNT_ID)
                .alias("Test savings account")
                .balance(TEST_AMOUNT)
                .currency(TEST_CURRENCY_USD)
                .status(TEST_ACCOUNT_ACTIVE_STATUS)
                .type("Savings")
                .openedDate(LocalDateTime.now().toString())
                .build());
        return accountList;
    }

    public static ErrorRs mockErrorRsWrongBalance() {
        return ErrorRs.builder()
                .errorCode(ErrorEnum.INITIAL_BALANCE_NEGATIVE.getCode())
                .message(ErrorEnum.INITIAL_BALANCE_NEGATIVE.getMessage())
                .build();
    }

    public static ErrorRs mockErrorRsWrongType() {
        return ErrorRs.builder()
                .errorCode(ErrorEnum.INVALID_ACCOUNT_TYPE.getCode())
                .message(ErrorEnum.INVALID_ACCOUNT_TYPE.getMessage())
                .build();
    }

    public static ErrorRs mockErrorRsOverdrawn() {
        return ErrorRs.builder()
                .errorCode(ErrorEnum.TRANSACTION_EXCEPTION.getCode())
                .message(ErrorEnum.TRANSACTION_EXCEPTION.getMessage())
                .type(ErrorEnum.TRANSACTION_EXCEPTION.getType())
                .build();
    }

    public static ErrorRs mockErrorRsDeactivated() {
        return ErrorRs.builder()
                .errorCode(ErrorEnum.INVALID_ACCOUNT_STATUS.getCode())
                .message(ErrorEnum.INVALID_ACCOUNT_STATUS.getMessage())
                .type(ErrorEnum.INVALID_ACCOUNT_STATUS.getType())
                .build();
    }

    public static AccountRq moneyRequest() {
        return AccountRq.builder()
                .accountNumber(TEST_ACCOUNT_ID.toString())
                .amount(TEST_AMOUNT)
                .build();
    }

    public static TransferRq mockTransferRequest() {
        return TransferRq.builder()
                .fromAccount(TEST_ACCOUNT_ID.toString())
                .toAccount(TEST_ANOTHER_ACCOUNT_ID.toString())
                .amount(TEST_AMOUNT)
                .currency(TEST_CURRENCY_USD)
                .build();
    }

    public static TransferRq mockTransferRequestInvalidAccount() {
        return TransferRq.builder()
                .fromAccount(TEST_ACCOUNT_ID.toString())
                .toAccount(TEST_ACCOUNT_ID.toString())
                .amount(TEST_AMOUNT)
                .currency(TEST_CURRENCY_USD)
                .build();
    }

    public static TransferRq mockTransferRequestInvalidAmount() {
        return TransferRq.builder()
                .fromAccount(TEST_ACCOUNT_ID.toString())
                .toAccount(TEST_ANOTHER_ACCOUNT_ID.toString())
                .amount(TEST_AMOUNT_MINUS)
                .currency(TEST_CURRENCY_USD)
                .build();
    }

    public static TransferRq mockTransferRequestInvalidCurrency() {
        return TransferRq.builder()
                .fromAccount(TEST_ACCOUNT_ID.toString())
                .toAccount(TEST_ANOTHER_ACCOUNT_ID.toString())
                .amount(TEST_AMOUNT)
                .currency(TEST_CURRENCY_EUR)
                .build();
    }

    public static TransferRq mockTransferRequestInvalidAccountFormat() {
        return TransferRq.builder()
                .fromAccount(TEST_ACCOUNT_ID.toString())
                .toAccount("ccc111")
                .amount(TEST_AMOUNT)
                .currency(TEST_CURRENCY_USD)
                .build();
    }

    // Transfers section

    public static TransferRs mockTransferRs() {
        return TransferRs.builder()
                .transactionId(TEST_UUID)
                .fromAccount(TEST_ACCOUNT_ID)
                .toAccount(TEST_ANOTHER_ACCOUNT_ID)
                .amount(TEST_AMOUNT)
                .currency(TEST_CURRENCY_USD)
                .status(TEST_STATUS_COMPLETED)
                .build();
    }

    public static ErrorRs mockErrorRsTransferAccount() {
        return ErrorRs.builder()
                .errorCode(ErrorEnum.FROM_TO_ACCOUNT_IS_SAME.getCode())
                .message(ErrorEnum.FROM_TO_ACCOUNT_IS_SAME.getMessage())
                .type(ErrorEnum.FROM_TO_ACCOUNT_IS_SAME.getType())
                .build();
    }

    public static ErrorRs mockErrorRsTransferAmount() {
        return ErrorRs.builder()
                .errorCode(ErrorEnum.TRANSFER_AMOUNT_ZERO_OR_NEGATIVE.getCode())
                .message(ErrorEnum.TRANSFER_AMOUNT_ZERO_OR_NEGATIVE.getMessage())
                .type(ErrorEnum.TRANSFER_AMOUNT_ZERO_OR_NEGATIVE.getType())
                .build();
    }

    public static ErrorRs mockErrorRsTransferCurrency() {
        return ErrorRs.builder()
                .errorCode(ErrorEnum.INVALID_CURRENCY.getCode())
                .message(ErrorEnum.INVALID_CURRENCY.getMessage())
                .type(ErrorEnum.INVALID_CURRENCY.getType())
                .build();
    }

    public static ErrorRs mockErrorRsTransferClosedAccount() {
        return ErrorRs.builder()
                .errorCode(ErrorEnum.INVALID_ACCOUNT_STATUS.getCode())
                .message(ErrorEnum.INVALID_ACCOUNT_STATUS.getMessage())
                .type(ErrorEnum.INVALID_ACCOUNT_STATUS.getType())
                .build();
    }

    public static ErrorRs mockErrorRsTransferInvalidAccountFormat() {
        return ErrorRs.builder()
                .errorCode(ErrorEnum.INVALID_ACCOUNT_FORMAT.getCode())
                .message(ErrorEnum.INVALID_ACCOUNT_FORMAT.getMessage())
                .type(ErrorEnum.INVALID_ACCOUNT_FORMAT.getType())
                .build();
    }

}
