package ru.uskov.dmitry.transferapp.config;

public class TransferAppConfig {

    public static final int DEFAULT_SERVER_PORT = 8080;
    public static final int DEFAULT_POOL_SIZE = -1;
    public static final int DEFAULT_WAIT_TRANSACTION_MS = 5000;
    public static final String DEFAULT_SERVER_HTTP_CONTEXT = "/";


    private int serverPort = DEFAULT_SERVER_PORT;
    private int serverExecutorPoolSize = DEFAULT_POOL_SIZE;
    private long waitTransactionMs = DEFAULT_WAIT_TRANSACTION_MS;
    private String httpContextPath = DEFAULT_SERVER_HTTP_CONTEXT;

    public int getServerPort() {
        return serverPort;
    }

    public TransferAppConfig serverPort(int port) {
        this.serverPort = port;
        return this;
    }

    public long getWaitTransactionMs() {
        return waitTransactionMs;
    }

    public TransferAppConfig waitTransactionMs(long waitTransactionMs) {
        this.waitTransactionMs = waitTransactionMs;
        return this;
    }

    public String getHttpContextPath() {
        return httpContextPath;
    }

    public TransferAppConfig httpContextPath(String httpContextPath) {
        this.httpContextPath = httpContextPath;
        return this;
    }

    public int getServerExecutorPoolSize() {
        return serverExecutorPoolSize;
    }

    public TransferAppConfig serverExecutorPoolSize(int serverExecutorPoolSize) {
        this.serverExecutorPoolSize = serverExecutorPoolSize;
        return this;
    }
}
