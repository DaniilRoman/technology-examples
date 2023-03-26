package ru.uskov.dmitry.transferapp;

import org.slf4j.LoggerFactory;
import ru.uskov.dmitry.transferapp.config.TransferAppConfig;

import java.util.concurrent.CountDownLatch;

public class CommandLineStartup {

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        TransferAppConfig cfg = new TransferAppConfig();
        String port = System.getProperties().getProperty("transfer.app.server.port");
        String contextPath = System.getProperties().getProperty("transfer.app.server.contextPath");
        String poolSize = System.getProperties().getProperty("transfer.app.server.executor-pool-size");
        String waitTransactionMs = System.getProperties().getProperty("transfer.app.wait-transaction-ms");
        if (port != null && !port.trim().isEmpty()) {
            cfg.serverPort(Integer.parseInt(port));
        }
        if (poolSize != null && !poolSize.trim().isEmpty()) {
            cfg.serverExecutorPoolSize(Integer.parseInt(poolSize));
        }
        if (contextPath != null && !contextPath.trim().isEmpty()) {
            cfg.httpContextPath(contextPath);
        }
        if (waitTransactionMs != null && !waitTransactionMs.trim().isEmpty()) {
            cfg.waitTransactionMs(Long.valueOf(waitTransactionMs));
        }

        TransferApp transferApp = new TransferApp(cfg);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LoggerFactory.getLogger(TransferApp.class).info("Shutdown TransferApp");
            transferApp.stop();
            latch.countDown();
        }));
        transferApp.start();
        wait(latch);
    }

    private static void wait(CountDownLatch latch) {
        boolean exit = false;
        while (!exit) {
            try {
                latch.await();
                exit = true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
