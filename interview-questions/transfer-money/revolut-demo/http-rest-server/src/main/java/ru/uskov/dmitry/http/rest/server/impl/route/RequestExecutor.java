package ru.uskov.dmitry.http.rest.server.impl.route;

import com.sun.net.httpserver.HttpExchange;

public interface RequestExecutor {
    void execute(HttpExchange exchange) throws Exception;
}
