package ru.uskov.dmitry.transferapp.storage.impl.mem.serializer;

import org.jetbrains.annotations.NotNull;

public interface BytesSerializer {

    byte[] serialize(Object object);

    <T> T deserialize(@NotNull byte[] data);
}
