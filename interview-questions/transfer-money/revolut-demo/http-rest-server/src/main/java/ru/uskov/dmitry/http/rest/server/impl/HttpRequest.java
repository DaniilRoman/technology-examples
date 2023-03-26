package ru.uskov.dmitry.http.rest.server.impl;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final HttpExchange exchange;
    private final Map<String, String> queryRequests;

    public HttpRequest(@NotNull HttpExchange exchange) {
        this.exchange = exchange;
        this.queryRequests = parseArguments(exchange);
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    public Map<String, String> getQueryRequests() {
        return queryRequests;
    }


    private Map<String, String> parseArguments(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
