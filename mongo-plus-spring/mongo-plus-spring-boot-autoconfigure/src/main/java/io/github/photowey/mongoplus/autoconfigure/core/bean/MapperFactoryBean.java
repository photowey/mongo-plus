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
package io.github.photowey.mongoplus.autoconfigure.core.bean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.photowey.mongoplus.autoconfigure.core.proxy.MapperProxyFactory;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistry;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.executor.getter.ExecutorGetter;
import io.github.photowey.mongoplus.mapper.util.Mappers;
import io.github.photowey.mongoplus.plugin.interceptor.InterceptorRegistry;

/**
 * MapperFactoryBean - Spring FactoryBean that creates MongoMapper proxy.
 * Supports both MongoMapper-extending interfaces (with entity) and annotation-only
 * interfaces (no entity, no CRUD methods; discovered by &#64;MongoMapper only).
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class MapperFactoryBean implements FactoryBean<Object> {

    private final Class<?> mapperInterface;
    private final Class<?> entityClass;

    private ExecutorGetter executorGetter;
    private MethodHandlerRegistry methodHandlerRegistry;
    private InterceptorRegistry interceptorRegistry;

    public MapperFactoryBean(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
        Class<?> resolved = Mappers.resolve(mapperInterface);
        this.entityClass = Objects.nonNull(resolved) ? resolved : Object.class;
    }

    @Override
    public Object getObject() {
        MapperProxyFactory<?> factory = new MapperProxyFactory<>(
            (Class<?>) this.entityClass,
            this.executorGetter,
            this.methodHandlerRegistry,
            this.interceptorRegistry
        );
        return factory.createProxy(this.mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }

    @Autowired
    public void setExecutorGetter(ExecutorGetter executorGetter) {
        this.executorGetter = executorGetter;
    }

    @Autowired
    public void setMethodHandlerRegistry(MethodHandlerRegistry methodHandlerRegistry) {
        this.methodHandlerRegistry = methodHandlerRegistry;
    }

    @Autowired
    public void setMongoPlusInterceptorRegistry(InterceptorRegistry interceptorRegistry) {
        this.interceptorRegistry = interceptorRegistry;
    }
}
