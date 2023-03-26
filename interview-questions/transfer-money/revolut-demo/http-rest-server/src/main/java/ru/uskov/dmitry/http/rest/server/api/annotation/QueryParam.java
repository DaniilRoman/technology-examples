package ru.uskov.dmitry.http.rest.server.api.annotation;

import java.lang.annotation.*;


@Documented
@Target(ElementType.PARAMETER)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface QueryParam {

    /**
     * Query parameter name
     */
    String value() default "";

    boolean requerd() default true;
}
