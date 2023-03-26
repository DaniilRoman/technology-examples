package ru.uskov.dmitry.http.rest.server.impl.resolvers;

import org.jetbrains.annotations.NotNull;
import ru.uskov.dmitry.http.rest.server.impl.HttpRequest;
import ru.uskov.dmitry.http.rest.server.utils.TemplateUtils;
import ru.uskov.dmitry.http.rest.server.utils.TypeUtils;

import java.util.function.Function;

public class PathVariableResolver<T> implements MethodArgumentResolver<T> {

    private final Function<String, String> variableResolver;
    private final Function<String, T> typeResolver;

    public PathVariableResolver(@NotNull String pathValue, @NotNull String parameterName, @NotNull Class<T> argumentType) {
        this.variableResolver = TemplateUtils.getResolvers(pathValue).get(parameterName);
        this.typeResolver = TypeUtils.getResolver(argumentType);
        if(variableResolver == null) {
            throw new IllegalArgumentException("Not found path variable '" + parameterName +"' in path '" + pathValue + "'");
        }
    }


    @Override
    public T resolve(HttpRequest request) {
        String value =  variableResolver.apply(request.getExchange().getRequestURI().getPath());
        return typeResolver.apply(value);
    }
}
