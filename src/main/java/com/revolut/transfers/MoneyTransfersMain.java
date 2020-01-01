package com.revolut.transfers;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.transfers.api.routes.AccountRoutes;
import com.revolut.transfers.api.routes.TransferRoutes;
import com.revolut.transfers.config.APIConfig;
import com.revolut.transfers.config.GuiceConfig;
import com.revolut.transfers.service.impl.AccountServiceImpl;
import com.revolut.transfers.service.impl.TransferServiceImpl;
import com.revolut.transfers.validator.AccountValidator;

public class MoneyTransfersMain {

    public static void main(String[] args) throws Exception {
        initializeInjectors();

        //Initialize API Configuration
        new APIConfig();
    }

    private static void initializeInjectors() {
        //Initialize DI modules
        Injector injector = Guice.createInjector(new GuiceConfig());
        injector.getInstance(AccountRoutes.class);
        injector.getInstance(AccountServiceImpl.class);
        injector.getInstance(TransferRoutes.class);
        injector.getInstance(TransferServiceImpl.class);
        injector.getInstance(AccountValidator.class);
    }
}
