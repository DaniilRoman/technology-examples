package ru.uskov.dmitry.transferapp.services;

import ru.uskov.dmitry.transferapp.services.exception.AccountNotFoundException;
import ru.uskov.dmitry.transferapp.storage.entity.Account;
import ru.uskov.dmitry.transferapp.storage.interfaces.Storage;
import ru.uskov.dmitry.transferapp.exception.InvalidTransferArgumentException;
import ru.uskov.dmitry.transferapp.storage.interfaces.TransactionManager;

import java.util.Date;

public class TransferServiceImpl implements TransferService {

    private final Storage storage;
    private final TransactionManager transactionManager;
    private final long lockTimeoutMs;

    public TransferServiceImpl(
        Storage storage,
        TransactionManager transactionManager,
        long lockTimeoutMs) {

        this.storage = storage;
        this.transactionManager = transactionManager;
        this.lockTimeoutMs = lockTimeoutMs;
    }

    @Override
    public void transaction(long srcAccountId, long dstAccountId, long amount) throws InvalidTransferArgumentException, AccountNotFoundException {
        validateAmount(amount);
        if(srcAccountId == dstAccountId) {
            throw new InvalidTransferArgumentException("srcAccountId and dstAccountId must be different");
        }
        transactionManager.lockAccounts(lockTimeoutMs, srcAccountId, dstAccountId);
        try {
            Account srcAccount = storage.get(srcAccountId);
            if (srcAccount == null) {
                throw new AccountNotFoundException(srcAccountId);
            }
            Account dstAccount = storage.get(dstAccountId);
            if (dstAccount == null) {
                throw new AccountNotFoundException(dstAccountId);
            }
            long newSrcAmount = srcAccount.getAmount() - amount;
            if (newSrcAmount < amount) {
                throw new InvalidTransferArgumentException("Not enough money for transaction");
            }
            long newDstAmount = srcAccount.getAmount() + amount;
            if (newDstAmount < 0) {
                throw new InvalidTransferArgumentException("Could not execute transaction. Amount limit is exceeded");
            }
            Date currentDate = new Date();
            srcAccount.setAmount(newSrcAmount);
            dstAccount.setAmount(newDstAmount);
            srcAccount.setLastUpdate(currentDate);
            dstAccount.setLastUpdate(currentDate);
            storage.update(srcAccount);
            // a hope node doesn't failed at this moment :)
            // (really I need create transaction execution confirmation or something else in real life)
            storage.update(dstAccount);
        } finally {
            transactionManager.unlockAccounts(srcAccountId, dstAccountId);
        }
    }

    @Override
    public long replenish(long accountId, long amount) throws AccountNotFoundException, InvalidTransferArgumentException {
        validateAmount(amount);
        transactionManager.lockAccounts(lockTimeoutMs, accountId);
        try {
            Account account = storage.get(accountId);
            if (account == null) {
                throw new AccountNotFoundException(accountId);
            }
            long newAmount = account.getAmount() + amount;
            if (newAmount < 0) {
                throw new InvalidTransferArgumentException("Could not replenish account. Amount limit is exceeded");
            }
            account.setAmount(newAmount);
            account.setLastUpdate(new Date());
            storage.update(account);
            return newAmount;
        } finally {
            transactionManager.unlockAccounts(accountId);
        }
    }

    @Override
    public long withdrawing(long accountId, long amount) throws InvalidTransferArgumentException, AccountNotFoundException {
        validateAmount(amount);
        transactionManager.lockAccounts(lockTimeoutMs, accountId);
        try {
            Account account = storage.get(accountId);
            if (account == null) {
                throw new AccountNotFoundException(accountId);
            }
            long newAmount = account.getAmount() - amount;
            if (newAmount < 0) {
                throw new InvalidTransferArgumentException("Invalid amount. Current amount value: " + account.getAmount());
            }
            account.setAmount(newAmount);
            account.setLastUpdate(new Date());
            storage.update(account);
            return newAmount;
        } finally {
            transactionManager.unlockAccounts(accountId);
        }
    }

    private void validateAmount(long amount) throws InvalidTransferArgumentException {
        if (amount <= 0) {
            throw new InvalidTransferArgumentException("Invalid amount value " + amount + ". Amount must be above zero");
        }
    }

}
