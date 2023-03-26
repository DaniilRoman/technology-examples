package ru.uskov.dmitry.transferapp.services.exception;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(long accountId) {
        super("Account with id '" + accountId + "' not found");
    }
}
