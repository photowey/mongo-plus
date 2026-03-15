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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import io.github.photowey.mongoplus.annotation.MongoMapper;
import io.github.photowey.mongoplus.autoconfigure.core.bean.MapperFactoryBean;
import io.github.photowey.mongoplus.autoconfigure.core.binder.PropertyBinders;
import io.github.photowey.mongoplus.autoconfigure.core.engine.DefaultBeanFactoryMongoEngine;
import io.github.photowey.mongoplus.autoconfigure.core.mapper.MongoMapperScannerConfigurer;
import io.github.photowey.mongoplus.autoconfigure.core.metadata.SpringDataEntityMetadataResolver;
import io.github.photowey.mongoplus.autoconfigure.core.property.MongoPlusProperties;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistry;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistryCustomizer;
import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.github.photowey.mongoplus.core.id.IdentifyGenerator;
import io.github.photowey.mongoplus.core.id.SnowflakeIdGenerator;
import io.github.photowey.mongoplus.core.metadata.EntityMetadataResolver;
import io.github.photowey.mongoplus.core.metadata.EntityResolver;
import io.github.photowey.mongoplus.core.util.ClassUtils;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.executor.AggregationExecutor;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.compiler.PipelineCompiler;
import io.github.photowey.mongoplus.executor.compiler.QueryCompiler;
import io.github.photowey.mongoplus.executor.compiler.impl.MongoPipelineCompiler;
import io.github.photowey.mongoplus.executor.compiler.impl.MongoQueryCompiler;
import io.github.photowey.mongoplus.executor.getter.DefaultExecutorGetter;
import io.github.photowey.mongoplus.executor.getter.ExecutorGetter;
import io.github.photowey.mongoplus.executor.integration.engine.MongoEngine;
import io.github.photowey.mongoplus.executor.integration.service.DefaultQueryService;
import io.github.photowey.mongoplus.executor.integration.service.QueryService;
import io.github.photowey.mongoplus.executor.interceptor.InterceptableAggregationExecutor;
import io.github.photowey.mongoplus.executor.interceptor.InterceptableQueryExecutor;
import io.github.photowey.mongoplus.executor.listener.AutoInjectMongoIdEventListener;
import io.github.photowey.mongoplus.executor.template.MongoTemplateAggregationExecutor;
import io.github.photowey.mongoplus.executor.template.MongoTemplateExecutor;
import io.github.photowey.mongoplus.executor.template.impl.DefaultMongoTemplateAggregationExecutor;
import io.github.photowey.mongoplus.executor.template.impl.DefaultMongoTemplateExecutor;
import io.github.photowey.mongoplus.optimizer.QueryOptimizer;
import io.github.photowey.mongoplus.plugin.interceptor.InterceptorRegistry;
import io.github.photowey.mongoplus.plugin.interceptor.MongoPlusInterceptor;

/**
 * AbstractMongoPlusConfiguration - Abstract configuration for MongoPlus.
 * Contains all bean definitions shared between Spring Boot 2.x and 3.x.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ConditionalOnProperty(
    prefix = MongoPlusConstants.Configuration.MONGO_PLUS_PREFIX,
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public abstract class AbstractMongoPlusConfiguration {

    /**
     * Register MongoPlusProperties and bind from prefix "spring.data.mongodb.mongoplus".
     * Uses Binder explicitly so the instance is bound from Environment; annotation-based
     * binding may not run on @Bean-returned instances in some setups.
     */
    @Bean
    @ConditionalOnMissingBean(MongoPlusProperties.class)
    public MongoPlusProperties mongoPlusProperties(Environment environment) {
        String prefix = MongoPlusConstants.Configuration.determineMongoplusPropertyPrefix();
        return PropertyBinders.bind(environment, prefix, MongoPlusProperties.class);
    }

    // ----------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean(EntityMetadataResolver.class)
    public EntityMetadataResolver entityMetadataResolver() {
        EntityMetadataResolver resolver = new SpringDataEntityMetadataResolver();
        EntityResolver.setEntityMetadataResolver(resolver);
        return resolver;
    }

    /**
     * Ensures the {@link EntityResolver} static resolver is kept in sync
     * with the actual Spring bean, even when a user-provided
     * {@link EntityMetadataResolver} overrides the default.
     *
     * <p>This replaces the former {@code @Autowired} setter approach,
     * which is unreliable on {@code @AutoConfiguration} classes
     * (Spring Boot 2.7+) where setter injection may not be processed.
     */
    @Bean
    public SmartInitializingSingleton entityMetadataResolverRegistrar(EntityMetadataResolver resolver) {
        return () -> EntityResolver.setEntityMetadataResolver(resolver);
    }

    // ----------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean(QueryOptimizer.class)
    public QueryOptimizer queryOptimizer() {
        return new QueryOptimizer();
    }

    @Bean
    @ConditionalOnMissingBean(QueryCompiler.class)
    public QueryCompiler queryCompiler() {
        return new MongoQueryCompiler();
    }

    @Bean
    @ConditionalOnMissingBean(PipelineCompiler.class)
    public PipelineCompiler pipelineCompiler() {
        return new MongoPipelineCompiler();
    }

    @Bean
    @ConditionalOnMissingBean(InterceptorRegistry.class)
    public InterceptorRegistry mongoPlusInterceptorRegistry(
        Optional<List<MongoPlusInterceptor>> interceptors
    ) {
        return new InterceptorRegistry(interceptors.orElse(Collections.emptyList()));
    }

    // ----------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean(QueryExecutor.class)
    public QueryExecutor queryExecutor(
        MongoTemplate mongoTemplate,
        QueryCompiler queryCompiler,
        InterceptorRegistry interceptorRegistry
    ) {
        MongoTemplateExecutor executor = new DefaultMongoTemplateExecutor(queryCompiler);
        executor.setMongoTemplate(mongoTemplate);
        if (interceptorRegistry.isEmpty()) {
            return executor;
        }

        return new InterceptableQueryExecutor(executor, interceptorRegistry);
    }

    @Bean
    @ConditionalOnMissingBean(AggregationExecutor.class)
    public AggregationExecutor aggregationExecutor(
        QueryOptimizer optimizer,
        MongoTemplate mongoTemplate,
        PipelineCompiler pipelineCompiler,
        InterceptorRegistry interceptorRegistry
    ) {
        MongoTemplateAggregationExecutor executor =
            new DefaultMongoTemplateAggregationExecutor(pipelineCompiler, optimizer);
        executor.setMongoTemplate(mongoTemplate);
        if (interceptorRegistry.isEmpty()) {
            return executor;
        }

        return new InterceptableAggregationExecutor(executor, interceptorRegistry);
    }

    // ----------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean(ExecutorGetter.class)
    public ExecutorGetter executorGetter(QueryExecutor queryExecutor, AggregationExecutor aggregationExecutor) {
        return new DefaultExecutorGetter(queryExecutor, aggregationExecutor);
    }

    @Bean
    @ConditionalOnMissingBean(MethodHandlerRegistry.class)
    public MethodHandlerRegistry methodHandlerRegistry(Optional<List<MethodHandlerRegistryCustomizer>> customizers) {
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        customizers.orElse(Collections.emptyList())
            .forEach(it -> it.customize(registry));

        return registry;
    }

    // ----------------------------------------------------------------

    /**
     * Default bean name generator for mapper scanning.
     * Can be replaced by defining a custom BeanNameGenerator bean.
     *
     * @return the default {@link BeanNameGenerator}
     */
    @Bean
    @ConditionalOnMissingBean(BeanNameGenerator.class)
    public BeanNameGenerator beanNameGenerator() {
        return new DefaultBeanNameGenerator();
    }

    /**
     * MongoMapper scan configurer for property-based mapper scanning.
     * Only created when base-packages are configured in application.yml.
     *
     * @param nameGenerator the bean name generator (default from beanNameGenerator() when not customized)
     * @return the mapper scan configurer
     */
    @Bean
    @ConditionalOnProperty(
        prefix = MongoPlusConstants.Configuration.MONGO_PLUS_MAPPER_PREFIX,
        name = "base-packages"
    )
    public MongoMapperScannerConfigurer propertyMongoMapperScanConfigurer(
        BeanNameGenerator nameGenerator,
        MongoPlusProperties mongoPlusProperties
    ) {
        MongoPlusProperties.Mapper mapper = mongoPlusProperties.mapper();
        List<String> basePackages = mapper.basePackages();
        MongoMapperScannerConfigurer configurer = new MongoMapperScannerConfigurer();
        configurer.setBasePackage(String.join(",", basePackages));
        configurer.setLazyInitialization(mapper.lazyInitialization());
        configurer.setAnnotationClass(MongoMapper.class);

        Class<?> markerInterface = ClassUtils.forName(mapper.markerInterface());
        configurer.setMarkerInterface(markerInterface);
        configurer.setMapperFactoryBeanClass(MapperFactoryBean.class);

        configurer.setNameGenerator(nameGenerator);
        configurer.setProcessPropertyPlaceHolders(mapper.processPropertyPlaceHolders());

        return configurer;
    }

    // ----------------------------------------------------------------

    /**
     * Default Snowflake ID generator.
     * Can be replaced by defining a custom IdentifyGenerator bean.
     *
     * @return the default IdentifyGenerator
     */
    @Bean
    @ConditionalOnClass({IdentifyGenerator.class, AutoInjectMongoIdEventListener.class})
    @ConditionalOnMissingBean(IdentifyGenerator.class)
    @ConditionalOnProperty(
        prefix = MongoPlusConstants.Configuration.MONGO_PLUS_IDENTITY_PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
    )
    public IdentifyGenerator identifyGenerator(MongoPlusProperties mongoPlusProperties) {
        MongoPlusProperties.Identity identity = mongoPlusProperties.identity();
        Long workerId = identity.workerId();
        Long dataCenterId = identity.dataCenterId();

        if (Objects.nonNull(workerId) && Objects.nonNull(dataCenterId)) {
            return new SnowflakeIdGenerator(dataCenterId, workerId);
        }

        return new SnowflakeIdGenerator();
    }

    /**
     * MongoId event listener for automatic ID generation.
     *
     * @param identifyGenerator the ID generator
     * @return the event listener
     */
    @Bean
    @ConditionalOnClass(AutoInjectMongoIdEventListener.class)
    @ConditionalOnMissingBean(AutoInjectMongoIdEventListener.class)
    @ConditionalOnProperty(
        prefix = MongoPlusConstants.Configuration.MONGO_PLUS_IDENTITY_PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
    )
    public AutoInjectMongoIdEventListener autoInjectMongoIdEventListener(IdentifyGenerator identifyGenerator) {
        return new AutoInjectMongoIdEventListener(identifyGenerator);
    }

    // ----------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean(QueryService.class)
    public QueryService queryService(QueryExecutor queryExecutor) {
        return new DefaultQueryService(queryExecutor);
    }

    @Bean
    @ConditionalOnMissingBean(MongoEngine.class)
    public MongoEngine mongoEngine() {
        return new DefaultBeanFactoryMongoEngine();
    }
}
