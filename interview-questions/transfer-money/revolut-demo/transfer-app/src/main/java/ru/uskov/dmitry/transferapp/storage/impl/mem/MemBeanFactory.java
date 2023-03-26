package ru.uskov.dmitry.transferapp.storage.impl.mem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uskov.dmitry.transferapp.storage.impl.mem.serializer.BytesSerializer;
import ru.uskov.dmitry.transferapp.storage.interfaces.Storage;
import ru.uskov.dmitry.transferapp.storage.interfaces.BeanFactory;
import ru.uskov.dmitry.transferapp.storage.interfaces.IdGenerator;
import ru.uskov.dmitry.transferapp.storage.interfaces.TransactionManager;
import ru.uskov.dmitry.transferapp.exception.StorageInitialisationException;

public class MemBeanFactory implements BeanFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemTransactionManager.class);

    private final BytesSerializer serializer;

    public MemBeanFactory() {
        this.serializer = new ThreadSafeKryoSerializer();
    }

    @Override
    public Storage createStorage() throws StorageInitialisationException {
        try {
            return new MemStorage(serializer);
        } catch (Exception e) {
            LOGGER.error("Could not create In-memory storage: {}", e.getMessage(), e);
            throw new StorageInitialisationException(e);
        }
    }

    @Override
    public IdGenerator createIdGenerator() {
        return new MemIdGenerator();
    }

    @Override
    public TransactionManager createTransactionManager() {
        return new MemTransactionManager();
    }
}
