package com.revolut.transfers.dao.impl;

import com.google.inject.Inject;
import com.revolut.transfers.config.JDBIConfig;
import com.revolut.transfers.dao.AccountDAO;
import com.revolut.transfers.dao.TransfersDAO;
import com.revolut.transfers.dao.repository.AccountRepository;
import com.revolut.transfers.dao.repository.TransferRepository;
import com.revolut.transfers.entity.Account;
import com.revolut.transfers.entity.Transfer;
import com.revolut.transfers.exception.TransactionException;
import com.revolut.transfers.exception.account.AccountNotFoundException;
import com.revolut.transfers.exception.account.AmountOverdrawnException;
import com.revolut.transfers.util.ErrorEnum;
import com.revolut.transfers.util.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TransfersDAOImpl implements TransfersDAO {
    private ConcurrentHashMap<String, Lock> accounts = new ConcurrentHashMap<>();
    private Jdbi jdbi = JDBIConfig.getJdbi();
    private final AccountDAO accountDAO;

    @Inject
    public TransfersDAOImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public boolean deposit(Integer id, BigDecimal amount) {
        String key = jdbi.withExtension(AccountRepository.class,
                dao -> dao.getById(id)
                        .orElseThrow(() -> new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_FOUND)))
                .getId().toString().intern();
        Lock lock = accounts.computeIfAbsent(key, l -> new ReentrantLock());
        try {
            lock.lock();
            // Retrieve information about account once more
            Account account = jdbi.withExtension(AccountRepository.class,
                    dao -> dao.getById(id)
                            .orElseThrow(() -> new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_FOUND)));
            return jdbi.withExtension(AccountRepository.class, repository -> {
                BigDecimal toAcctNewBal = account.getBalance().add(amount);
                boolean status = repository.updateAccount(id, toAcctNewBal);
                log.info("Deposit of {} is successful to account {}. New Balance : {}",
                        amount, id, toAcctNewBal);
                return status;
            });
        } catch (Exception e) {
            log.error("Error while proceeding with deposit. account : {}", id);
            throw new TransactionException(ErrorEnum.TRANSACTION_EXCEPTION, e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean withdraw(Integer id, BigDecimal amount) {
        String key = jdbi.withExtension(AccountRepository.class,
                dao -> dao.getById(id)
                        .orElseThrow(() -> new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_FOUND)))
                .getId().toString().intern();
        Lock lock = accounts.computeIfAbsent(key, l -> new ReentrantLock());
        try {
            lock.lock();
            // Retrieve information about account once more
            Account account = jdbi.withExtension(AccountRepository.class,
                    dao -> dao.getById(id)
                            .orElseThrow(() -> new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_FOUND)));
            return jdbi.withExtension(AccountRepository.class, repository -> {
                BigDecimal toAcctNewBal = account.getBalance().subtract(amount);
                if (toAcctNewBal.compareTo(BigDecimal.ZERO) < 0) {
                    throw new AmountOverdrawnException(ErrorEnum.AMOUNT_OVERDRAWN);
                }
                boolean status = repository.updateAccount(id, toAcctNewBal);
                log.info("Withdraw of {} is successful to account {}. New Balance : {}",
                        amount, id, toAcctNewBal);
                return status;
            });
        } catch (Exception e) {
            log.error("Error while proceeding with withdraw. account : {}", id +
                    ". Exception: " + e.getMessage());
            throw new TransactionException(ErrorEnum.TRANSACTION_EXCEPTION, e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Transfer transfer(Integer fromAccount, Integer toAccount, BigDecimal amount, String currency) {
        String keyFrom = jdbi.withExtension(AccountRepository.class,
                dao -> dao.getById(fromAccount)
                        .orElseThrow(() -> new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_FOUND)))
                .getId().toString().intern();
        Lock lockFrom = accounts.computeIfAbsent(keyFrom, l -> new ReentrantLock());

        String keyTo = jdbi.withExtension(AccountRepository.class,
                dao -> dao.getById(toAccount)
                        .orElseThrow(() -> new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_FOUND)))
                .getId().toString().intern();
        Lock lockTo = accounts.computeIfAbsent(keyTo, l -> new ReentrantLock());

        try {
            return jdbi.inTransaction(handle -> {
                // Subtract from Account 1, throw exception is amount > balance
                AccountRepository accountRepository = handle.attach(AccountRepository.class);

                lockFrom.lock();
//                Account accountFrom = jdbi.withExtension(AccountRepository.class,
//                        dao -> dao.getById(fromAccount)
//                                .orElseThrow(() -> new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_FOUND)))

                BigDecimal balFrom = accountDAO.getBalanceById(fromAccount).subtract(amount);
                if (balFrom.compareTo(BigDecimal.ZERO) < 0) {
                    throw new AmountOverdrawnException(ErrorEnum.AMOUNT_OVERDRAWN);
                }
                accountRepository.updateAccount(fromAccount, balFrom);

                lockTo.lock();

                // Add in Account 2
                BigDecimal balTo = accountDAO.getBalanceById(toAccount).add(amount);
                accountRepository.updateAccount(toAccount, balTo);

                // Insert the record in Transfer table as Success
                TransferRepository transferRepository = handle.attach(TransferRepository.class);

                UUID transId = transferRepository.insert(fromAccount,
                        toAccount, amount, currency, StatusEnum.Transfer.COMPLETED.name());
                log.info("Transfer successful. amount {} has been transferred from {} to {} account." +
                                "From account balance : {}, To account balance : {}",
                        amount, fromAccount, toAccount, balFrom, balTo);
                return Transfer.builder()
                        .transDate(LocalDateTime.now())
                        .transId(transId)
                        .status(StatusEnum.Transfer.COMPLETED.name()).build();
            });
        } catch (AmountOverdrawnException e) {
            log.info("Transfer amount exceeded from account balance. amount {} cannot be transfer from " +
                    "  {} to {} account.", amount, fromAccount, toAccount);
            throw e;
        } catch (Exception e) {
            log.info("Transfer has been failed due to exception. amount {} hasn't been transferred " +
                            "  from {} to {} account. Exception is : " + e.getMessage(),
                    amount, fromAccount, toAccount);
            jdbi.withExtension(TransferRepository.class, repository -> repository.insert(fromAccount, toAccount, amount,
                    currency, StatusEnum.Transfer.FAILED.name()));
            throw e;
        } finally {
            lockFrom.unlock();
            lockTo.unlock();
        }
    }

    @Override
    public Transfer findTransferById(UUID transId) {
        return jdbi.withExtension(TransferRepository.class, dao -> dao.getTransferByTransId(transId));
    }
}
