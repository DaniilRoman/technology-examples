package ru.uskov.dmitry.transferapp.storage.impl.mem;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;
import ru.uskov.dmitry.transferapp.storage.impl.mem.serializer.BytesSerializer;

import java.io.IOException;

public class MapDbSerializer<V> implements Serializer<V> {

    private final BytesSerializer bytesSerializer;
    private final Serializer<byte[]> serializer = Serializer.BYTE_ARRAY;

    public MapDbSerializer(BytesSerializer serializer) {
        bytesSerializer = serializer;
    }

    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull V value) throws IOException {
        serializer.serialize(out, bytesSerializer.serialize(value));
    }

    @Override
    public V deserialize(@NotNull DataInput2 input, int available) throws IOException {
        return bytesSerializer.deserialize(serializer.deserialize(input, available));
    }
}
