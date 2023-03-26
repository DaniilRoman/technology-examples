package ru.uskov.dmitry.http.rest.server.impl.exception;

public class InvocationException extends RuntimeException {
    public InvocationException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
