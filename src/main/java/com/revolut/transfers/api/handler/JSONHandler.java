package com.revolut.transfers.api.handler;

import com.google.gson.Gson;

public class JSONHandler {

    private JSONHandler() {
    }

    public static String toJson(Object o) {
        return new Gson().toJson(o);
    }
}
