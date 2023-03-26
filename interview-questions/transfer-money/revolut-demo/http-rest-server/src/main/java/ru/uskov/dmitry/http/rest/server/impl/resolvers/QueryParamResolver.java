package ru.uskov.dmitry.http.rest.server.impl.resolvers;

import ru.uskov.dmitry.http.rest.server.impl.HttpRequest;
import ru.uskov.dmitry.http.rest.server.impl.exception.HttpRequestException;
import ru.uskov.dmitry.http.rest.server.utils.TypeUtils;

import java.util.Map;
import java.util.function.Function;

public class QueryParamResolver<T> implements MethodArgumentResolver<T> {

    private final String paramName;
    private final Class<T> argumentType;
    private final boolean required;
    private final Function<String, Object> resolver;

    public QueryParamResolver(String paramName, Class<T> argumentType, boolean required) {
        if (paramName == null) {
            throw new NullPointerException();
        }
        if (argumentType == null) {
            throw new NullPointerException();
        }
        resolver = getResolver(argumentType);
        this.paramName = paramName;
        this.argumentType = argumentType;
        this.required = required;
    }

    @Override
    public T resolve(HttpRequest request) {
        Map<String, String> queryRequests = request.getQueryRequests();
        String queryValue = queryRequests.get(paramName);
        if (queryValue == null) {
            if (required) {
                throw new HttpRequestException(400, "Query param '" + paramName + "' is required");
            }
            return TypeUtils.getDefault(argumentType);
        }
        return (T) resolver.apply(queryValue);
    }


    private Function<String, Object> getResolver(Class<T> argumentType) {
        if (argumentType.equals(String.class)) {
            return s -> s;
        }
        if (argumentType.equals(boolean.class) || argumentType.equals(Boolean.class)) {
            return Boolean::valueOf;
        }
        if (argumentType.equals(byte.class) || argumentType.equals(Byte.class)) {
            return Byte::valueOf;
        }
        if (argumentType.equals(short.class) || argumentType.equals(Short.class)) {
            return Short::valueOf;
        }
        if (argumentType.equals(int.class) || argumentType.equals(Integer.class)) {
            return Integer::valueOf;
        }
        if (argumentType.equals(long.class) || argumentType.equals(Long.class)) {
            return Long::valueOf;
        }
        if (argumentType.equals(float.class) || argumentType.equals(Float.class)) {
            return Float::valueOf;
        }
        if (argumentType.equals(double.class) || argumentType.equals(Double.class)) {
            return Double::valueOf;
        }
        if (argumentType.equals(char.class) || argumentType.equals(Character.class)) {
            return s -> {
                s = s.trim();
                if (s.isEmpty()) {
                    return ' ';
                }
                return s.charAt(0);
            };
        }
        throw new IllegalArgumentException("Could not resolve type '" + argumentType + "'");
    }
}
