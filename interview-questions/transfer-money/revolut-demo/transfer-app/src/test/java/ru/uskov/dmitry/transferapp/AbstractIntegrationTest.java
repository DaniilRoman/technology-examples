package ru.uskov.dmitry.transferapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import ru.uskov.dmitry.transferapp.config.TransferAppConfig;

import java.io.IOException;

public class AbstractIntegrationTest {

    protected static final String DEFAULT_TEST_ADDRESS = "http://localhost:8083";

    protected static TransferApp app;

    protected static CloseableHttpClient httpClient;

    protected static String address;

    @BeforeClass
    public static void beforeClass() {
        address = System.getProperties().getProperty("transfer.app.test.remote.address");

        if (address == null) {
            address = DEFAULT_TEST_ADDRESS;
            app = new TransferApp(new TransferAppConfig().serverPort(8083).waitTransactionMs(5000));
            app.start();
        }
        httpClient = HttpClients.createDefault();
    }



    protected <T> T executeRequest(String url, String method, Object body, int expectedStatusCode, Class<T> expectedResponse) throws IOException {


        CloseableHttpResponse response = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            HttpRequestBase request = createRequest(url, method);
            if (body != null) {
                ((HttpEntityEnclosingRequest) request).setEntity(new StringEntity(objectMapper.writeValueAsString(body)));
            }
            // add request headers
            request.addHeader("Content-Type", "application/json");

            System.out.println("Execute request: " + request.getURI() + " (" + method + ")");
            response = httpClient.execute(request);

            Assert.assertEquals(expectedStatusCode, response.getStatusLine().getStatusCode());
            if(expectedResponse != null) {
                String s = EntityUtils.toString(response.getEntity());
                System.out.println("Result:\n" + s);
                return objectMapper.readValue(s, expectedResponse);
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(response != null) {
                response.close();
            }
        }
    }

    private HttpRequestBase createRequest(String url, String method) {
        switch (method) {
            case "GET":
                return new HttpGet(address + url);
            case "POST":
                return new HttpPost(address + url);
            case "PUT":
                return new HttpPut(address + url);
        }
        throw new IllegalArgumentException("Unexpected method: " + method);
    }

    @AfterClass
    public static void afterClass() throws IOException {
        if (app != null) {
            app.stop();
        }
        if (httpClient != null) {
            httpClient.close();
        }
    }




}
