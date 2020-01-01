package com.revolut.transfers.integration;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.revolut.transfers.MoneyTransfersMain;
import com.revolut.transfers.api.dto.rq.AccountRq;
import com.revolut.transfers.api.dto.rs.AccountRs;
import com.revolut.transfers.util.Constants;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.revolut.transfers.util.IntegrationMocks.mockAccountRq;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test ensure that when concurrent deposit and withdraw happens the account balance
 * stays consistent with the writes.
 */
public class DepositWithDrawConcurrencyTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        MoneyTransfersMain.main(null);
        Spark.awaitInitialization();
    }

    @Test
    public void depositWithDrawConcurrencyTest() throws UnirestException, InterruptedException {
        //Create an account with 10000 balance
        HttpResponse<String> responseOne = Unirest.post("http://localhost:4567/v1/account/add")
                .header("accept", "application/json")
                .body(new Gson().toJson(mockAccountRq()))
                .asString();
        AccountRs acctResponse = new Gson().fromJson(responseOne.getBody(), AccountRs.class);

        //Execute 100 deposit and 100 withdraw concurrently with same amount
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(100);
        IntStream.range(0, 100).forEach(i ->
                executor.submit(() -> {
                    try {
                        AccountRq depReq = depositRequest(acctResponse.getAccountNumber().toString(),
                                new BigDecimal(100));
                        Unirest.post("http://localhost:4567/v1/account/deposit")
                                .header("accept", "application/json")
                                .body(new Gson().toJson(depReq))
                                .asString();

                        AccountRq depWth = withdrawRequest(acctResponse.getAccountNumber().toString(),
                                new BigDecimal(100));
                        Unirest.post("http://localhost:4567/v1/account/withdraw")
                                .header("accept", "application/json")
                                .body(new Gson().toJson(depWth))
                                .asString();
                        latch.await();
                    } catch (UnirestException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }));

        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        HttpResponse<String> responseAcct = Unirest.get("http://localhost:4567/v1/account/" + acctResponse.getAccountNumber())
                .asString();
        AccountRs accountAfter =
                new Gson().fromJson(responseAcct.getBody(), AccountRs.class);

        //Verify account balance should be 10000 as before
        assertThat(accountAfter.getBalance()).isEqualByComparingTo(Constants.TEST_AMOUNT);
    }

    @Test
    public void overDrawnMultiThreadTest() throws UnirestException, InterruptedException {
        //Create an account with 10000 balance
        HttpResponse<String> responseOne = Unirest.post("http://localhost:4567/v1/account/add")
                .header("accept", "application/json")
                .body(new Gson().toJson(mockAccountRq()))
                .asString();
        AccountRs acctResponse = new Gson().fromJson(responseOne.getBody(), AccountRs.class);

        //Execute 101 withdraw concurrently and this will throw AMOUNT_OVERDRAWN exception
        final int threads = 101;
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                try {
                    AccountRq depWth = withdrawRequest(acctResponse.getAccountNumber().toString(),
                            new BigDecimal(100));
                    Unirest.post("http://localhost:4567/v1/account/withdraw")
                            .header("accept", "application/json")
                            .body(new Gson().toJson(depWth))
                            .asString();
                    latch.await();
                } catch (UnirestException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        latch.countDown();
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        HttpResponse<String> responseAcct = Unirest.get("http://localhost:4567/v1/account/" + acctResponse.getAccountNumber())
                .asString();
        AccountRs accountAfter =
                new Gson().fromJson(responseAcct.getBody(), AccountRs.class);

        //Verify account balance should be 0 and not negative
        assertThat(accountAfter.getBalance()).isGreaterThanOrEqualTo(new BigDecimal(0));
    }

    private AccountRq depositRequest(String accountNumber, BigDecimal amount) {
        return AccountRq.builder()
                .accountNumber(accountNumber)
                .amount(amount)
                .build();
    }

    private AccountRq withdrawRequest(String accountNumber, BigDecimal amount) {
        return AccountRq.builder()
                .accountNumber(accountNumber)
                .amount(amount)
                .build();
    }
}
