package ru.uskov.dmitry.http.rest.server.api.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface PathVariable {
    String value() default "";
}
