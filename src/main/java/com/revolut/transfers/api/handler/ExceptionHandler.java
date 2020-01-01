package com.revolut.transfers.api.handler;

import com.revolut.transfers.api.dto.rs.ErrorRs;
import com.revolut.transfers.exception.DataValidationException;
import com.revolut.transfers.exception.TransactionException;
import com.revolut.transfers.exception.TransfersBaseException;
import com.revolut.transfers.exception.account.AccountNotFoundException;
import com.revolut.transfers.exception.account.AmountOverdrawnException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;

import static org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500;
import static spark.Spark.exception;

@Slf4j
public class ExceptionHandler {
    public ExceptionHandler() {
        initializeExceptionHandler();
    }

    private void initializeExceptionHandler() {
        exception(Exception.class, (e, req, res) -> {
            ErrorRs errorRs;
            if (e instanceof DataValidationException) {
                res.status(HttpStatus.BAD_REQUEST_400);
                DataValidationException exception = (DataValidationException) e;
                errorRs = ErrorRs.builder()
                        .errorCode(exception.getErrorCode())
                        .message(e.getMessage())
                        .type(exception.getType())
                        .build();
            } else if (e instanceof AmountOverdrawnException) {
                res.status(HttpStatus.PAYMENT_REQUIRED_402);
                AmountOverdrawnException exception = (AmountOverdrawnException) e;
                errorRs = ErrorRs.builder()
                        .errorCode(exception.getErrorCode())
                        .message(e.getMessage())
                        .type(exception.getType())
                        .build();
            } else if (e instanceof TransactionException) {
                res.status(HttpStatus.PAYMENT_REQUIRED_402);
                TransactionException exception = (TransactionException) e;
                errorRs = ErrorRs.builder()
                        .errorCode(exception.getErrorCode())
                        .message(e.getMessage())
                        .type(exception.getType())
                        .build();
            } else if (e instanceof AccountNotFoundException) {
                res.status(HttpStatus.NOT_FOUND_404);
                AccountNotFoundException exception = (AccountNotFoundException) e;
                errorRs = ErrorRs.builder()
                        .errorCode(exception.getErrorCode())
                        .message(e.getMessage())
                        .type(exception.getType())
                        .build();
            } else if (e instanceof TransfersBaseException) {
                res.status(INTERNAL_SERVER_ERROR_500);
                TransfersBaseException exception = (TransfersBaseException) e;
                errorRs = ErrorRs.builder()
                        .errorCode(exception.getErrorCode())
                        .type(exception.getType())
                        .message(e.getMessage())
                        .build();
            } else {
                res.status(INTERNAL_SERVER_ERROR_500);
                errorRs = ErrorRs.builder()
                        .errorCode(INTERNAL_SERVER_ERROR_500)
                        .message(e.getMessage() != null ? limitString(e.getMessage())
                                : limitString(e.toString()))
                        .type(HttpStatus.getMessage(INTERNAL_SERVER_ERROR_500))
                        .build();
            }
            log.error("Exception handler message: {}", e.getMessage());

            res.body(JSONHandler.toJson(errorRs));
            res.header("Content-Type", "application/json");
        });
    }

    private static String limitString(String str) {
        if (str.length() < 100) {
            return str;
        } else {
            return str.substring(0, 100).concat("...");
        }
    }
}
