package ru.uskov.dmitry.http.rest.server.utils;


import org.junit.Assert;
import org.junit.Test;


public class UrlUtilsTest {

    @Test
    public void validatePath() {

        Assert.assertTrue(UrlUtils.validatePath("/"));
        Assert.assertTrue(UrlUtils.validatePath("/hello"));
        Assert.assertTrue(UrlUtils.validatePath("/hello/"));
        Assert.assertTrue(UrlUtils.validatePath("/hello/world"));
        Assert.assertTrue(UrlUtils.validatePath("/hello-world/1234"));
        Assert.assertFalse(UrlUtils.validatePath(" "));
        Assert.assertFalse(UrlUtils.validatePath("/ "));
        Assert.assertFalse(UrlUtils.validatePath("/ hello"));
        Assert.assertFalse(UrlUtils.validatePath("/hello.oi"));
        Assert.assertFalse(UrlUtils.validatePath("/hello,oi"));
        Assert.assertFalse(UrlUtils.validatePath("/hel,lo"));
        Assert.assertFalse(UrlUtils.validatePath("/hel,lo"));

        Assert.assertTrue(UrlUtils.validatePath("/hello/world/${id}"));
        Assert.assertTrue(UrlUtils.validatePath("/hello/world/${id}/qwe"));
        Assert.assertTrue(UrlUtils.validatePath("/hello/world/${id}/${id1}"));
        Assert.assertTrue(UrlUtils.validatePath("/hello/world/${id}/qwe/${id1}"));
        Assert.assertTrue(UrlUtils.validatePath("/${id}/qwe/${id1}"));
        Assert.assertTrue(UrlUtils.validatePath("/${id}"));
        Assert.assertFalse(UrlUtils.validatePath("/hello/world/${v1/{v2}"));
    }

    @Test(expected = NullPointerException.class)
    public void validatePathNpe() {
        UrlUtils.validatePath(null);
    }


    @Test
    public void cleanPath() {
        Assert.assertEquals("/", UrlUtils.cleanPath(" "));
        Assert.assertEquals("/", UrlUtils.cleanPath(""));
        Assert.assertEquals("/", UrlUtils.cleanPath("//"));
        Assert.assertEquals("/hello", UrlUtils.cleanPath("/hello"));
        Assert.assertEquals("/hello", UrlUtils.cleanPath("hello"));
        Assert.assertEquals("/hello", UrlUtils.cleanPath("hello/"));
        Assert.assertEquals("/hello/world", UrlUtils.cleanPath("hello//world"));
    }

    @Test(expected = NullPointerException.class)
    public void cleanPathNpe() {
        UrlUtils.cleanPath(null);
    }
}