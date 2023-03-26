package ru.uskov.dmitry.transferapp.storage.impl.mem;

import ru.uskov.dmitry.transferapp.storage.interfaces.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

public class MemIdGenerator implements IdGenerator {

    private final AtomicLong inc = new AtomicLong();

    @Override
    public long generateUniqueId() {
        return inc.getAndIncrement();
    }
}
