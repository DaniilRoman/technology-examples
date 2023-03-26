package ru.uskov.dmitry.http.rest.server.impl.resolvers;

import ru.uskov.dmitry.http.rest.server.impl.HttpRequest;

public interface MethodArgumentResolver<T> {
    T resolve(HttpRequest request);
}
