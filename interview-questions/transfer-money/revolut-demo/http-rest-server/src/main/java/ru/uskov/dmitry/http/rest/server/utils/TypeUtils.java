package ru.uskov.dmitry.http.rest.server.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class TypeUtils {

    private static final Map<Class, Object> defaultPrimitives;
    private static final Set<Class> simpleTypes;

    static {
        Map tmp = new HashMap();
        tmp.put(boolean.class, false);
        tmp.put(byte.class, (byte) 0);
        tmp.put(short.class, (short) 0);
        tmp.put(int.class, 0);
        tmp.put(long.class, 0L);
        tmp.put(float.class, 0f);
        tmp.put(double.class, 0d);
        tmp.put(char.class, '\u0000');
        defaultPrimitives = Collections.unmodifiableMap(tmp);
    }

    static {
        Set<Class> tmp = new HashSet<>();
        tmp.add(Boolean.class);
        tmp.add(Byte.class);
        tmp.add(Short.class);
        tmp.add(Integer.class);
        tmp.add(Long.class);
        tmp.add(Float.class);
        tmp.add(Double.class);
        tmp.add(Character.class);
        tmp.add(String.class);
        simpleTypes = Collections.unmodifiableSet(tmp);
    }


    /**
     * Get default value for {@code type}
     *
     * @return default value for type
     * @throws IllegalArgumentException if {@code type} is null
     */
    public static <T> T getDefault(@NotNull Class<T> type) {
        if (type.isPrimitive()) {
            return (T) defaultPrimitives.get(type);
        } else {
            return null;
        }
    }

    /**
     * Return {@code true}, if {@code type} is primitive, boxing type or {@link String}
     *
     * @throws IllegalArgumentException if {@code type} is null
     */
    public static boolean isSimpleType(@NotNull Class<?> type) {
        if (type.isPrimitive()) {
            return true;
        }
        return simpleTypes.contains(type);
    }

    public static Set<Class> getSimpleTypes() {
        return simpleTypes;
    }


    public static <T> Function<String, T> getResolver(Class<T> argumentType) {

        if (argumentType.equals(String.class)) {
            return s -> (T) s;
        }
        if (argumentType.equals(boolean.class) || argumentType.equals(Boolean.class)) {
            return s -> (T) Boolean.valueOf(s);
        }
        if (argumentType.equals(byte.class) || argumentType.equals(Byte.class)) {
            return s-> (T) Byte.valueOf(s);
        }
        if (argumentType.equals(short.class) || argumentType.equals(Short.class)) {
            return s-> (T) Short.valueOf(s);
        }
        if (argumentType.equals(int.class) || argumentType.equals(Integer.class)) {
            return s-> (T) Integer.valueOf(s);
        }
        if (argumentType.equals(long.class) || argumentType.equals(Long.class)) {
            return s-> (T) Long.valueOf(s);
        }
        if (argumentType.equals(float.class) || argumentType.equals(Float.class)) {
            return s-> (T) Float.valueOf(s);
        }
        if (argumentType.equals(double.class) || argumentType.equals(Double.class)) {
            return s-> (T) Double.valueOf(s);
        }
        if (argumentType.equals(char.class) || argumentType.equals(Character.class)) {
            return s -> {
                s = s.trim();
                if (s.isEmpty()) {
                    return (T) new Character(' ');
                }
                return (T) new Character(s.charAt(0));
            };
        }
        throw new IllegalArgumentException("Could not resolve type '" + argumentType + "'");
    }
}
