package ru.uskov.dmitry.transferapp.exception;

public class TransferAppException extends RuntimeException {
    public TransferAppException(String message) {
        super(message);
    }

    public TransferAppException(String message, Throwable cause) {
        super(message, cause);
    }
}
