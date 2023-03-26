package ru.uskov.dmitry.http.rest.server.impl.resolvers;

import ru.uskov.dmitry.http.rest.server.impl.HttpRequest;

import java.util.Map;

public class QueryParamsResolver implements MethodArgumentResolver<Map<String,String>> {
    @Override
    public Map<String, String> resolve(HttpRequest request) {
        return request.getQueryRequests();
    }
}
