package com.revolut.transfers.integration;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.revolut.transfers.api.dto.rs.AccountPartialRs;
import com.revolut.transfers.api.dto.rs.AccountRs;
import com.revolut.transfers.api.dto.rs.ErrorRs;
import com.revolut.transfers.util.ErrorEnum;
import com.revolut.transfers.util.StatusEnum;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.revolut.transfers.util.Constants.*;
import static com.revolut.transfers.util.Converter.convertHttpResponseToString;
import static com.revolut.transfers.util.IntegrationMocks.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountRoutesAPITest {
    @Rule
    public WireMockRule mockAccountsRule = new WireMockRule(TEST_PORT_ACCOUNTS);
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void whenAddAccount_thenReturnAccountRs() throws IOException {
        mockAccountsRule.stubFor(post(urlEqualTo(TEST_ADD_ACCOUNT_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(201)
                        .withBody(new Gson().toJson(mockAccountActiveRs()))));

        HttpPost request = new HttpPost(TEST_ADD_ACCOUNT_FULL_URL);
        request.setEntity(new ByteArrayEntity(new Gson().toJson(mockAccountRq()).getBytes(StandardCharsets.UTF_8)));
        HttpResponse httpResponse = httpClient.execute(request);

        AccountRs accountResponse = new Gson().fromJson(convertHttpResponseToString(httpResponse), AccountRs.class);

        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.CREATED_201);
        assertThat(accountResponse.getStatus()).isEqualTo(StatusEnum.Account.ACTIVE.name());
    }

    @Test
    public void whenAddAccountWrongBalance_thenReturnErrorRs() throws IOException {
        mockAccountsRule.stubFor(post(urlEqualTo(TEST_ADD_ACCOUNT_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(ErrorEnum.INITIAL_BALANCE_NEGATIVE.getCode())
                        .withBody(new Gson().toJson(mockErrorRsWrongBalance()))));

        HttpPost request = new HttpPost(TEST_ADD_ACCOUNT_FULL_URL);
        request.setEntity(new ByteArrayEntity(new Gson().toJson(mockAccountRqWrongBalance()).getBytes(StandardCharsets.UTF_8)));
        HttpResponse httpResponse = httpClient.execute(request);

        ErrorRs error = new Gson().fromJson(convertHttpResponseToString(httpResponse), ErrorRs.class);

        assertThat(error.getErrorCode()).isEqualTo(ErrorEnum.INITIAL_BALANCE_NEGATIVE.getCode());
    }

    @Test
    public void whenAddAccountWrongType_thenReturnErrorRs() throws IOException {
        mockAccountsRule.stubFor(post(urlEqualTo(TEST_ADD_ACCOUNT_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(ErrorEnum.INVALID_ACCOUNT_TYPE.getCode())
                        .withBody(new Gson().toJson(mockErrorRsWrongType()))));

        HttpPost request = new HttpPost(TEST_ADD_ACCOUNT_FULL_URL);
        request.setEntity(new ByteArrayEntity(new Gson().toJson(mockAccountRqWrongType()).getBytes(StandardCharsets.UTF_8)));
        HttpResponse httpResponse = httpClient.execute(request);

        ErrorRs error = new Gson().fromJson(convertHttpResponseToString(httpResponse), ErrorRs.class);

        assertThat(error.getErrorCode()).isEqualTo(ErrorEnum.INITIAL_BALANCE_NEGATIVE.getCode());
    }

    @Test
    public void whenGetAllAccounts_thenReturnListAccountRs() throws IOException {
        mockAccountsRule.stubFor(get(urlEqualTo(TEST_GET_ALL_ACCOUNTS_URL))
                .willReturn(aResponse()
                        .withBody(new Gson().toJson(mockAccountRsList()))));

        HttpGet request = new HttpGet(TEST_GET_ALL_ACCOUNTS_FULL_URL);
        HttpResponse httpResponse = httpClient.execute(request);

        AccountRs[] responseList = new Gson().fromJson(convertHttpResponseToString(httpResponse), AccountRs[].class);

        assertThat(responseList[0].getAccountNumber()).isEqualTo(TEST_ACCOUNT_ID);
    }

    @Test
    public void whenWithdrawNotEnoughMoney_thenThrowAmountOverdrawnException() throws IOException {
        mockAccountsRule.stubFor(post(urlEqualTo(TEST_WITHDRAW_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(ErrorEnum.TRANSACTION_EXCEPTION.getCode())
                        .withBody(new Gson().toJson(mockErrorRsOverdrawn()))));

        HttpPost request = new HttpPost(TEST_WITHDRAW_FULL_URL);
        request.setEntity(new ByteArrayEntity(new Gson().toJson(moneyRequest()).getBytes(StandardCharsets.UTF_8)));
        HttpResponse httpResponse = httpClient.execute(request);

        ErrorRs errorResponse = new Gson().fromJson(convertHttpResponseToString(httpResponse), ErrorRs.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorEnum.TRANSACTION_EXCEPTION.getCode());
        assertThat(errorResponse.getType()).isEqualTo(ErrorEnum.TRANSACTION_EXCEPTION.getType());
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorEnum.TRANSACTION_EXCEPTION.getMessage());
    }

    @Test
    public void whenWithdrawDeactivatedAccount_thenThrowDataValidationException() throws IOException {
        mockAccountsRule.stubFor(post(urlEqualTo(TEST_WITHDRAW_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(ErrorEnum.INVALID_ACCOUNT_STATUS.getCode())
                        .withBody(new Gson().toJson(mockErrorRsDeactivated()))));

        HttpPost request = new HttpPost(TEST_WITHDRAW_FULL_URL);
        request.setEntity(new ByteArrayEntity(new Gson().toJson(moneyRequest()).getBytes(StandardCharsets.UTF_8)));
        HttpResponse httpResponse = httpClient.execute(request);

        ErrorRs errorResponse = new Gson().fromJson(convertHttpResponseToString(httpResponse), ErrorRs.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorEnum.INVALID_ACCOUNT_STATUS.getCode());
        assertThat(errorResponse.getType()).isEqualTo(ErrorEnum.INVALID_ACCOUNT_STATUS.getType());
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorEnum.INVALID_ACCOUNT_STATUS.getMessage());
    }

    @Test
    public void whenDeactivateAccount_thenReturnAccountPartialRs() throws IOException {
        mockAccountsRule.stubFor(put(urlEqualTo(TEST_DEACTIVATE_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(200)
                        .withBody(new Gson().toJson(mockAccountClosedRs()))));

        HttpPut request = new HttpPut(TEST_DEACTIVATE_FULL_URL);
        HttpResponse httpResponse = httpClient.execute(request);

        AccountPartialRs account = new Gson().fromJson(convertHttpResponseToString(httpResponse), AccountPartialRs.class);

        assertThat(account.getStatus()).isEqualTo(StatusEnum.Account.CLOSED.name());
        assertThat(account.getAccountNumber()).isEqualTo(TEST_ACCOUNT_ID);
    }

    @Test
    public void whenActivateAccount_thenReturnAccountPartialRs() throws IOException {
        mockAccountsRule.stubFor(put(urlEqualTo(TEST_ACTIVATE_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(200)
                        .withBody(new Gson().toJson(mockAccountActiveRs()))));

        HttpPut request = new HttpPut(TEST_ACTIVATE_FULL_URL);
        HttpResponse httpResponse = httpClient.execute(request);

        AccountPartialRs account = new Gson().fromJson(convertHttpResponseToString(httpResponse), AccountPartialRs.class);

        assertThat(account.getStatus()).isEqualTo(StatusEnum.Account.ACTIVE.name());
        assertThat(account.getAccountNumber()).isEqualTo(TEST_ACCOUNT_ID);
    }


}
