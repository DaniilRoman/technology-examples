package ru.uskov.dmitry.transferapp.storage.interfaces;


import ru.uskov.dmitry.transferapp.exception.LockException;

public interface TransactionManager {

    void lockAccounts(long timeoutMs, long... accountIds) throws LockException;

    void unlockAccounts(long... accountIds);

}
