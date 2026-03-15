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
package io.github.photowey.mongoplus.autoconfigure.config;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.autoconfigure.core.engine.DefaultBeanFactoryMongoEngine;
import io.github.photowey.mongoplus.autoconfigure.core.enums.Command;
import io.github.photowey.mongoplus.autoconfigure.core.mapper.MongoMapperScannerConfigurer;
import io.github.photowey.mongoplus.autoconfigure.core.metadata.SpringDataEntityMetadataResolver;
import io.github.photowey.mongoplus.autoconfigure.core.property.MongoPlusProperties;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistry;
import io.github.photowey.mongoplus.core.id.IdentifyGenerator;
import io.github.photowey.mongoplus.core.id.SnowflakeIdGenerator;
import io.github.photowey.mongoplus.core.metadata.EntityMetadata;
import io.github.photowey.mongoplus.core.metadata.EntityMetadataResolver;
import io.github.photowey.mongoplus.core.metadata.EntityResolver;
import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.executor.AggregationExecutor;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.batch.BatchResult;
import io.github.photowey.mongoplus.executor.compiler.PipelineCompiler;
import io.github.photowey.mongoplus.executor.compiler.QueryCompiler;
import io.github.photowey.mongoplus.executor.getter.DefaultExecutorGetter;
import io.github.photowey.mongoplus.executor.getter.ExecutorGetter;
import io.github.photowey.mongoplus.executor.integration.engine.MongoEngine;
import io.github.photowey.mongoplus.executor.integration.service.DefaultQueryService;
import io.github.photowey.mongoplus.executor.integration.service.QueryService;
import io.github.photowey.mongoplus.executor.interceptor.InterceptableAggregationExecutor;
import io.github.photowey.mongoplus.executor.interceptor.InterceptableQueryExecutor;
import io.github.photowey.mongoplus.executor.listener.AutoInjectMongoIdEventListener;
import io.github.photowey.mongoplus.executor.template.impl.DefaultMongoTemplateAggregationExecutor;
import io.github.photowey.mongoplus.executor.template.impl.DefaultMongoTemplateExecutor;
import io.github.photowey.mongoplus.optimizer.QueryOptimizer;
import io.github.photowey.mongoplus.plugin.interceptor.InterceptorRegistry;
import io.github.photowey.mongoplus.plugin.interceptor.Invocation;
import io.github.photowey.mongoplus.plugin.interceptor.MongoPlusInterceptor;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.UpdateWrapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * AbstractMongoPlusConfigurationTest - Tests for executor, interceptor, and engine wiring.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
@ExtendWith({MockitoExtension.class, AllureJunit5.class})
@Epic("MongoPlus")
@Feature("AbstractMongoPlusConfigurationTest")
class AbstractMongoPlusConfigurationTest {

    @AfterEach
    void resetEntityResolver() {
        EntityResolver.resetEntityMetadataResolver();
        EntityResolver.clearCache();
    }

    @Test
    @DisplayName("Given empty interceptor registry, when creating queryExecutor, then default executor type is preserved")
    @Story("Empty interceptor registry preserves default query executor")
    void givenEmptyInterceptorRegistry_whenCreatingQueryExecutor_thenDefaultExecutorTypeIsPreserved()
        throws Exception {
        // Given
        AbstractMongoPlusConfiguration configuration = new AbstractMongoPlusConfiguration() {
        };
        QueryCompiler queryCompiler = new StubQueryCompiler();

        // When
        QueryExecutor queryExecutor = configuration.queryExecutor(
            null,
            queryCompiler,
            InterceptorRegistry.empty()
        );

        // Then
        assertTrue(queryExecutor instanceof DefaultMongoTemplateExecutor);
        assertSame(queryCompiler, this.readField(queryExecutor, "queryCompiler"));
    }

    @Test
    @DisplayName("Given non-empty interceptor registry, when creating queryExecutor, then decorator is applied")
    @Story("Non-empty interceptor registry applies decorator to query executor")
    void givenNonEmptyInterceptorRegistry_whenCreatingQueryExecutor_thenDecoratorIsApplied() {
        // Given
        AbstractMongoPlusConfiguration configuration = new AbstractMongoPlusConfiguration() {
        };
        MongoPlusInterceptor interceptor = invocation -> invocation.proceed();

        // When
        QueryExecutor queryExecutor = configuration.queryExecutor(
            null,
            new StubQueryCompiler(),
            new InterceptorRegistry(List.of(interceptor))
        );

        // Then
        assertTrue(queryExecutor instanceof InterceptableQueryExecutor);
    }

    @Test
    @DisplayName("Given empty interceptor registry, when creating aggregationExecutor, then default executor type is preserved")
    @Story("Empty interceptor registry preserves default aggregation executor")
    void givenEmptyInterceptorRegistry_whenCreatingAggregationExecutor_thenDefaultExecutorTypeIsPreserved()
        throws Exception {
        // Given
        AbstractMongoPlusConfiguration configuration = new AbstractMongoPlusConfiguration() {
        };
        PipelineCompiler pipelineCompiler = new StubPipelineCompiler();
        QueryOptimizer optimizer = new QueryOptimizer(false);

        // When
        AggregationExecutor aggregationExecutor = configuration.aggregationExecutor(
            optimizer,
            null,
            pipelineCompiler,
            InterceptorRegistry.empty()
        );

        // Then
        assertTrue(aggregationExecutor instanceof DefaultMongoTemplateAggregationExecutor);
        assertSame(pipelineCompiler, this.readField(aggregationExecutor, "pipelineCompiler"));
        assertSame(optimizer, this.readField(aggregationExecutor, "optimizer"));
    }

    @Test
    @DisplayName("Given non-empty interceptor registry, when creating aggregationExecutor, then decorator is applied")
    @Story("Non-empty interceptor registry applies decorator to aggregation executor")
    void givenNonEmptyInterceptorRegistry_whenCreatingAggregationExecutor_thenDecoratorIsApplied() {
        // Given
        AbstractMongoPlusConfiguration configuration = new AbstractMongoPlusConfiguration() {
        };
        MongoPlusInterceptor interceptor = invocation -> invocation.proceed();

        // When
        AggregationExecutor aggregationExecutor = configuration.aggregationExecutor(
            new QueryOptimizer(false),
            null,
            new StubPipelineCompiler(),
            new InterceptorRegistry(List.of(interceptor))
        );

        // Then
        assertTrue(aggregationExecutor instanceof InterceptableAggregationExecutor);
    }

    @Test
    @DisplayName(
        "Given queryExecutor, when creating queryService and mongoEngine, then engine exposes configured query service"
    )
    @Story("Engine exposes configured query service")
    void givenQueryExecutor_whenCreatingQueryServiceAndMongoEngine_thenEngineExposesConfiguredQueryService()
        throws Exception {
        // Given
        AbstractMongoPlusConfiguration configuration = new AbstractMongoPlusConfiguration() {
        };
        QueryExecutor queryExecutor = configuration.queryExecutor(
            null,
            new StubQueryCompiler(),
            InterceptorRegistry.empty()
        );

        // When
        QueryService queryService = configuration.queryService(queryExecutor);
        MongoEngine mongoEngine = configuration.mongoEngine();
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(beanFactory.getBean(QueryService.class)).thenReturn(queryService);
        ((BeanFactoryAware) mongoEngine).setBeanFactory(beanFactory);

        // Then
        assertTrue(queryService instanceof DefaultQueryService);
        assertTrue(configuration.mongoEngine() instanceof DefaultBeanFactoryMongoEngine);
        assertSame(queryExecutor, this.readField(queryService, "queryExecutor"));
        assertSame(queryService, mongoEngine.queryService());
    }

    @Test
    @DisplayName(
        "Given Spring metadata resolver, when installed through configuration, then EntityResolver uses Spring annotations"
    )
    @Story("Spring metadata resolver uses Spring annotations for entity resolution")
    void givenSpringMetadataResolver_whenInstalledThroughConfiguration_thenEntityResolverUsesSpringAnnotations() {
        // Given
        AbstractMongoPlusConfiguration configuration = new AbstractMongoPlusConfiguration() {
        };

        // When
        EntityMetadataResolver metadataResolver = configuration.entityMetadataResolver();
        configuration.entityMetadataResolverRegistrar(metadataResolver);
        EntityMetadata metadata = EntityResolver.resolve(AnnotatedEntity.class);

        // Then
        assertTrue(metadataResolver instanceof SpringDataEntityMetadataResolver);
        assertSame(metadataResolver, EntityResolver.getEntityMetadataResolver());
        assertEquals("annotated_users", metadata.getCollectionName());
        assertEquals("name_value", metadata.getColumnName("name"));
        assertEquals("identifier", metadata.getIdField());
        assertEquals(ID, metadata.getIdColumn());
    }

    @Test
    @DisplayName("Given interceptor beans, when creating registry bean, then registry stores them in order")
    @Story("Interceptor registry stores beans in order")
    void givenInterceptorBeans_whenCreatingRegistryBean_thenRegistryStoresThemInOrder() {
        // Given
        AbstractMongoPlusConfiguration configuration = new AbstractMongoPlusConfiguration() {
        };
        MongoPlusInterceptor slower = new OrderedInterceptor(10);
        MongoPlusInterceptor faster = new OrderedInterceptor(1);

        // When
        InterceptorRegistry registry = configuration.mongoPlusInterceptorRegistry(
            Optional.of(List.of(slower, faster))
        );

        // Then
        assertEquals(2, registry.getInterceptors().size());
        assertSame(faster, registry.getInterceptors().get(0));
        assertSame(slower, registry.getInterceptors().get(1));
    }

    @Test
    @DisplayName(
        "Given mapper and identity properties, when creating configuration beans, then returns "
            + "configured scanner id generator listener and defaults"
    )
    @Story("Configuration returns configured mapper and identity beans")
    void givenMapperAndIdentityProperties_whenCreatingConfigurationBeans_thenReturnsConfiguredBeans()
        throws Exception {
        // Given
        AbstractMongoPlusConfiguration configuration = new AbstractMongoPlusConfiguration() {
        };
        MongoPlusProperties properties = new MongoPlusProperties();
        properties.mapper().setBasePackages(List.of("alpha.pkg", "beta.pkg"));
        properties.mapper().setMarkerInterface(Runnable.class.getName());
        properties.mapper().setLazyInitialization("true");
        properties.mapper().setProcessPropertyPlaceHolders(true);
        properties.identity().setWorkerId(3L);
        properties.identity().setDataCenterId(4L);

        // When
        MongoMapperScannerConfigurer configurer = configuration.propertyMongoMapperScanConfigurer(
            configuration.beanNameGenerator(),
            properties
        );
        IdentifyGenerator identifyGenerator = configuration.identifyGenerator(properties);
        AutoInjectMongoIdEventListener listener = configuration.autoInjectMongoIdEventListener(identifyGenerator);
        ExecutorGetter getter = configuration.executorGetter(new StubQueryExecutor(), new StubAggregationExecutor());
        MethodHandlerRegistry registry = configuration.methodHandlerRegistry(
            Optional.of(
                List.of(
                    customRegistry -> customRegistry.register(
                        "customMethod",
                        Command.SELECT,
                        (g, c, a) -> "custom"
                    )
                )
            )
        );

        // Then
        assertEquals("alpha.pkg,beta.pkg", this.readField(configurer, "basePackage"));
        assertEquals("true", this.readField(configurer, "lazyInitialization"));
        assertEquals(true, this.readField(configurer, "processPropertyPlaceHolders"));
        assertEquals(Runnable.class, this.readField(configurer, "markerInterface"));
        assertSame(
            configuration.beanNameGenerator().getClass(),
            this.readField(configurer, "nameGenerator").getClass()
        );
        assertInstanceOf(SnowflakeIdGenerator.class, identifyGenerator);
        assertEquals(4L, this.readField(identifyGenerator, "dataCenterId"));
        assertEquals(3L, this.readField(identifyGenerator, "workerId"));
        assertSame(identifyGenerator, this.readField(listener, "identifyGenerator"));
        assertInstanceOf(DefaultBeanNameGenerator.class, configuration.beanNameGenerator());
        assertInstanceOf(DefaultExecutorGetter.class, getter);
        assertTrue(registry.getHandlers().containsKey("customMethod"));
    }

    private Object readField(Object target, String fieldName) throws Exception {
        Field field = this.findField(target.getClass(), fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private Field findField(Class<?> type, String fieldName) throws Exception {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }

        throw new NoSuchFieldException(fieldName);
    }

    private static final class StubQueryCompiler implements QueryCompiler {

        @Override
        public Query compile(AbstractWrapper<?> wrapper) {
            return new Query();
        }
    }

    private static final class StubPipelineCompiler implements PipelineCompiler {

        @Override
        public Aggregation compile(Pipeline pipeline) {
            return Aggregation.newAggregation();
        }
    }

    private static final class StubQueryExecutor implements QueryExecutor {

        @Override
        public <T> List<T> selectList(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            return List.of();
        }

        @Override
        public <T> T selectOne(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            return null;
        }

        @Override
        public long selectCount(AbstractWrapper<?> wrapper, String collectionName) {
            return 0;
        }

        @Override
        public <T> boolean exists(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            return false;
        }

        @Override
        public <T> boolean delete(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            return false;
        }

        @Override
        public <T> boolean update(
            UpdateWrapper<T> updateWrapper,
            Class<T> entityClass
        ) {
            return false;
        }

        @Override
        public <T> Page<T> selectPage(
            Page<T> page,
            AbstractWrapper<T> wrapper,
            Class<T> entityClass
        ) {
            return page;
        }

        @Override
        public <T> T selectById(Serializable id, Class<T> entityClass) {
            return null;
        }

        @Override
        public <T> boolean insert(T entity, String collectionName) {
            return false;
        }

        @Override
        public <T> boolean updateById(T entity, String collectionName) {
            return false;
        }

        @Override
        public boolean deleteById(Serializable id, String collectionName) {
            return false;
        }

        @Override
        public <T> BatchResult insertBatch(
            List<T> entities,
            String collectionName
        ) {
            return null;
        }

        @Override
        public <T> BatchResult insertBatch(
            List<T> entities,
            Class<T> entityClass
        ) {
            return null;
        }

        @Override
        public <T> BatchResult updateBatch(
            List<T> entities,
            String collectionName
        ) {
            return null;
        }

        @Override
        public <T> BatchResult updateBatch(
            List<T> entities,
            Class<T> entityClass
        ) {
            return null;
        }

        @Override
        public BatchResult deleteBatch(
            List<? extends Serializable> ids,
            String collectionName
        ) {
            return null;
        }

        @Override
        public <T> BatchResult deleteBatch(
            List<? extends Serializable> ids,
            Class<T> entityClass
        ) {
            return null;
        }
    }

    private static final class StubAggregationExecutor implements AggregationExecutor {

        @Override
        public <T, R> List<R> execute(Pipeline pipeline, Class<T> inputType, Class<R> outputType) {
            return List.of();
        }

        @Override
        public <R> List<R> execute(Pipeline pipeline, String collectionName, Class<R> outputType) {
            return List.of();
        }

        @Override
        public <T, R> List<R> executeRaw(Pipeline pipeline, Class<T> inputType, Class<R> outputType) {
            return List.of();
        }
    }

    private static final class OrderedInterceptor implements MongoPlusInterceptor {

        private final int order;

        private OrderedInterceptor(int order) {
            this.order = order;
        }

        @Override
        public int getOrder() {
            return this.order;
        }

        @Override
        public Object intercept(Invocation invocation)
            throws Throwable {
            return invocation.proceed();
        }
    }

    @Document(collection = "annotated_users")
    private static final class AnnotatedEntity {

        @Id
        private String identifier;

        @org.springframework.data.mongodb.core.mapping.Field("name_value")
        private String name;
    }
}
