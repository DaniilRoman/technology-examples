package ru.uskov.dmitry.http.rest.server.impl.marshaller;

public class MarshallerException extends Exception {
    public MarshallerException(Throwable cause) {
        super(cause);
    }

    public MarshallerException(String s) {
        super(s);
    }
}
