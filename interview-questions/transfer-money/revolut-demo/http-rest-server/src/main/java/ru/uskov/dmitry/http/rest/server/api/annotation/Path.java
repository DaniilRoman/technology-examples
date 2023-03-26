package ru.uskov.dmitry.http.rest.server.api.annotation;

import ru.uskov.dmitry.http.rest.server.api.HttpMethod;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(value= RetentionPolicy.RUNTIME)
public @interface Path {

    String value();

    HttpMethod method() default HttpMethod.NULL;

}
