package com.revolut.transfers.api.routes;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.revolut.transfers.api.dto.rq.AccountAmountRq;
import com.revolut.transfers.api.dto.rq.AccountRq;
import com.revolut.transfers.api.handler.JSONHandler;
import com.revolut.transfers.service.AccountService;
import org.eclipse.jetty.http.HttpStatus;

import static spark.Spark.*;

public class AccountRoutes {
    @Inject
    private AccountService accountService;

    public AccountRoutes() {
        initializeAccountRoutes();
    }

    public void initializeAccountRoutes() {

        get("/v1/accounts/all", (request, response) -> accountService.getAllAccounts(),
                JSONHandler::toJson);

        get("/v1/account/:id", ((request, response) ->
                        accountService.getAccountById(Integer.valueOf(request.params(":id")))),
                JSONHandler::toJson);

        post("/v1/account/deposit", (request, response) -> {
            AccountAmountRq account = new Gson().fromJson(request.body(), AccountAmountRq.class);
            return accountService.deposit(account);
        }, JSONHandler::toJson);

        post("/v1/account/withdraw", (request, response) -> {
            AccountAmountRq account = new Gson().fromJson(request.body(), AccountAmountRq.class);
            return accountService.withdraw(account);
        }, JSONHandler::toJson);

        post("/v1/account/add", (request, response) -> {
            response.status(HttpStatus.CREATED_201);

            AccountRq account = new Gson().fromJson(request.body(), AccountRq.class);
            return accountService.createAccount(account);
        }, JSONHandler::toJson);

        put("/v1/account/:id/deactivate", (request, response) ->
                accountService.deactivate(Integer.valueOf(request.params(":id"))), JSONHandler::toJson);

        put("/v1/account/:id/activate", (request, response) ->
                accountService.activate(Integer.valueOf(request.params(":id"))), JSONHandler::toJson);
    }
}
