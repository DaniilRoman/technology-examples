package ru.uskov.dmitry.http.rest.server.impl.marshaller;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface Marshaller {

    String marshal(Object object, ContentType contentType) throws MarshallerException;

    <T> T unmarshal(InputStream content, Class<T> type, ContentType contentType) throws MarshallerException;

    enum ContentType {

        APPLICATION_JSON("application/json");

        private static final Map<String, ContentType> valueByHeader;

        static {
            Map<String, ContentType> tmp = new HashMap<>();
            for (ContentType value : values()) {
                tmp.put(value.headerValue, value);
            }
            valueByHeader = Collections.unmodifiableMap(tmp);
        }

        private final String headerValue;

        ContentType(String value) {
            headerValue = value;
        }

        public String getHeaderValue() {
            return headerValue;
        }

        public static ContentType getByHeaderValue(String headerValue) {
            return valueByHeader.get(headerValue);
        }

        public static Set<String> getHeaderValues() {
            return valueByHeader.keySet();
        }
    }
}
