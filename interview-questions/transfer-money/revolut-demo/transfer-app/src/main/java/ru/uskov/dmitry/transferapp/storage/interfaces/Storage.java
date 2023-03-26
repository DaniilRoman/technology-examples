package ru.uskov.dmitry.transferapp.storage.interfaces;

import ru.uskov.dmitry.transferapp.storage.entity.Account;

public interface Storage extends AutoCloseable {

    void create(Account newAccount);

    void update(Account account);

    Account get(long accountId);

    void block(long accountId);

}
