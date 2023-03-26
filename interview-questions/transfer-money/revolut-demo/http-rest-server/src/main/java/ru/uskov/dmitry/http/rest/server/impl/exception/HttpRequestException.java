package ru.uskov.dmitry.http.rest.server.impl.exception;

public class HttpRequestException extends RuntimeException {

    private final int statusCode;

    public HttpRequestException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpRequestException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
