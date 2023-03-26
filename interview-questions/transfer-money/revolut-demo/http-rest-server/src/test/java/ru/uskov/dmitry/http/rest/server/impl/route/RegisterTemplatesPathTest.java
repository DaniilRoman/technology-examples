package ru.uskov.dmitry.http.rest.server.impl.route;

import com.sun.net.httpserver.HttpExchange;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import ru.uskov.dmitry.http.rest.server.impl.exception.HttpRequestException;

import java.net.URI;

public class RegisterTemplatesPathTest {

    @Test(expected = IllegalArgumentException.class)
    public void invalidTemplateTest() {
        Route route = new Route();
        route.register(e -> {}, "/${var1}-hi", "GET");
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicatePathException() {
        Route route = new Route();
        route.register(e -> {}, "/${var1}", "GET");
        route.register(e -> {}, "/${var2}", "GET");
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicatePathException2() {
        Route route = new Route();
        route.register(e -> {}, "${var1}", "GET");
        route.register(e -> {}, "/${var2}", "GET");
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicatePathException3() {
        Route route = new Route();
        route.register(e -> {}, "/${var1}/hello", "GET");
        route.register(e -> {}, "/${var2}/hello", "GET");
    }

    @Test
    public void templateAndStatic() throws Exception {
        Route route = new Route();
        RequestExecutorHandler h1 = new RequestExecutorHandler();
        RequestExecutorHandler h2 = new RequestExecutorHandler();
        route.register(h1, "root/${var1}", "GET");
        route.register(h2, "root/world", "GET");

        HttpExchange e1 = createExchange("/root/hello", "GET");
        route.execute(e1);
        Assert.assertSame(h1.getHandledExchange(), e1);

        HttpExchange e2 = createExchange("/root/world", "GET");
        route.execute(e2);
        Assert.assertSame(h2.getHandledExchange(), e2);
    }

    @Test
    public void templateAndStaticConflictTest() throws Exception {
        Route route = new Route();
        RequestExecutorHandler h1 = new RequestExecutorHandler();
        RequestExecutorHandler h2 = new RequestExecutorHandler();
        route.register(h1, "root/${var1}/create", "GET");
        route.register(h2, "root/world", "GET");

        HttpExchange e1 = createExchange("/root/world/create", "GET");
        route.execute(e1);
        Assert.assertSame(h1.getHandledExchange(), e1);
    }

    @Test(expected = HttpRequestException.class)
    public void templateAndStaticConflictTest2() throws Exception {
        Route route = new Route();
        RequestExecutorHandler h1 = new RequestExecutorHandler();
        RequestExecutorHandler h2 = new RequestExecutorHandler();
        route.register(h1, "root/${var1}/create", "GET");
        route.register(h2, "root/world/p1", "GET");

        HttpExchange e1 = createExchange("/root/world/", "GET");
        route.execute(e1);
    }

    @Test
    public void templateAndStaticConflictTest3() throws Exception {
        Route route = new Route();
        RequestExecutorHandler h1 = new RequestExecutorHandler();
        RequestExecutorHandler h2 = new RequestExecutorHandler();
        route.register(h1, "root/p1/p2/p3", "GET");
        route.register(h2, "root/${hi}/p2/p4", "GET");

        HttpExchange e2 = createExchange("/root/p1/p2/p4", "GET");
        route.execute(e2);
        Assert.assertSame(h2.getHandledExchange(), e2);
    }


    /**
     * Should register executor with same path because executors binds for different http methods
     */
    @Test
    public void registerDiffMethods() throws Exception {
        Route route = new Route();
        RequestExecutorHandler h1 = new RequestExecutorHandler();
        RequestExecutorHandler h2 = new RequestExecutorHandler();
        route.register(h1, "/${var1}", "GET");
        route.register(h2, "/${var1}", "POST");
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
        route.register(h1, "/${var1}", "GET");
        route.register(h2, "/${var1}/world", "GET");
        HttpExchange e1 = createExchange("/hello", "GET");
        route.execute(e1);
        Assert.assertSame(h1.getHandledExchange(), e1);

        HttpExchange e2 = createExchange("/hello/world", "GET");
        route.execute(e2);
        Assert.assertSame(h2.getHandledExchange(), e2);
    }


    @Test
    public void slashTest() throws Exception {
        Route route = new Route();
        RequestExecutorHandler h1 = new RequestExecutorHandler();
        route.register(h1, "/${var1}", "GET");
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
        route.register(h1, "/${var1}/${var2}", "GET");

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
        try {
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