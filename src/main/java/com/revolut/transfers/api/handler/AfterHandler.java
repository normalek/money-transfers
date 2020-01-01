package com.revolut.transfers.api.handler;

import static spark.Spark.after;

public class AfterHandler {
    public AfterHandler() {
        after(((request, response) -> response.header("Content-Type", "application/json")));
    }
}