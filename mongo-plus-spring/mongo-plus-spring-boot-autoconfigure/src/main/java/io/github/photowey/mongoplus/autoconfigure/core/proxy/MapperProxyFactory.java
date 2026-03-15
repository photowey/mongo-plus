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

import java.util.function.Consumer;

import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistry;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.executor.getter.ExecutorGetter;
import io.github.photowey.mongoplus.mapper.MongoMapper;
import io.github.photowey.mongoplus.plugin.interceptor.InterceptorRegistry;

/**
 * MapperProxyFactory - Factory for creating MapperProxy instances.
 * Supports customization of method handlers.
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class MapperProxyFactory<T> {

    private final Class<T> entityClass;
    private final ExecutorGetter getter;
    private final MethodHandlerRegistry registry;
    private final InterceptorRegistry interceptorRegistry;

    private Consumer<MethodHandlerRegistry> handlerCustomizer;

    public MapperProxyFactory(
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

    /**
     * Set custom handler registry configurator.
     *
     * @param customizer the customizer to apply
     * @return this factory
     */
    public MapperProxyFactory<T> withHandlers(Consumer<MethodHandlerRegistry> customizer) {
        this.handlerCustomizer = customizer;
        return this;
    }

    /**
     * Create a new MapperProxy instance.
     *
     * @return the mapper proxy
     */
    public MapperProxy<T> createProxy() {
        if (Objects.nonNull(this.handlerCustomizer)) {
            this.handlerCustomizer.accept(this.registry);
        }

        return new MapperProxy<>(
            this.entityClass,
            this.getter,
            this.registry,
            this.interceptorRegistry
        );
    }

    /**
     * Create proxy for any interface (e.g. annotation-only mapper without MongoMapper).
     *
     * @param mapperInterface the mapper interface class
     * @return the proxy instance
     */
    public Object createProxy(Class<?> mapperInterface) {
        return this.createProxy().createProxy(mapperInterface);
    }

    /**
     * Create proxy and return as mapper interface.
     *
     * @param mapperInterface the mapper interface class
     * @return the proxy instance
     */
    @Deprecated
    public <M extends MongoMapper<T>> M createMapperProxy(Class<M> mapperInterface) {
        return this.createProxy().createMapperProxy(mapperInterface);
    }
}
