package com.revolut.transfers.unit;

import com.revolut.transfers.api.dto.rs.AccountPartialRs;
import com.revolut.transfers.api.dto.rs.AccountRs;
import com.revolut.transfers.api.dto.rs.AccountRsList;
import com.revolut.transfers.dao.AccountDAO;
import com.revolut.transfers.dao.TransfersDAO;
import com.revolut.transfers.exception.account.AccountNotFoundException;
import com.revolut.transfers.exception.account.AmountOverdrawnException;
import com.revolut.transfers.service.impl.AccountServiceImpl;
import com.revolut.transfers.util.ErrorEnum;
import com.revolut.transfers.util.UnitMocks;
import com.revolut.transfers.validator.AccountValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.revolut.transfers.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @Mock
    private AccountDAO accountDAO;
    @Mock
    private TransfersDAO transfersDAO;
    @Mock
    private AccountValidator validator;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @Test
    public void whenCreateAccount_thenReturnAccountPartialRs() {
        when(accountDAO.createAccount(anyString(), anyString(), any(), anyString()))
                .thenReturn(TEST_ACCOUNT_ID);
        AccountPartialRs response = accountServiceImpl.createAccount(UnitMocks.mockAccountNewRequest());

        assertThat(response.getAccountNumber()).isEqualTo(TEST_ACCOUNT_ID);
        assertThat(response.getStatus()).isEqualTo(TEST_ACCOUNT_ACTIVE_STATUS);
    }

    @Test
    public void whenFindAccountById_thenReturnAccountRs() {
        when(accountDAO.getById(TEST_ACCOUNT_ID)).thenReturn(UnitMocks.mockOneAccount());
        AccountRs accountResponse = accountServiceImpl.getAccountById(TEST_ACCOUNT_ID);

        assertThat(accountResponse.getAccountNumber()).isEqualTo(TEST_ACCOUNT_ID);
    }

    @Test
    public void whenActivateAccount_thenReturnAccountPartialRs() {
        when(accountDAO.activateAccount(TEST_ACCOUNT_ID)).thenReturn(true);
        AccountPartialRs accountResponse = accountServiceImpl.activate(TEST_ACCOUNT_ID);

        assertThat(accountResponse.getAccountNumber()).isEqualTo(TEST_ACCOUNT_ID);
    }

    @Test
    public void whenDeactivateAccount_thenReturnAccountPartialRs() {
        when(accountDAO.deactivateAccount(TEST_ACCOUNT_ID)).thenReturn(true);
        AccountPartialRs accountResponse = accountServiceImpl.deactivate(TEST_ACCOUNT_ID);

        assertThat(accountResponse.getAccountNumber()).isEqualTo(TEST_ACCOUNT_ID);
    }

    @Test
    public void whenFindAllAccounts_thenReturnListAccountRs() {
        when(accountDAO.getAll()).thenReturn(UnitMocks.mockAccountList());
        AccountRsList accountResponseList = accountServiceImpl.getAllAccounts();

        assertThat(accountResponseList.getAccounts().size()).isEqualTo(2);
        assertThat(accountResponseList.getAccounts().get(0).getAccountNumber()).isEqualTo(TEST_ACCOUNT_ID);
    }

    @Test(expected = AccountNotFoundException.class)
    public void whenCannotFindAccountById_thenThrowAccountNotFoundException() {
        when(accountDAO.getById(TEST_ACCOUNT_ID)).thenThrow(new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_FOUND));
        accountServiceImpl.getAccountById(TEST_ACCOUNT_ID);
    }

    @Test
    public void whenDeposit_thenReturnAccountPartialRs() {
        when(transfersDAO.deposit(TEST_ACCOUNT_ID, TEST_AMOUNT)).thenReturn(true);
        AccountPartialRs response = accountServiceImpl.deposit(UnitMocks.mockAccountTransRequest());

        assertThat(response.getStatus()).isEqualTo(TEST_STATUS_SUCCESS);
        assertThat(response.getAccountNumber()).isEqualTo(TEST_ACCOUNT_ID);
    }

    @Test
    public void whenWithdraw_thenReturnAccountPartialRs() {
        when(transfersDAO.withdraw(TEST_ACCOUNT_ID, TEST_AMOUNT)).thenReturn(true);
        AccountPartialRs response = accountServiceImpl.withdraw(UnitMocks.mockAccountTransRequest());

        assertThat(response.getStatus()).isEqualTo(TEST_STATUS_SUCCESS);
        assertThat(response.getAccountNumber()).isEqualTo(TEST_ACCOUNT_ID);
    }

    @Test(expected = AmountOverdrawnException.class)
    public void whenWithdrawTooMuch_thenThrowAmountOverdrawnException() {
        when(transfersDAO.withdraw(TEST_ACCOUNT_ID, TEST_AMOUNT)).thenThrow(new AmountOverdrawnException(ErrorEnum.AMOUNT_OVERDRAWN));
        accountServiceImpl.withdraw(UnitMocks.mockAccountTransRequest());
    }
}
