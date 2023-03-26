package ru.uskov.dmitry.transferapp.exception;

public class LockException extends RuntimeException {

    public LockException(String message) {
        super(message);
    }

    public LockException(Throwable cause) {
        super(cause);
    }
}
