package ru.uskov.dmitry.http.rest.server.impl.route;

import org.jetbrains.annotations.NotNull;
import ru.uskov.dmitry.http.rest.server.api.annotation.Body;
import ru.uskov.dmitry.http.rest.server.api.annotation.PathVariable;
import ru.uskov.dmitry.http.rest.server.api.annotation.QueryParam;
import ru.uskov.dmitry.http.rest.server.api.annotation.QueryParams;
import ru.uskov.dmitry.http.rest.server.impl.marshaller.Marshaller;
import ru.uskov.dmitry.http.rest.server.impl.resolvers.*;
import ru.uskov.dmitry.http.rest.server.utils.ClassUtils;
import ru.uskov.dmitry.http.rest.server.utils.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RequestExecutorFactory {

    private final Map<Class<? extends Annotation>, Function<ResolverArguments, MethodArgumentResolver<?>>> resolvedAnnotation;

    {
        Map<Class<? extends Annotation>, Function<ResolverArguments, MethodArgumentResolver<?>>> tmp = new HashMap<>();
        tmp.put(Body.class, this::createBodyArgumentResolver);
        tmp.put(PathVariable.class, this::createPathVariableResolver);
        tmp.put(QueryParam.class, this::createQueryParamResolver);
        tmp.put(QueryParams.class, this::createQueryParamsResolver);
        resolvedAnnotation = Collections.unmodifiableMap(tmp);
    }

    public RequestExecutor createExecutor(Object instance, @NotNull Method declaredMethod, @NotNull Marshaller marshaller, @NotNull String absolutePath) {
        Parameter[] typeParameters = declaredMethod.getParameters();

        MethodArgumentResolver<?>[] resolvers = new MethodArgumentResolver[typeParameters.length];
        for (int paramIndex = 0; paramIndex < typeParameters.length; paramIndex++) {
            Class<? extends Annotation> foundedResolveAnnotation = null;
            for (Annotation annotation : typeParameters[paramIndex].getAnnotations()) {
                if (isResolvedAnnotation(annotation)) {
                    if (foundedResolveAnnotation != null) {
                        throw new IllegalArgumentException(ClassUtils.getParameterDescription(declaredMethod, paramIndex) + "has more then one resolved annotation: " +
                                foundedResolveAnnotation.getName() + ", " + annotation.annotationType().getName());
                    }
                    foundedResolveAnnotation = annotation.annotationType();
                    resolvers[paramIndex] = createAnnotationResolver(annotation, new ResolverArguments(declaredMethod, paramIndex, marshaller, absolutePath));
                }
            }
            if (foundedResolveAnnotation == null) {
                resolvers[paramIndex] = createDefaultResolver(declaredMethod, paramIndex);
            }
        }
        return new RequestExecutorImpl(instance, declaredMethod, marshaller, resolvers);
    }

    private MethodArgumentResolver<?> createDefaultResolver(Method declaredMethod, int parameterIndex) {
        return new DefaultValueResolver<>(declaredMethod.getParameterTypes()[parameterIndex]);
    }

    private MethodArgumentResolver<?> createAnnotationResolver(Annotation annotation, ResolverArguments arguments) {
        return resolvedAnnotation.get(annotation.annotationType()).apply(arguments);
    }

    private boolean isResolvedAnnotation(Annotation annotation) {
        return resolvedAnnotation.containsKey(annotation.annotationType());
    }

    private MethodArgumentResolver<?> createBodyArgumentResolver(ResolverArguments arguments) {
        return new BodyResolver<>(arguments.marshaller, arguments.declaredMethod.getParameterTypes()[arguments.parameterIndex]);
    }

    private MethodArgumentResolver<?> createPathVariableResolver(ResolverArguments arguments) {
        Parameter parameter = arguments.declaredMethod.getParameters()[arguments.parameterIndex];
        Class<?> parameterType = arguments.declaredMethod.getParameterTypes()[arguments.parameterIndex];
        if (!TypeUtils.isSimpleType(parameterType)) {
            throw new IllegalArgumentException("Parameter annotated PathVariable must be primitive or one of this type: " + TypeUtils.getSimpleTypes());
        }

        PathVariable annotation = parameter.getAnnotation(PathVariable.class);
        String paramName = annotation.value();
        if (paramName.trim().equals("")) {
            if (!parameter.isNamePresent()) {
                throw new IllegalArgumentException("Annotation PathVariable for " + ClassUtils.getParameterDescription(arguments.declaredMethod, arguments.parameterIndex) + " doesn't contain path variable name");
            } else {
                paramName = parameter.getName();
            }
        }
        return new PathVariableResolver<>(arguments.absolutePath, paramName, parameterType);
    }

    private MethodArgumentResolver<?> createQueryParamResolver(ResolverArguments arguments) {
        Parameter parameter = arguments.declaredMethod.getParameters()[arguments.parameterIndex];
        Class<?> parameterType = arguments.declaredMethod.getParameterTypes()[arguments.parameterIndex];
        if (!TypeUtils.isSimpleType(parameterType)) {
            throw new IllegalArgumentException("Parameter annotated QueryParam must be primitive or one of this type: " + TypeUtils.getSimpleTypes());
        }
        QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
        String paramName = queryParam.value();
        if (paramName.trim().equals("")) {
            if (!parameter.isNamePresent()) {
                throw new IllegalArgumentException("Annotation QueryParam for " + ClassUtils.getParameterDescription(arguments.declaredMethod, arguments.parameterIndex) + " doesn't contain query parameter name");
            } else {
                paramName = parameter.getName();
            }
        }
        return new QueryParamResolver<>(paramName, parameterType, queryParam.requerd());
    }

    private MethodArgumentResolver<?> createQueryParamsResolver(ResolverArguments arguments) {
        Class<?> parameterType = arguments.declaredMethod.getParameterTypes()[arguments.parameterIndex];
        if (!parameterType.equals(Map.class)) {
            throw new IllegalArgumentException("parameter annotated ru.uskov.dmitry.http.rest.server.api.annotation.QueryParams must be Map<String, String>");
        }
        ParameterizedType parameter = (ParameterizedType) arguments.declaredMethod.getParameters()[arguments.parameterIndex].getParameterizedType();

        if (!String.class.equals(parameter.getActualTypeArguments()[0]) || !String.class.equals(parameter.getActualTypeArguments()[2])) {
            throw new IllegalArgumentException("parameter annotated ru.uskov.dmitry.http.rest.server.api.annotation.QueryParams must be Map<String, String>");
        }
        return new QueryParamsResolver();
    }

    private static final class ResolverArguments {

        public ResolverArguments(Method declaredMethod, int parameterIndex, Marshaller marshaller, String absolutePath) {
            this.declaredMethod = declaredMethod;
            this.parameterIndex = parameterIndex;
            this.marshaller = marshaller;
            this.absolutePath = absolutePath;
        }

        Method declaredMethod;
        int parameterIndex;
        Marshaller marshaller;
        String absolutePath;
    }

}
