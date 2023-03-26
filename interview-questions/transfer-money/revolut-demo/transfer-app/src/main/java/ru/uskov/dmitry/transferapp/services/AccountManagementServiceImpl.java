package ru.uskov.dmitry.transferapp.services;

import ru.uskov.dmitry.transferapp.services.exception.AccountNotFoundException;
import ru.uskov.dmitry.transferapp.storage.entity.Account;
import ru.uskov.dmitry.transferapp.storage.interfaces.Storage;
import ru.uskov.dmitry.transferapp.storage.interfaces.IdGenerator;

public class AccountManagementServiceImpl implements AccountManagementService {

    private final Storage storage;
    private final IdGenerator idGenerator;

    public AccountManagementServiceImpl(Storage storage, IdGenerator idGenerator) {
        this.storage = storage;
        this.idGenerator = idGenerator;
    }

    @Override
    public Account createAccount(String description) {
        long accountId = idGenerator.generateUniqueId();
        Account account = Account.createNew(accountId, description);
        storage.create(account);
        return account;
    }

    @Override
    public void blockAccount(long accountId) throws AccountNotFoundException {
        if (storage.get(accountId) == null) {
            throw new AccountNotFoundException(accountId);
        }
        storage.block(accountId);
    }

    @Override
    public Account getAccount(long accountId) throws AccountNotFoundException {
        Account account = storage.get(accountId);
        if(account == null) {
            throw new AccountNotFoundException(accountId);
        }
        return account;
    }
}
