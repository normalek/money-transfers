package com.revolut.transfers.config;

import com.revolut.transfers.api.handler.AfterHandler;
import com.revolut.transfers.api.handler.ExceptionHandler;
import com.revolut.transfers.api.routes.AccountRoutes;
import com.revolut.transfers.api.routes.TransferRoutes;

public class APIConfig {
    public APIConfig() throws Exception {
        Class[] classes = {AccountRoutes.class, TransferRoutes.class, AfterHandler.class, ExceptionHandler.class};
        for (Class clazz : classes) {
            clazz.getDeclaredConstructors()[0].newInstance();
        }
    }
}
