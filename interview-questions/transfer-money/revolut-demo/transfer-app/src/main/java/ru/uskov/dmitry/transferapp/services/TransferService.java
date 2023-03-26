package ru.uskov.dmitry.transferapp.services;

import ru.uskov.dmitry.transferapp.exception.InvalidTransferArgumentException;
import ru.uskov.dmitry.transferapp.services.exception.AccountNotFoundException;

public interface TransferService {

    void transaction(long srcAccountId, long dstAccountId, long amount) throws InvalidTransferArgumentException, AccountNotFoundException;

    long replenish(long accountId, long amount) throws InvalidTransferArgumentException, AccountNotFoundException;

    long withdrawing(long accountId, long amount) throws InvalidTransferArgumentException, AccountNotFoundException;

}
