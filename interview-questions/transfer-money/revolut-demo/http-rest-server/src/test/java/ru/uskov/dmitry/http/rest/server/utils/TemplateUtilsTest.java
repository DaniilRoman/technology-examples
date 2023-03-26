package ru.uskov.dmitry.http.rest.server.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.function.Function;

public class TemplateUtilsTest {

    @Test
    public void testGetAllVariables() {
        Map<String, Function<String, String>> resolvers = TemplateUtils.getResolvers("/${hello}/${world}/create/${id}");
        Assert.assertEquals("my", resolvers.get("hello").apply("/my/name/create/123"));
        Assert.assertEquals("name", resolvers.get("world").apply("/my/name/create/123"));
        Assert.assertEquals("123", resolvers.get("id").apply("/my/name/create/123"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllVariables_DuplicateTemplateName() {
        TemplateUtils.getResolvers("/${hello}/${hello}/create/${id}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllVariables_nullArgument() {
        TemplateUtils.getResolvers(null);
    }

    @Test
    public void testGetAllVariables_invalidUrl() {
        Map<String, Function<String, String>> resolvers = TemplateUtils.getResolvers("/${hello}/${world}/create/${id}");
        Assert.assertNull(resolvers.get("hello").apply("/incorrectUrl"));
    }
}