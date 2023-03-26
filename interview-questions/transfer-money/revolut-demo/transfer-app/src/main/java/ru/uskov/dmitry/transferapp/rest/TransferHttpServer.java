package ru.uskov.dmitry.transferapp.rest;

import ru.uskov.dmitry.transferapp.utils.Stopped;

public interface TransferHttpServer extends Stopped {
    void start();
}
