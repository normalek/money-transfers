package com.revolut.transfers.config;

import com.google.inject.AbstractModule;
import com.revolut.transfers.dao.AccountDAO;
import com.revolut.transfers.dao.TransfersDAO;
import com.revolut.transfers.dao.impl.AccountDAOImpl;
import com.revolut.transfers.dao.impl.TransfersDAOImpl;
import com.revolut.transfers.service.AccountService;
import com.revolut.transfers.service.TransferService;
import com.revolut.transfers.service.impl.AccountServiceImpl;
import com.revolut.transfers.service.impl.TransferServiceImpl;
import com.revolut.transfers.validator.AccountValidator;

public class GuiceConfig extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountService.class).to(AccountServiceImpl.class);
        bind(TransferService.class).to(TransferServiceImpl.class);
        bind(TransfersDAO.class).to(TransfersDAOImpl.class);
        bind(AccountDAO.class).to(AccountDAOImpl.class);
        bind(AccountValidator.class);
    }
}
