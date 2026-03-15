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

import java.lang.reflect.Proxy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistry;
import io.github.photowey.mongoplus.executor.AggregationExecutor;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.getter.ExecutorGetter;
import io.github.photowey.mongoplus.mapper.MongoMapper;
import io.github.photowey.mongoplus.plugin.interceptor.InterceptorRegistry;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MapperFactoryBeanTest - Unit tests for MapperFactoryBean proxy creation behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/13
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("MapperFactoryBeanTest")
class MapperFactoryBeanTest {

    @Test
    @DisplayName(
        "Given mapper interfaces, when creating objects, then returns JDK proxies for "
            + "typed and annotation-only mappers"
    )
    @Story("Mapper interfaces create JDK proxies for typed and annotation-only mappers")
    void givenMapperInterfaces_whenCreatingObjects_thenReturnsJdkProxiesForTypedAndAnnotationOnlyMappers()
        throws Exception {
        // Given
        MapperFactoryBean typedFactoryBean = this.factoryBean(TypedMapper.class);
        MapperFactoryBean annotationOnlyFactoryBean = this.factoryBean(AnnotationOnlyMapper.class);

        // When
        Object typedProxy = typedFactoryBean.getObject();
        Object annotationOnlyProxy = annotationOnlyFactoryBean.getObject();

        // Then
        assertEquals(TypedMapper.class, typedFactoryBean.getObjectType());
        assertTrue(Proxy.isProxyClass(typedProxy.getClass()));
        assertTrue(typedProxy instanceof TypedMapper);
        assertEquals(AnnotationOnlyMapper.class, annotationOnlyFactoryBean.getObjectType());
        assertTrue(Proxy.isProxyClass(annotationOnlyProxy.getClass()));
        assertTrue(annotationOnlyProxy instanceof AnnotationOnlyMapper);
    }

    private MapperFactoryBean factoryBean(Class<?> mapperInterface) {
        MapperFactoryBean factoryBean = new MapperFactoryBean(mapperInterface);
        factoryBean.setExecutorGetter(new EmptyExecutorGetter());
        factoryBean.setMethodHandlerRegistry(new MethodHandlerRegistry());
        factoryBean.setMongoPlusInterceptorRegistry(InterceptorRegistry.empty());
        return factoryBean;
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
