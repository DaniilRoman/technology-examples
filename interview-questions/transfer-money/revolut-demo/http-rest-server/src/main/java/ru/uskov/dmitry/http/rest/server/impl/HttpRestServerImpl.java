package ru.uskov.dmitry.http.rest.server.impl;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uskov.dmitry.http.rest.server.api.HttpMethod;
import ru.uskov.dmitry.http.rest.server.api.HttpRestServer;
import ru.uskov.dmitry.http.rest.server.api.annotation.Path;
import ru.uskov.dmitry.http.rest.server.impl.exception.HttpRequestException;
import ru.uskov.dmitry.http.rest.server.impl.marshaller.JacksonMarshaller;
import ru.uskov.dmitry.http.rest.server.impl.marshaller.Marshaller;
import ru.uskov.dmitry.http.rest.server.impl.route.RequestExecutorFactory;
import ru.uskov.dmitry.http.rest.server.impl.route.Route;
import ru.uskov.dmitry.http.rest.server.utils.HttpHandlerExt;
import ru.uskov.dmitry.http.rest.server.utils.UrlUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpRestServerImpl implements HttpRestServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRestServerImpl.class);

    private final HttpServer server;
    private final ExecutorService serverExecutor;
    private final HttpContext rootHttpContext;
    private final String contextPath;
    private final Route rootRoute;
    private final Marshaller marshaller;
    private final RequestExecutorFactory requestExecutorFactory;
    private final int port;

    HttpRestServerImpl(HttpRestServerBuilder httpRestServerBuilder) throws IOException {
        this.marshaller = new JacksonMarshaller();
        this.requestExecutorFactory = new RequestExecutorFactory();
        this.server = HttpServer.create();
        int poolSize = httpRestServerBuilder.getPoolSize();
        if (poolSize < 0) {
            poolSize = Runtime.getRuntime().availableProcessors() * 2;
        }
        this.port = httpRestServerBuilder.getPort();
        this.serverExecutor = Executors.newFixedThreadPool(poolSize);
        server.setExecutor(serverExecutor);
        server.bind(new InetSocketAddress(port), 0);
        contextPath = UrlUtils.cleanPath(httpRestServerBuilder.getContextPath());
        this.rootRoute = new Route();
        rootHttpContext = server.createContext(contextPath, handelException(rootRoute::execute));

    }

    @Override
    public void start() {
        server.start();
        LOGGER.info("Server has been started. Listen port: '{}', contextPath: '{}'", port, contextPath);
    }

    @Override
    public void stop() {
        server.removeContext(rootHttpContext);
        server.stop(0);
        serverExecutor.shutdownNow();

    }

    @Override
    public void registerController(Object... controllers) {
        for (Object controller : controllers) {
            String path = contextPath;
            Path controllerPath = controller.getClass().getAnnotation(Path.class);
            if (controllerPath == null) {
                throw new IllegalArgumentException("class '" + controller.getClass().getName() + "' is not annotated ru.uskov.dmitry.http.rest.server.api.annotation.Path");
            }
            if (!UrlUtils.validatePath(controllerPath.value())) {
                throw new IllegalArgumentException("Invalid Path value: " + controllerPath.value());
            }
            path = UrlUtils.cleanPath(path + UrlUtils.cleanPath(controllerPath.value()));
            HttpMethod method = controllerPath.method();
            for (Method declaredMethod : controller.getClass().getDeclaredMethods()) {
                registerMethod(controller, declaredMethod, path, method);
            }
        }
    }

    private void registerMethod(Object controller, Method declaredMethod, String path, HttpMethod method) {
        Path annotation = declaredMethod.getAnnotation(Path.class);
        if (annotation == null) {
            return;
        }
        if (!annotation.value().trim().isEmpty() && !UrlUtils.validatePath(annotation.value())) {
            throw new IllegalArgumentException("Invalid Path value: '" + annotation.value() + "'");
        }
        path = path + UrlUtils.cleanPath(annotation.value());
        if (annotation.method() != HttpMethod.NULL) {
            method = annotation.method();
        }
        if (method == HttpMethod.NULL) {
            throw new IllegalArgumentException("HttpMethod for method '" +
                    declaredMethod.getDeclaringClass().getName() + "." + declaredMethod.getName() + "' is not set");
        }
        rootRoute.register(requestExecutorFactory.createExecutor(controller, declaredMethod, marshaller, path), path, method.name());
    }

    private HttpHandler handelException(HttpHandlerExt target) {
        return exchange -> {
            OutputStream responseBody = exchange.getResponseBody();
            try {
                target.handle(exchange);
            } catch (HttpRequestException e) {
                LOGGER.debug("Client exception: {}", e.getMessage(), e);
                sendResponse(exchange, e.getStatusCode(), e.getStatusCode() + ": " + e.getMessage());
            } catch (Exception e) {
                LOGGER.error("Internal server error: {}", e.getMessage(), e);
                sendResponse(exchange, 500, "Internal server error: " + e.getMessage());
            } finally {
                responseBody.close();
                exchange.close();
            }
        };
    }

    private void sendResponse(HttpExchange exchange, int code, Object payload) {
        try {
            String body = marshaller.marshal(payload, Marshaller.ContentType.APPLICATION_JSON);
            OutputStream responseBody = exchange.getResponseBody();
            exchange.getResponseHeaders().set("Content-Type", Marshaller.ContentType.APPLICATION_JSON.getHeaderValue());
            exchange.sendResponseHeaders(code, body.length());
            responseBody.write(body.getBytes());
        } catch (Exception e) {
            LOGGER.error("Could not send response: {}", e.getMessage(), e);
        }

    }


}
