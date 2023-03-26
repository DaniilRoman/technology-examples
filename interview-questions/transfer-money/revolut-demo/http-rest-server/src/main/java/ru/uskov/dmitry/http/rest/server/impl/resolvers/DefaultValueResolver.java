package ru.uskov.dmitry.http.rest.server.impl.resolvers;

import ru.uskov.dmitry.http.rest.server.impl.HttpRequest;
import ru.uskov.dmitry.http.rest.server.utils.TypeUtils;

public class DefaultValueResolver<T> implements MethodArgumentResolver<T> {


    private final Class<T> argumentType;

    public DefaultValueResolver(Class<T> argumentType) {
        if(argumentType == null) {
            throw new NullPointerException("argumentType mustn't be null");
        }
        this.argumentType = argumentType;
    }


    @Override
    public T resolve(HttpRequest request) {
        return TypeUtils.getDefault(argumentType);
    }
}
