package ru.uskov.dmitry.transferapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uskov.dmitry.transferapp.config.TransferAppConfig;
import ru.uskov.dmitry.transferapp.rest.RestTransferHttpServer;
import ru.uskov.dmitry.transferapp.rest.TransferHttpServer;
import ru.uskov.dmitry.transferapp.services.AccountManagementServiceImpl;
import ru.uskov.dmitry.transferapp.services.TransferServiceImpl;
import ru.uskov.dmitry.transferapp.storage.interfaces.BeanFactory;
import ru.uskov.dmitry.transferapp.exception.TransferAppException;
import ru.uskov.dmitry.transferapp.storage.impl.mem.MemBeanFactory;
import ru.uskov.dmitry.transferapp.storage.interfaces.Storage;
import ru.uskov.dmitry.transferapp.utils.Stopped;

public class TransferApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferApp.class);

    private final BeanFactory beanFactory;

    private TransferHttpServer server;
    private Storage storage;

    private boolean started = false;
    private final TransferAppConfig cfg;

    public TransferApp() {
        this(new TransferAppConfig());
    }

    public TransferApp(TransferAppConfig transferAppConfig) {
        this.cfg = transferAppConfig;
        beanFactory = new MemBeanFactory();
    }

    public synchronized void start() {
        if (started) {
            throw new TransferAppException("TransferApp instance already started");
        }
        try {
            LOGGER.info("Create Storage");
            storage = beanFactory.createStorage();
            TransferServiceImpl transferService = new TransferServiceImpl(storage, beanFactory.createTransactionManager(), cfg.getWaitTransactionMs());
            AccountManagementServiceImpl accountManagementService = new AccountManagementServiceImpl(storage, beanFactory.createIdGenerator());
            LOGGER.info("Create Http Server");
            server = new RestTransferHttpServer(transferService, accountManagementService, cfg);
            server.start();
            LOGGER.info("TransferApp has been started");
            started = true;
        } catch (Exception e) {
            stop();
            throw new TransferAppException("Could not start TransferApp", e);
        }
    }

    public synchronized void stop() {
        stopService("Http Server", server);
        stopService("Storage", null);
        LOGGER.info("TransferApp has been stopped");
        started = false;
    }

    private void stopService(String serviceName, Stopped bean) {
        try {
            if (bean != null) {
                LOGGER.info("Stop '{}'", serviceName);
                bean.stop();
            }
        } catch (Exception e) {
            LOGGER.error("Could not stop '{}' correctly: {}", serviceName, e.getMessage(), e);
        }
    }
}
