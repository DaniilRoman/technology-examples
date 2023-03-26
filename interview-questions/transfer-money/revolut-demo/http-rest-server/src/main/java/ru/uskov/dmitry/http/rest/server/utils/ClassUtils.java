package ru.uskov.dmitry.http.rest.server.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ClassUtils {

    public static String getParameterDescription(@NotNull Method method, int parameterIndex) {
        if (parameterIndex < 0) {
            throw new IllegalArgumentException("parameterIndex must be above zero. Actual value: " + parameterIndex);
        }
        Parameter parameter = method.getParameters()[parameterIndex];
        if (parameter.isNamePresent()) {
            return String.format("parameter '%s' in method %s.%s",
                    parameter.getName(),
                    method.getDeclaringClass().getName(),
                    method.getName()
            );
        } else {
            return String.format("%d-parameter in method %s.%s",
                    parameterIndex,
                    method.getDeclaringClass().getName(),
                    method.getName()
            );
        }
    }

}
