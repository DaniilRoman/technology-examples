package ru.uskov.dmitry.http.rest.server.impl.route;

import com.sun.net.httpserver.HttpExchange;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import ru.uskov.dmitry.http.rest.server.impl.exception.HttpRequestException;

import java.net.URI;

public class RegisterStaticPathTest {

    @Test(expected = IllegalArgumentException.class)
    public void duplicatePathException() {
        Route route = new Route();
        route.register(e -> {
        }, "/hello", "GET");
        route.register(e -> {
        }, "/hello", "GET");
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicatePathException2() {
        Route route = new Route();
        route.register(e -> {
        }, "hello", "GET");
        route.register(e -> {
        }, "/hello", "GET");
    }

    /**
     * Should register executor with same path because executors binds for different http methods
     */
    @Test
    public void registerDiffMethods() throws Exception {
        Route route = new Route();
        RequestExecutorHandler h1 = new RequestExecutorHandler();
        RequestExecutorHandler h2 = new RequestExecutorHandler();
        route.register(h1, "/hello", "GET");
        route.register(h2, "/hello", "POST");
        HttpExchange e1 = createExchange("/hello", "GET");
        route.execute(e1);
        Assert.assertSame(h1.getHandledExchange(), e1);

        HttpExchange e2 = createExchange("/hello", "POST");
        route.execute(e2);
        Assert.assertSame(h2.getHandledExchange(), e2);
    }

    @Test
    public void deepRegistrationTest() throws Exception {
        Route route = new Route();
        RequestExecutorHandler h1 = new RequestExecutorHandler();
        RequestExecutorHandler h2 = new RequestExecutorHandler();
        route.register(h1, "/hello", "GET");
        route.register(h2, "/hello/world", "GET");
        HttpExchange e1 = createExchange("/hello", "GET");
        route.execute(e1);
        Assert.assertSame(h1.getHandledExchange(), e1);

        HttpExchange e2 = createExchange("/hello/world", "GET");
        route.execute(e2);
        Assert.assertSame(h2.getHandledExchange(), e2);
    }

    @Test(expected = HttpRequestException.class)
    public void differentMethodTest() throws Exception {
        Route route = new Route();
        RequestExecutorHandler h1 = new RequestExecutorHandler();
        route.register(h1, "/hello/world", "GET");
        HttpExchange e1 = createExchange("/hello", "POST");
        route.execute(e1);
        Assert.assertSame(h1.getHandledExchange(), e1);
    }

    @Test
    public void slashTest() throws Exception {
        Route route = new Route();
        RequestExecutorHandler h1 = new RequestExecutorHandler();
        route.register(h1, "/hello", "GET");
        HttpExchange e1 = createExchange("/hello", "GET");
        route.execute(e1);
        Assert.assertSame(h1.getHandledExchange(), e1);
        h1.clear();

        e1 = createExchange("/hello/", "GET");
        route.execute(e1);
        Assert.assertSame(h1.getHandledExchange(), e1);
        h1.clear();

        e1 = createExchange("hello", "GET");
        route.execute(e1);
        Assert.assertSame(h1.getHandledExchange(), e1);
        h1.clear();
    }

    @Test
    public void deepSlashTest() throws Exception {
        Route route = new Route();
        RequestExecutorHandler h1 = new RequestExecutorHandler();
        route.register(h1, "/hello/world", "GET");

        HttpExchange e1 = createExchange("/hello/world", "GET");
        route.execute(e1);
        Assert.assertSame(h1.getHandledExchange(), e1);
        h1.clear();

        e1 = createExchange("hello/world/", "GET");
        route.execute(e1);
        Assert.assertSame(h1.getHandledExchange(), e1);
        h1.clear();

        e1 = createExchange("/hello/world/", "GET");
        route.execute(e1);
        Assert.assertSame(h1.getHandledExchange(), e1);
        h1.clear();
    }

    private HttpExchange createExchange(String path, String methodName) {
        try{
            HttpExchange exchange = Mockito.mock(HttpExchange.class, Answers.RETURNS_DEEP_STUBS);
            Mockito.when(exchange.getRequestURI()).thenReturn(new URI(path));
            Mockito.when(exchange.getRequestMethod()).thenReturn(methodName);
            return exchange;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class RequestExecutorHandler implements RequestExecutor {

        private HttpExchange handledExchange;

        @Override
        public void execute(HttpExchange exchange) {
            this.handledExchange = exchange;
        }

        public HttpExchange getHandledExchange() {
            return handledExchange;
        }

        public void clear() {
            handledExchange = null;
        }
    }

}