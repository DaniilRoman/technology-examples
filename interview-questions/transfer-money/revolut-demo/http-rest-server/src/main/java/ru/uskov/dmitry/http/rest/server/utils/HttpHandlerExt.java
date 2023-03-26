package ru.uskov.dmitry.http.rest.server.utils;

import com.sun.net.httpserver.HttpExchange;

public interface HttpHandlerExt {
    void handle (HttpExchange exchange) throws Exception;
}
