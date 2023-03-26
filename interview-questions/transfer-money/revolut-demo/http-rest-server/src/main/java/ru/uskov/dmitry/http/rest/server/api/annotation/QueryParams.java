package ru.uskov.dmitry.http.rest.server.api.annotation;

import java.lang.annotation.*;


/**
 * Shuld be used only for {@link java.util.Map}
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface QueryParams {
}
