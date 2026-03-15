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

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistry;
import io.github.photowey.mongoplus.executor.AggregationExecutor;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.getter.ExecutorGetter;
import io.github.photowey.mongoplus.mapper.MongoMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MapperProxyFactoryTest - Unit tests for MapperProxyFactory proxy creation APIs.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/13
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("MapperProxyFactoryTest")
class MapperProxyFactoryTest {

    @Test
    @DisplayName(
        "Given a handler customizer, when creating a mapper proxy, then applies the "
            + "customizer before creation"
    )
    @Story("Handler customizer applied before mapper proxy creation")
    void givenAHandlerCustomizer_whenCreatingAMapperProxy_thenAppliesTheCustomizerBeforeCreation() {
        // Given
        AtomicInteger customizerCalls = new AtomicInteger();
        MapperProxyFactory<Object> factory = this.factory()
            .withHandlers(registry -> customizerCalls.incrementAndGet());

        // When
        MapperProxy<Object> mapperProxy = factory.createProxy();

        // Then
        assertNotNull(mapperProxy);
        assertEquals(1, customizerCalls.get());
    }

    @Test
    @DisplayName("Given mapper interfaces, when creating typed and generic proxies, then returns JDK proxy instances")
    @Story("Mapper proxy factory returns JDK proxy instances for typed and generic mappers")
    void givenMapperInterfaces_whenCreatingTypedAndGenericProxies_thenReturnsJdkProxyInstances() {
        // Given
        MapperProxyFactory<Object> factory = this.factory();

        // When
        TypedMapper typedProxy = factory.createMapperProxy(TypedMapper.class);
        Object annotationOnlyProxy = factory.createProxy(AnnotationOnlyMapper.class);

        // Then
        assertTrue(Proxy.isProxyClass(typedProxy.getClass()));
        assertTrue(typedProxy instanceof TypedMapper);
        assertTrue(Proxy.isProxyClass(annotationOnlyProxy.getClass()));
        assertTrue(annotationOnlyProxy instanceof AnnotationOnlyMapper);
    }

    private MapperProxyFactory<Object> factory() {
        return new MapperProxyFactory<>(
            Object.class,
            new EmptyExecutorGetter(),
            new MethodHandlerRegistry(),
            null
        );
    }

    private interface TypedMapper extends MongoMapper<Object> {
    }

    private interface AnnotationOnlyMapper {
    }

    private static final class EmptyExecutorGetter implements ExecutorGetter {

        @Override
        public QueryExecutor queryExecutor() {
            return null;
        }

        @Override
        public AggregationExecutor aggregationExecutor() {
            return null;
        }
    }
}
