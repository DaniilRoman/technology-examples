package ru.uskov.dmitry.http.rest.server.api;

import ru.uskov.dmitry.http.rest.server.impl.HttpRestServerBuilder;

public interface HttpRestServer {

    int DEFAULT_PORT = 8080;
    int DEFAULT_POOL_SIZE = -1;
    String DEFAULT_CONTEXT_PATH = "/";

    static HttpRestServerBuilder newBuilder() {
        return new HttpRestServerBuilder();
    }

    void start();

    void stop();

    void registerController(Object... controllers);

}
