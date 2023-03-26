package ru.uskov.dmitry.http.rest.server.impl.route;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;
import ru.uskov.dmitry.http.rest.server.impl.HttpRequest;
import ru.uskov.dmitry.http.rest.server.impl.exception.HttpRequestException;
import ru.uskov.dmitry.http.rest.server.impl.exception.InvocationException;
import ru.uskov.dmitry.http.rest.server.impl.resolvers.MethodArgumentResolver;
import ru.uskov.dmitry.http.rest.server.impl.marshaller.Marshaller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestExecutorImpl implements RequestExecutor {

    private final Marshaller marshaller;
    private final Object instance;
    private final Method method;
    private final MethodArgumentResolver<?>[] resolvers;

    public RequestExecutorImpl(@NotNull Object instance,
                               @NotNull Method declaredMethod,
                               @NotNull Marshaller marshaller,
                               @NotNull MethodArgumentResolver<?>[] resolvers) {

        this.instance = instance;
        this.method = declaredMethod;
        this.marshaller = marshaller;
        this.resolvers = resolvers;
    }


    @Override
    public void execute(HttpExchange exchange) throws Exception {
        HttpRequest request = new HttpRequest(exchange);
        Object[] args = new Object[resolvers.length];
        for (int i = 0; i < resolvers.length; i++) {
            args[i] = resolvers[i].resolve(request);
        }

        Object result = null;
        try {
            result = method.invoke(instance, args);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if(t instanceof HttpRequestException) {
                throw (HttpRequestException)t;
            } else {
                throw new InvocationException(e);
            }
        }
        if(result != null) {
            String body = marshaller.marshal(result, Marshaller.ContentType.APPLICATION_JSON);
            Headers headers = exchange.getResponseHeaders();
            headers.set("Content-Type", Marshaller.ContentType.APPLICATION_JSON.getHeaderValue());
            byte[] b = body.getBytes();
            exchange.sendResponseHeaders(200, b.length);
            exchange.getResponseBody().write(b);
        } else {
            exchange.sendResponseHeaders(200, 0);
        }
    }

}
