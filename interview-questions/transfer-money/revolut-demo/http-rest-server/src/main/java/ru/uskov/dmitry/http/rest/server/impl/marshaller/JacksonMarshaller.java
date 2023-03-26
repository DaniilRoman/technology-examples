package ru.uskov.dmitry.http.rest.server.impl.marshaller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

public class JacksonMarshaller implements Marshaller {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new KotlinModule());

    @Override
    public String marshal(@Nullable Object object, @NotNull ContentType contentType) throws MarshallerException {
        if(contentType != ContentType.APPLICATION_JSON) {
            throw new MarshallerException("Unsupported Content-Type: " + contentType);
        }
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new MarshallerException(e);
        }
    }

    @Override
    public <T> T unmarshal(@NotNull InputStream content, @NotNull Class<T> type, @NotNull ContentType contentType) throws MarshallerException {
        if(contentType != ContentType.APPLICATION_JSON) {
            throw new MarshallerException("Unsupported Content-Type: " + contentType);
        }
        try {
            return mapper.readValue(content, type);
        } catch (Exception e) {
            throw new MarshallerException(e);
        }
    }
}
