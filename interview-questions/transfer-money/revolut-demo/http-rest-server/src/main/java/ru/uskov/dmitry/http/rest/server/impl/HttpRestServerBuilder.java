package ru.uskov.dmitry.http.rest.server.impl;

import ru.uskov.dmitry.http.rest.server.api.HttpRestServer;
import ru.uskov.dmitry.http.rest.server.utils.UrlUtils;

import java.io.IOException;

public class HttpRestServerBuilder {

    private int port = HttpRestServer.DEFAULT_PORT;

    private int poolSize = HttpRestServer.DEFAULT_POOL_SIZE;

    private String contextPath = HttpRestServer.DEFAULT_CONTEXT_PATH;

    public HttpRestServerBuilder() {
    }

    public HttpRestServerBuilder setPort(int port) {
        if (port < 0 || port >= 65535) {
            throw new IllegalArgumentException("Port value must be >= 0 and <=65535");
        }
        this.port = port;
        return this;
    }

    public HttpRestServerBuilder setExecutorPoolSize(int poolSize) {
        this.poolSize = poolSize;
        return this;
    }

    public HttpRestServerBuilder setContextPath(String contextPath) {
        if(!UrlUtils.validatePath(contextPath)) {
            throw new IllegalArgumentException("context path contains illegal symbols");
        }
        this.contextPath = contextPath;
        return this;
    }

    public HttpRestServer build() throws IOException {
        return new HttpRestServerImpl(this);
    }

    public int getPort() {
        return port;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public String getContextPath() {
        return contextPath;
    }
}