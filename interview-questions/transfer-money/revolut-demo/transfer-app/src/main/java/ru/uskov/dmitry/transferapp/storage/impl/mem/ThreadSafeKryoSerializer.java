package ru.uskov.dmitry.transferapp.storage.impl.mem;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.jetbrains.annotations.NotNull;
import ru.uskov.dmitry.transferapp.storage.impl.mem.serializer.BytesSerializer;

/**
 * By default it is not allowed the same instance Kryo in different threads.
 * This class is a ThreadSafe-wrapper for {@link Kryo}.
 * </p>
 * This class uses ThreadLocal variable and creates new Kryo instance for each {@link Thread}.
 */
public class ThreadSafeKryoSerializer implements BytesSerializer {

    private static final int BUFFER_SIZE = 1024;
    private static final int MAX_BUFFER_SIZE = -1;

    private final ThreadLocal<Kryo> kryoTL = ThreadLocal.withInitial(Kryo::new);

    @Override
    public byte[] serialize(Object object) {
        Kryo kryo = kryoTL.get();
        try (Output output = new Output(BUFFER_SIZE, MAX_BUFFER_SIZE)) {
            kryo.writeClassAndObject(output, object);
            return output.toBytes();
        }
    }

    @Override
    public <T> T deserialize(@NotNull byte[] data) {
        Kryo kryo = kryoTL.get();
        try (Input input = new Input(data)) {
            return (T)kryo.readClassAndObject(input);
        }
    }

}
