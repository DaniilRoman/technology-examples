package ru.uskov.dmitry.transferapp.rest;

import ru.uskov.dmitry.http.rest.server.api.HttpRestServer;
import ru.uskov.dmitry.transferapp.config.TransferAppConfig;
import ru.uskov.dmitry.transferapp.rest.controller.AccountManagementController;
import ru.uskov.dmitry.transferapp.rest.controller.HealthcheckController;
import ru.uskov.dmitry.transferapp.rest.controller.TransferController;
import ru.uskov.dmitry.transferapp.services.AccountManagementService;
import ru.uskov.dmitry.transferapp.services.TransferService;

import java.io.IOException;

public class RestTransferHttpServer implements TransferHttpServer {

    private final HttpRestServer server;

    public RestTransferHttpServer(
            TransferService transferService,
            AccountManagementService accountManagementService, TransferAppConfig cfg) throws IOException {

        server = HttpRestServer.newBuilder()
                .setPort(cfg.getServerPort())
                .setExecutorPoolSize(cfg.getServerExecutorPoolSize())
                .setContextPath(cfg.getHttpContextPath())
                .build();
        AccountManagementController accountManagement = new AccountManagementController(accountManagementService);
        TransferController transfer = new TransferController(transferService);
        server.registerController(accountManagement, transfer, new HealthcheckController());
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void stop() {
        server.stop();
    }
}
