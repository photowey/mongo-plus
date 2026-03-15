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
package io.github.photowey.mongoplus.autoconfigure.core.mapper;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import io.github.photowey.mongoplus.autoconfigure.core.bean.MapperFactoryBean;
import io.github.photowey.mongoplus.autoconfigure.core.scanner.ClassPathMongoMapperScanner;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;

import lombok.Setter;

import static org.springframework.util.Assert.notNull;

/**
 * MongoMapperScannerConfigurer - Configures mapper scanning based on properties.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Setter
public class MongoMapperScannerConfigurer
    implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware, BeanNameAware, InitializingBean {

    private String beanName;
    private String basePackage;
    private String lazyInitialization;
    private Class<?> markerInterface;
    private boolean processPropertyPlaceHolders;

    private Class<? extends Annotation> annotationClass;
    private Class<? extends MapperFactoryBean> mapperFactoryBeanClass;

    private BeanNameGenerator nameGenerator;
    private ApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(this.basePackage, "Property 'basePackage' is required");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // left intentionally blank
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (this.processPropertyPlaceHolders) {
            this.processPropertyPlaceHolders();
        }

        ClassPathMongoMapperScanner scanner =
            new ClassPathMongoMapperScanner(registry, this.applicationContext, this.getEnvironment());
        scanner.setMarkerInterface(this.markerInterface);
        scanner.setMapperFactoryBeanClass(this.mapperFactoryBeanClass);
        scanner.setAnnotationClass(this.annotationClass);
        if (Objects.nonNull(this.nameGenerator)) {
            scanner.setBeanNameGenerator(this.nameGenerator);
        }
        scanner.setResourceLoader(this.applicationContext);

        if (StringUtils.hasText(lazyInitialization)) {
            scanner.setLazyInitialization(Boolean.parseBoolean(lazyInitialization));
        }

        scanner.registerFilters();
        scanner.scan(
            StringUtils.tokenizeToStringArray(
                this.basePackage,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS
            )
        );
    }

    private void processPropertyPlaceHolders() {
        Map<String, PropertyResourceConfigurer> prcs = applicationContext.getBeansOfType(
            PropertyResourceConfigurer.class, false, false
        );

        if (Collections.isNotEmpty(prcs) && this.applicationContext instanceof ConfigurableApplicationContext) {
            BeanDefinition mapperScannerBean = ((ConfigurableApplicationContext) this.applicationContext)
                .getBeanFactory()
                .getBeanDefinition(this.beanName);

            DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
            factory.registerBeanDefinition(this.beanName, mapperScannerBean);

            for (PropertyResourceConfigurer prc : prcs.values()) {
                prc.postProcessBeanFactory(factory);
            }

            PropertyValues values = mapperScannerBean.getPropertyValues();
            this.basePackage = this.updatePropertyValue("basePackage", values);
            this.lazyInitialization = this.updatePropertyValue("lazyInitialization", values);
        }
        this.basePackage = Optional.ofNullable(this.basePackage)
            .map(this.getEnvironment()::resolvePlaceholders)
            .orElse(null);
        this.lazyInitialization = Optional.ofNullable(this.lazyInitialization)
            .map(this.getEnvironment()::resolvePlaceholders)
            .orElse(null);
    }

    private Environment getEnvironment() {
        return this.applicationContext.getEnvironment();
    }

    private String updatePropertyValue(String propertyName, PropertyValues values) {
        PropertyValue property = values.getPropertyValue(propertyName);
        if (Objects.isNull(property)) {
            return null;
        }

        Object value = property.getValue();
        if (Objects.isNull(value)) {
            return null;
        }
        if (value instanceof String) {
            return value.toString();
        }
        if (value instanceof TypedStringValue) {
            return ((TypedStringValue) value).getValue();
        }

        return null;
    }
}
