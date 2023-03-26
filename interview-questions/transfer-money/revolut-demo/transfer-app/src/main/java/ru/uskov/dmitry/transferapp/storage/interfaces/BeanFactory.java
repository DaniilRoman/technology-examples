package ru.uskov.dmitry.transferapp.storage.interfaces;

import ru.uskov.dmitry.transferapp.exception.StorageInitialisationException;

public interface BeanFactory {

    Storage createStorage() throws StorageInitialisationException;

    IdGenerator createIdGenerator();

    TransactionManager createTransactionManager();

}
