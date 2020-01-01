package com.revolut.transfers.integration;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.revolut.transfers.api.dto.rs.ErrorRs;
import com.revolut.transfers.api.dto.rs.TransferRs;
import com.revolut.transfers.util.ErrorEnum;
import com.revolut.transfers.util.StatusEnum;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.revolut.transfers.util.Constants.*;
import static com.revolut.transfers.util.Converter.convertHttpResponseToString;
import static com.revolut.transfers.util.IntegrationMocks.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferRoutesAPITest {
    @Rule
    public WireMockRule mockTransfersRule = new WireMockRule(TEST_PORT_TRANSFERS);
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void whenTransfer_thenReturnTransferRs() throws IOException {
        mockTransfersRule.stubFor(post(urlEqualTo(TEST_TRANSFER_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(201)
                        .withBody(new Gson().toJson(mockTransferRs()))));

        HttpPost request = new HttpPost(TEST_TRANSFER_FULL_URL);
        request.setEntity(new ByteArrayEntity(new Gson().toJson(mockTransferRequest()).getBytes(StandardCharsets.UTF_8)));
        HttpResponse httpResponse = httpClient.execute(request);

        TransferRs transferResponse = new Gson().fromJson(convertHttpResponseToString(httpResponse), TransferRs.class);

        assertThat(transferResponse.getStatus()).isEqualTo(StatusEnum.Transfer.COMPLETED.name());
        assertThat(transferResponse.getTransactionId()).isEqualTo(TEST_UUID);
    }

    @Test
    public void whenTransferInvalidAccount_thenReturnErrorRs() throws IOException {
        mockTransfersRule.stubFor(post(urlEqualTo(TEST_TRANSFER_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(ErrorEnum.FROM_TO_ACCOUNT_IS_SAME.getCode())
                        .withBody(new Gson().toJson(mockErrorRsTransferAccount()))));

        HttpPost request = new HttpPost(TEST_TRANSFER_FULL_URL);
        request.setEntity(new ByteArrayEntity(new Gson().toJson(mockTransferRequestInvalidAccount()).getBytes(StandardCharsets.UTF_8)));
        HttpResponse httpResponse = httpClient.execute(request);

        ErrorRs transferResponse = new Gson().fromJson(convertHttpResponseToString(httpResponse), ErrorRs.class);

        assertThat(transferResponse.getMessage()).isEqualTo(ErrorEnum.FROM_TO_ACCOUNT_IS_SAME.getMessage());
        assertThat(transferResponse.getErrorCode()).isEqualTo(ErrorEnum.FROM_TO_ACCOUNT_IS_SAME.getCode());
        assertThat(transferResponse.getType()).isEqualTo(ErrorEnum.FROM_TO_ACCOUNT_IS_SAME.getType());
    }

    @Test
    public void whenTransferInvalidAmount_thenReturnErrorRs() throws IOException {
        mockTransfersRule.stubFor(post(urlEqualTo(TEST_TRANSFER_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(ErrorEnum.TRANSFER_AMOUNT_ZERO_OR_NEGATIVE.getCode())
                        .withBody(new Gson().toJson(mockErrorRsTransferAmount()))));

        HttpPost request = new HttpPost(TEST_TRANSFER_FULL_URL);
        request.setEntity(new ByteArrayEntity(new Gson().toJson(mockTransferRequestInvalidAmount()).getBytes(StandardCharsets.UTF_8)));
        HttpResponse httpResponse = httpClient.execute(request);

        ErrorRs transferResponse = new Gson().fromJson(convertHttpResponseToString(httpResponse), ErrorRs.class);

        assertThat(transferResponse.getMessage()).isEqualTo(ErrorEnum.TRANSFER_AMOUNT_ZERO_OR_NEGATIVE.getMessage());
        assertThat(transferResponse.getErrorCode()).isEqualTo(ErrorEnum.TRANSFER_AMOUNT_ZERO_OR_NEGATIVE.getCode());
        assertThat(transferResponse.getType()).isEqualTo(ErrorEnum.TRANSFER_AMOUNT_ZERO_OR_NEGATIVE.getType());
    }

    @Test
    public void whenTransferInvalidCurrency_thenReturnErrorRs() throws IOException {
        mockTransfersRule.stubFor(post(urlEqualTo(TEST_TRANSFER_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(ErrorEnum.INVALID_CURRENCY.getCode())
                        .withBody(new Gson().toJson(mockErrorRsTransferCurrency()))));

        HttpPost request = new HttpPost(TEST_TRANSFER_FULL_URL);
        request.setEntity(new ByteArrayEntity(new Gson().toJson(mockTransferRequestInvalidCurrency()).getBytes(StandardCharsets.UTF_8)));
        HttpResponse httpResponse = httpClient.execute(request);

        ErrorRs transferResponse = new Gson().fromJson(convertHttpResponseToString(httpResponse), ErrorRs.class);

        assertThat(transferResponse.getMessage()).isEqualTo(ErrorEnum.INVALID_CURRENCY.getMessage());
        assertThat(transferResponse.getErrorCode()).isEqualTo(ErrorEnum.INVALID_CURRENCY.getCode());
        assertThat(transferResponse.getType()).isEqualTo(ErrorEnum.INVALID_CURRENCY.getType());
    }

    @Test
    public void whenTransferClosedAccount_thenReturnErrorRs() throws IOException {
        mockTransfersRule.stubFor(post(urlEqualTo(TEST_TRANSFER_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(ErrorEnum.INVALID_ACCOUNT_STATUS.getCode())
                        .withBody(new Gson().toJson(mockErrorRsTransferClosedAccount()))));

        HttpPost request = new HttpPost(TEST_TRANSFER_FULL_URL);
        request.setEntity(new ByteArrayEntity(new Gson().toJson(mockTransferRequest()).getBytes(StandardCharsets.UTF_8)));
        HttpResponse httpResponse = httpClient.execute(request);

        ErrorRs transferResponse = new Gson().fromJson(convertHttpResponseToString(httpResponse), ErrorRs.class);

        assertThat(transferResponse.getMessage()).isEqualTo(ErrorEnum.INVALID_ACCOUNT_STATUS.getMessage());
        assertThat(transferResponse.getErrorCode()).isEqualTo(ErrorEnum.INVALID_ACCOUNT_STATUS.getCode());
        assertThat(transferResponse.getType()).isEqualTo(ErrorEnum.INVALID_ACCOUNT_STATUS.getType());
    }

    @Test
    public void whenTransferInvalidAccountFormat_thenReturnErrorRs() throws IOException {
        mockTransfersRule.stubFor(post(urlEqualTo(TEST_TRANSFER_URL))
                .willReturn(aResponse()
                        .withHeader("accept", "application/json")
                        .withStatus(ErrorEnum.INVALID_ACCOUNT_FORMAT.getCode())
                        .withBody(new Gson().toJson(mockErrorRsTransferInvalidAccountFormat()))));

        HttpPost request = new HttpPost(TEST_TRANSFER_FULL_URL);
        request.setEntity(new ByteArrayEntity(new Gson().toJson(mockTransferRequestInvalidAccountFormat()).getBytes(StandardCharsets.UTF_8)));
        HttpResponse httpResponse = httpClient.execute(request);

        ErrorRs transferResponse = new Gson().fromJson(convertHttpResponseToString(httpResponse), ErrorRs.class);

        assertThat(transferResponse.getMessage()).isEqualTo(ErrorEnum.INVALID_ACCOUNT_FORMAT.getMessage());
        assertThat(transferResponse.getErrorCode()).isEqualTo(ErrorEnum.INVALID_ACCOUNT_FORMAT.getCode());
        assertThat(transferResponse.getType()).isEqualTo(ErrorEnum.INVALID_ACCOUNT_FORMAT.getType());
    }
}
