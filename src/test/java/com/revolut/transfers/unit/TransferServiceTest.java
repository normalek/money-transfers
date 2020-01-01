package com.revolut.transfers.unit;

import com.revolut.transfers.api.dto.rs.TransferRs;
import com.revolut.transfers.dao.TransfersDAO;
import com.revolut.transfers.exception.DataValidationException;
import com.revolut.transfers.exception.account.AccountNotFoundException;
import com.revolut.transfers.service.impl.TransferServiceImpl;
import com.revolut.transfers.util.ErrorEnum;
import com.revolut.transfers.util.UnitMocks;
import com.revolut.transfers.validator.TransferValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.revolut.transfers.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransferServiceTest {
    @Mock
    private TransfersDAO transfersDAO;
    @Mock
    private TransferValidator validator;

    @InjectMocks
    private TransferServiceImpl transferService;

    @Test
    public void whenTransfer_thenReturnTransferRs() {
        when(transfersDAO.transfer(TEST_ACCOUNT_ID, TEST_ANOTHER_ACCOUNT_ID,
                TEST_AMOUNT, TEST_CURRENCY_USD))
                .thenReturn(UnitMocks.mockTransferRes());

        TransferRs response = transferService.transfer(UnitMocks.mockTransferReq());

        assertThat(response.getTransactionId()).isEqualTo(TEST_UUID);
    }

    @Test
    public void whenFindTransactionByTransId_thenReturnTransferRs() {
        when(transfersDAO.findTransferById(TEST_UUID))
                .thenReturn(UnitMocks.mockTransferRes());
        TransferRs response = transferService.findTransferByTransId(TEST_UUID);

        assertThat(response.getTransactionId()).isEqualTo(TEST_UUID);
        assertThat(response.getStatus()).isEqualTo(TEST_STATUS_SUCCESS);
    }

    @Test(expected = AccountNotFoundException.class)
    public void whenTransferWithAbsentAccount_thenThrowAccountNotFoundException() {
        when(transfersDAO.transfer(TEST_ACCOUNT_ID, TEST_ANOTHER_ACCOUNT_ID,
                TEST_AMOUNT, TEST_CURRENCY_USD)).thenThrow(new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_FOUND));
        transferService.transfer(UnitMocks.mockTransferReq());
    }

    @Test(expected = DataValidationException.class)
    public void whenTransferWithClosedAccount_thenThrowDataValidationException() {
        when(transfersDAO.transfer(TEST_ACCOUNT_ID, TEST_ANOTHER_ACCOUNT_ID,
                TEST_AMOUNT, TEST_CURRENCY_USD)).thenThrow(new DataValidationException(ErrorEnum.INVALID_ACCOUNT_STATUS));
        transferService.transfer(UnitMocks.mockTransferReq());
    }
}
