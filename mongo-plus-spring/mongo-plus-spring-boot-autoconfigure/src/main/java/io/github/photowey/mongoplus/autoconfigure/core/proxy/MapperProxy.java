/*
 * Copyright (c) 2026-present The MongoPlus Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.photowey.mongoplus.autoconfigure.core.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistry;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.method.MapperMethod;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.executor.getter.ExecutorGetter;
import io.github.photowey.mongoplus.mapper.MongoMapper;
import io.github.photowey.mongoplus.plugin.context.MapperInvocationContext;
import io.github.photowey.mongoplus.plugin.interceptor.InterceptorRegistry;

/**
 * MapperProxy - Dynamic proxy for MongoMapper interface.
 * Uses MethodHandlerRegistry for extensible method dispatch.
 *
 * <p>Default methods in MongoMapper are invoked directly without going through the registry.</p>
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class MapperProxy<T> implements InvocationHandler {

    private final Class<T> entityClass;
    private final ExecutorGetter getter;
    private final MethodHandlerRegistry registry;
    private final InterceptorRegistry interceptorRegistry;

    public MapperProxy(
        Class<T> entityClass,
        ExecutorGetter getter,
        MethodHandlerRegistry registry,
        InterceptorRegistry interceptorRegistry
    ) {
        this.entityClass = entityClass;
        this.getter = getter;
        this.registry = registry;
        this.interceptorRegistry = Objects.nonNull(interceptorRegistry)
            ? interceptorRegistry
            : InterceptorRegistry.empty();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        if (method.isDefault()) {
            return this.invokeDefaultMethod(proxy, method, args);
        }

        MapperMethod mapperMethod = this.registry.getMapperMethod(method);
        if (Objects.nonNull(mapperMethod) && Objects.nonNull(mapperMethod.handler())) {
            Object[] actualArguments = Objects.nonNull(args) ? args : new Object[0];

            return this.interceptorRegistry.invoke(
                MapperInvocationContext.builder()
                    .entityClass(this.entityClass)
                    .mapperMethod(method)
                    .commandName(
                        Objects.nonNull(mapperMethod.command())
                            ? mapperMethod.command().name()
                            : null
                    )
                    .arguments(actualArguments)
                    .build(),
                () -> mapperMethod.handler().execute(this.getter, this.entityClass, actualArguments)
            );
        }

        throw new UnsupportedOperationException("Method not implemented: " + method.getName());
    }

    /**
     * Invoke default method using MethodHandles.
     */
    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(declaringClass, MethodHandles.lookup());

        MethodHandle methodHandle = lookup.findSpecial(
            declaringClass,
            method.getName(),
            MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
            declaringClass
        );

        return methodHandle
            .bindTo(proxy)
            .invokeWithArguments(args);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public <M extends MongoMapper<T>> M createMapperProxy(Class<M> mapperInterface) {
        return (M) this.createProxy(mapperInterface);
    }

    /**
     * Create proxy for any mapper interface (e.g. annotation-only, no MongoMapper extend).
     *
     * @param mapperInterface the mapper interface class
     * @return the proxy instance
     */
    public Object createProxy(Class<?> mapperInterface) {
        return Proxy.newProxyInstance(
            mapperInterface.getClassLoader(),
            new Class<?>[] {mapperInterface},
            this
        );
    }
}
