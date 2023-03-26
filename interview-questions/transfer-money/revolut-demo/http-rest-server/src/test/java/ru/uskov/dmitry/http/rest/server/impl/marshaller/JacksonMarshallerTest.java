package ru.uskov.dmitry.http.rest.server.impl.marshaller;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Objects;

public class JacksonMarshallerTest {

    private static final JacksonMarshaller marshaller = new JacksonMarshaller();
    private static final String TEST_DTO_JSON = "{\"id\":\"test-id\"}";
    private static final TestDto TEST_DTO = new TestDto("test-id");

    @Test
    public void marshal() throws MarshallerException {
        Assert.assertEquals(TEST_DTO_JSON, marshaller.marshal(TEST_DTO, Marshaller.ContentType.APPLICATION_JSON));
        Assert.assertEquals("null", marshaller.marshal(null, Marshaller.ContentType.APPLICATION_JSON));
    }

    @Test(expected = IllegalArgumentException.class)
    public void marshalException2() throws MarshallerException {
        marshaller.marshal(TEST_DTO, null);
    }

    @Test
    public void unmarshal() throws MarshallerException {
        Assert.assertEquals(TEST_DTO, marshaller.unmarshal(new ByteArrayInputStream(TEST_DTO_JSON.getBytes()), TestDto.class, Marshaller.ContentType.APPLICATION_JSON));
    }

    @Test(expected = MarshallerException.class)
    public void unmarshalException() throws MarshallerException {
        marshaller.unmarshal(new ByteArrayInputStream("invalidJson".getBytes()), TestDto.class, Marshaller.ContentType.APPLICATION_JSON);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unmarshalException2() throws MarshallerException {
        marshaller.unmarshal(new ByteArrayInputStream(TEST_DTO_JSON.getBytes()), null, Marshaller.ContentType.APPLICATION_JSON);
    }


    @Test(expected = IllegalArgumentException.class)
    public void unmarshalException3() throws MarshallerException {
        marshaller.unmarshal(new ByteArrayInputStream(TEST_DTO_JSON.getBytes()),  TestDto.class, null);
    }

    public static class TestDto {
        private String id;

        public TestDto() {
        }

        public TestDto(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TestDto testDto = (TestDto)o;
            return Objects.equals(id, testDto.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
