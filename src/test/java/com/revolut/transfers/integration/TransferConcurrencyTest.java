package com.revolut.transfers.integration;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.revolut.transfers.MoneyTransfersMain;
import com.revolut.transfers.api.dto.rq.AccountRq;
import com.revolut.transfers.api.dto.rq.TransferRq;
import com.revolut.transfers.api.dto.rs.AccountRs;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class tests for concurrency tests with 100 threads via
 * an executor service fixed thread pool. after the transactions completes
 * asserts the remaining balance whether correct or not.
 * <p>
 * The transfer transaction is being made synchronized after concurrency test.
 */
public class TransferConcurrencyTest {
    @BeforeClass
    public static void beforeClass() throws Exception {
        MoneyTransfersMain.main(null);
        Spark.awaitInitialization();
    }

    @Test
    public void verifyBalanceAfterConcurrencyTransfer() throws UnirestException, InterruptedException {
        // Create account#1 with deposit of $30000
        HttpResponse<String> responseOne = Unirest.post("http://localhost:4567/v1/account/add")
                .header("accept", "application/json")
                .body(new Gson().toJson(accountOne()))
                .asString();
        AccountRs acctResponseOne = new Gson().fromJson(responseOne.getBody(), AccountRs.class);

        // Create account#2 with a deposit of $0
        HttpResponse<String> responseTwo = Unirest.post("http://localhost:4567/v1/account/add")
                .header("accept", "application/json")
                .body(new Gson().toJson(accountTwo()))
                .asString();
        AccountRs acctResponseTwo = new Gson().fromJson(responseTwo.getBody(), AccountRs.class);

        // Create 100 threads and transfer $10.00 from account 1 to account 2
        final int threads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                TransferRq transferRequest =
                        transferFromAccountOneToAccountTwo(acctResponseOne.getAccountNumber().toString(),
                                acctResponseTwo.getAccountNumber().toString());
                try {
                    Unirest.post("http://localhost:4567/v1/transfer")
                            .header("accept", "application/json")
                            .body(new Gson().toJson(transferRequest))
                            .asString();
                } catch (UnirestException e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // Check the remaining balance in account#1 is $29000 (30000 - (100 * 10)) in Account Table
        final String accountOne = acctResponseOne.getAccountNumber().toString();
        HttpResponse<String> responseOneAfter = Unirest.get("http://localhost:4567/v1/account/" + accountOne)
                .asString();
        AccountRs accountOneAfter =
                new Gson().fromJson(responseOneAfter.getBody(), AccountRs.class);
        // Check the new balance in account#2 is #100
        final String accountTwo = acctResponseTwo.getAccountNumber().toString();
        HttpResponse<String> responseTwoAfter = Unirest.get("http://localhost:4567/v1/account/" + accountTwo)
                .asString();
        AccountRs accountTwoAfter =
                new Gson().fromJson(responseTwoAfter.getBody(), AccountRs.class);

        // Finally Check the total records in Transfer Table Should be 100 with $10 each
        // Account 1 - 29900 & Account 2- 100
        assertThat(accountOneAfter.getBalance()).isEqualByComparingTo(new BigDecimal(29000));
        assertThat(accountTwoAfter.getBalance()).isEqualByComparingTo(new BigDecimal(1000));
    }

    @AfterClass
    public static void afterClass() throws InterruptedException {
        Spark.stop();
        Thread.sleep(2000);
    }

    private AccountRq accountOne() {
        return AccountRq.builder()
                .alias("My Checking Account - One")
                .type("Checking")
                .amount(new BigDecimal(30000))
                .currency("USD")
                .build();
    }

    private AccountRq accountTwo() {
        return AccountRq.builder()
                .alias("My Savings Account - Two")
                .type("Savings")
                .amount(new BigDecimal(0))
                .currency("USD")
                .build();
    }

    private TransferRq transferFromAccountOneToAccountTwo(String fromAcct, String toAcct) {
        return TransferRq.builder()
                .fromAccount(fromAcct)
                .toAccount(toAcct)
                .amount(BigDecimal.valueOf(10))
                .currency("USD")
                .build();
    }
}
