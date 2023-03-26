package ru.uskov.dmitry.http.rest.server.impl.resolvers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uskov.dmitry.http.rest.server.impl.HttpRequest;
import ru.uskov.dmitry.http.rest.server.impl.exception.HttpRequestException;
import ru.uskov.dmitry.http.rest.server.impl.marshaller.Marshaller;
import ru.uskov.dmitry.http.rest.server.impl.marshaller.MarshallerException;

import java.util.List;

public class BodyResolver<T> implements MethodArgumentResolver<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BodyResolver.class);


    private final Marshaller marshaller;

    private final Class<T> argumentType;

    public BodyResolver(Marshaller marshaller, Class<T> argumentType) {
        this.marshaller = marshaller;
        this.argumentType = argumentType;
    }

    @Override
    public T resolve(HttpRequest request) {
        Marshaller.ContentType  contentType = getContentType(request);
        try {
            return marshaller.unmarshal(request.getExchange().getRequestBody(), argumentType, contentType);
        } catch (MarshallerException e) {
            LOGGER.error("Could not unmarshal body value: {}", e.getMessage(), e);
            throw new HttpRequestException(400, "Could not unmarshal body value: " + e.getMessage(), e);
        }
    }

    private Marshaller.ContentType getContentType(HttpRequest request) {
        List<String> contentType = request.getExchange().getRequestHeaders().get("Content-Type");

        if (contentType != null) {
            for (String t : contentType) {
                Marshaller.ContentType value = Marshaller.ContentType.getByHeaderValue(t);
                if (value != null) {
                    return value;
                }
            }
        }
        throw new HttpRequestException(400,
            "Content-Type must be one of this: " + Marshaller.ContentType.getHeaderValues() + ". Actual value: " + contentType);
    }
}
