package com.revolut.transfers.api.routes;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.revolut.transfers.api.dto.rq.TransferRq;
import com.revolut.transfers.api.handler.JSONHandler;
import com.revolut.transfers.service.TransferService;

import java.util.UUID;

import static spark.Spark.get;
import static spark.Spark.post;

public class TransferRoutes {
    @Inject
    private TransferService transferService;

    public TransferRoutes() {
        initializeTransferRoutes();
    }

    public void initializeTransferRoutes() {

        post("/v1/transfer", ((request, response) -> {
            TransferRq transferRequest = new Gson().fromJson(request.body(), TransferRq.class);
            return transferService.transfer(transferRequest);
        }), JSONHandler::toJson);

        get("/v1/transfer/:id", ((request, response) ->
                        transferService.findTransferByTransId(UUID.fromString(request.params(":id")))),
                JSONHandler::toJson);
    }
}
