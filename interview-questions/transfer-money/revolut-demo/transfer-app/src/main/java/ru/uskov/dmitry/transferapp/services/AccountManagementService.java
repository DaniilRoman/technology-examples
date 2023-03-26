package ru.uskov.dmitry.transferapp.services;

import ru.uskov.dmitry.transferapp.services.exception.AccountNotFoundException;
import ru.uskov.dmitry.transferapp.storage.entity.Account;

public interface AccountManagementService {

    Account createAccount(String description);

    void blockAccount(long accountId) throws AccountNotFoundException;

    Account getAccount(long accountId) throws AccountNotFoundException;
}
