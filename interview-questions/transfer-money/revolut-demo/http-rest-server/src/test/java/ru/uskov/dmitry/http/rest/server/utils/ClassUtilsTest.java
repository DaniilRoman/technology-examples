package ru.uskov.dmitry.http.rest.server.utils;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class ClassUtilsTest {

    @Test
    public void testGetParameterDescription() throws Exception {
        Method doNothing = TestClass.class.getDeclaredMethod("doNothing", String.class);
        Parameter parameter = doNothing.getParameters()[0];
        System.out.println(parameter.isNamePresent());
        String description = ClassUtils.getParameterDescription(doNothing, 0);
        Assert.assertEquals("0-parameter in method ru.uskov.dmitry.http.rest.server.utils.TestClass.doNothing", description);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetParameterDescription_null_ref() throws Exception {
        String description = ClassUtils.getParameterDescription(null, 0);
        Assert.assertEquals("0-parameter in method ru.uskov.dmitry.http.rest.server.utils.TestClass.doNothing", description);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetParameterDescription_invalidIndex() throws Exception {
        Method doNothing = TestClass.class.getDeclaredMethod("doNothing", String.class);
        Parameter parameter = doNothing.getParameters()[0];
        System.out.println(parameter.isNamePresent());
        String description = ClassUtils.getParameterDescription(doNothing, -1);
    }

    @Test
    public void test2() throws Exception {
        Method doNothing = TestClass.class.getDeclaredMethod("doNothing", Map.class);
        System.out.println(doNothing);
    }
}