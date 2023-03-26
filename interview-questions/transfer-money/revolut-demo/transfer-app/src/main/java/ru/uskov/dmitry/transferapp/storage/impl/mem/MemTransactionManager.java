package ru.uskov.dmitry.transferapp.storage.impl.mem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uskov.dmitry.transferapp.exception.LockException;
import ru.uskov.dmitry.transferapp.storage.interfaces.TransactionManager;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MemTransactionManager implements TransactionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemTransactionManager.class);

    private final ConcurrentHashMap<Long, Lock> locks = new ConcurrentHashMap<>();

    @Override
    public void lockAccounts(long timeoutMs, long... accountIds) throws LockException {
        validate(accountIds);
        Arrays.sort(accountIds);
        for (int i = 0; i < accountIds.length; i++) {
            try {
                boolean success = locks.computeIfAbsent(accountIds[i], k -> new ReentrantLock()).tryLock(timeoutMs, TimeUnit.MILLISECONDS);
                if (!success) {
                    unlockAccounts(i, accountIds);
                    throw new LockException("Could not get lock in " + timeoutMs + " ms");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new LockException(e);
            }
        }
    }

    @Override
    public void unlockAccounts(long... accountIds) {
        validate(accountIds);
        Arrays.sort(accountIds);
        unlockAccounts(accountIds.length, accountIds);
    }

    private void unlockAccounts(int size, long... accountIds) {
        for (int i = 0; i < size; i++) {
            Lock lock = locks.get(accountIds[i]);
            if (lock == null) {
                LOGGER.error("Could not found lock for account '{}'", accountIds[i]);
            } else {
                lock.unlock();
            }
        }
    }

    private void validate(long[] accountIds) {
        if (accountIds == null) {
            throw new IllegalArgumentException("accountIds mustn't be null");
        }
    }

}
