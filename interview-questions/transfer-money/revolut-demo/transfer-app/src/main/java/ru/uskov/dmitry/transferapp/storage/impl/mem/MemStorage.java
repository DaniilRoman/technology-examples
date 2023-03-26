package ru.uskov.dmitry.transferapp.storage.impl.mem;

import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import ru.uskov.dmitry.transferapp.storage.entity.Account;
import ru.uskov.dmitry.transferapp.storage.impl.mem.serializer.BytesSerializer;
import ru.uskov.dmitry.transferapp.storage.interfaces.Storage;

public class MemStorage implements Storage {

    private final HTreeMap<Long, Account> accounts;

    public MemStorage(BytesSerializer byteSerializer) {
        MapDbSerializer serializer = new MapDbSerializer(byteSerializer);
        accounts = DBMaker.memoryDB().make().hashMap("accounts", serializer, serializer).createOrOpen();
    }

    @Override
    public void create(Account newAccount) {
        accounts.put(newAccount.getId(), newAccount);
    }

    @Override
    public void update(Account account) {
        accounts.computeIfPresent(account.getId(), (id, ac) -> account);
    }

    @Override
    public Account get(long accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void block(long accountId) {
        accounts.computeIfPresent(accountId, (id, account) -> {
            Account newAcc = new Account(account);
            newAcc.setBlocked(true);
            return newAcc;
        });
    }

    @Override
    public void close() {
        accounts.clear();
        accounts.close();
    }
}
