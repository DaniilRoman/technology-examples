package ru.uskov.dmitry.http.rest.server.utils;

import org.junit.Assert;
import org.junit.Test;

public class TypeUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testGetDefault_null() {
        TypeUtils.getDefault(null);
    }


    @Test
    public void testGetDefault() {
        Assert.assertEquals(false, TypeUtils.getDefault(boolean.class));
        Assert.assertEquals(0, (byte) TypeUtils.getDefault(byte.class));
        Assert.assertEquals(0, (short) TypeUtils.getDefault(short.class));
        Assert.assertEquals(0, (int) TypeUtils.getDefault(int.class));
        Assert.assertEquals(0, (long) TypeUtils.getDefault(long.class));
        Assert.assertEquals(0f, TypeUtils.getDefault(float.class), 0.0);
        Assert.assertEquals(0d, TypeUtils.getDefault(double.class), 0.0);
        Assert.assertEquals(0, (char) TypeUtils.getDefault(char.class));
        Assert.assertNull(TypeUtils.getDefault(String.class));
    }


    @Test
    public void testIsSimpleType() {
        Assert.assertTrue(TypeUtils.isSimpleType(boolean.class));
        Assert.assertTrue(TypeUtils.isSimpleType(byte.class));
        Assert.assertTrue(TypeUtils.isSimpleType(short.class));
        Assert.assertTrue(TypeUtils.isSimpleType(int.class));
        Assert.assertTrue(TypeUtils.isSimpleType(long.class));
        Assert.assertTrue(TypeUtils.isSimpleType(float.class));
        Assert.assertTrue(TypeUtils.isSimpleType(double.class));
        Assert.assertTrue(TypeUtils.isSimpleType(char.class));
        Assert.assertTrue(TypeUtils.isSimpleType(Boolean.class));
        Assert.assertTrue(TypeUtils.isSimpleType(Byte.class));
        Assert.assertTrue(TypeUtils.isSimpleType(Short.class));
        Assert.assertTrue(TypeUtils.isSimpleType(Integer.class));
        Assert.assertTrue(TypeUtils.isSimpleType(Long.class));
        Assert.assertTrue(TypeUtils.isSimpleType(Float.class));
        Assert.assertTrue(TypeUtils.isSimpleType(Double.class));
        Assert.assertTrue(TypeUtils.isSimpleType(Character.class));
        Assert.assertTrue(TypeUtils.isSimpleType(String.class));

        Assert.assertFalse(TypeUtils.isSimpleType(Object.class));
        Assert.assertFalse(TypeUtils.isSimpleType(TypeUtilsTest.class));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testIsSimpleType_null() {
        TypeUtils.isSimpleType(null);
    }

    @Test
    public void getResolver() {
        Assert.assertEquals(false, TypeUtils.getResolver(boolean.class).apply("false"));
        Assert.assertEquals((byte) 123, (byte) TypeUtils.getResolver(byte.class).apply("123"));
        Assert.assertEquals((short) 123, (short) TypeUtils.getResolver(short.class).apply("123"));
        Assert.assertEquals(123, (int)TypeUtils.getResolver(int.class).apply("123"));
        Assert.assertEquals(123L, (long)TypeUtils.getResolver(long.class).apply("123"));
        Assert.assertEquals(123f, TypeUtils.getResolver(float.class).apply("123"), 0);
        Assert.assertEquals(123d, TypeUtils.getResolver(double.class).apply("123"), 0);
        Assert.assertEquals('x', (char)TypeUtils.getResolver(char.class).apply("x"));
        Assert.assertEquals(Boolean.TRUE, TypeUtils.getResolver(Boolean.class).apply("true"));
        Assert.assertEquals(new Byte((byte) 123), TypeUtils.getResolver(Byte.class).apply("123"));
        Assert.assertEquals(new Short((short)123), TypeUtils.getResolver(Short.class).apply("123"));
        Assert.assertEquals(new Integer(123), TypeUtils.getResolver(Integer.class).apply("123"));
        Assert.assertEquals(new Long(123), TypeUtils.getResolver(Long.class).apply("123"));
        Assert.assertEquals(new Float(123), TypeUtils.getResolver(Float.class).apply("123"));
        Assert.assertEquals(new Double(123), TypeUtils.getResolver(Double.class).apply("123"));
        Assert.assertEquals(new Character('x'), TypeUtils.getResolver(Character.class).apply("x"));
        Assert.assertEquals("qwe-123", TypeUtils.getResolver(String.class).apply("qwe-123"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getResolver_illegal_type() {
        TypeUtils.getResolver(Object.class);
    }
}