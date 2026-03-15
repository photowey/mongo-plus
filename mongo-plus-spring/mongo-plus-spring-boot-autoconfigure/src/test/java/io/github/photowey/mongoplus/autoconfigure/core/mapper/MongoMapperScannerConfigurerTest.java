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

import java.util.Properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.StringUtils;

import io.github.photowey.mongoplus.annotation.MongoMapper;
import io.github.photowey.mongoplus.autoconfigure.core.bean.MapperFactoryBean;
import io.github.photowey.mongoplus.autoconfigure.core.scanner.fixture.AnnotatedFixtureMapper;
import io.github.photowey.mongoplus.autoconfigure.core.scanner.fixture.FixtureMarker;
import io.github.photowey.mongoplus.autoconfigure.core.scanner.fixture.MarkerFixtureMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MongoMapperScannerConfigurerTest - Unit tests for scanner configurer property and scanning behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("MongoMapperScannerConfigurerTest")
class MongoMapperScannerConfigurerTest {

    private static final String FIXTURE_PACKAGE =
        "io.github.photowey.mongoplus.autoconfigure.core.scanner.fixture";

    @Test
    @DisplayName(
        "Given a missing base package, when validating properties, then throws an illegal "
            + "argument exception"
    )
    @Story("Missing base package throws on validation")
    void givenAMissingBasePackage_whenValidatingProperties_thenThrowsAnIllegalArgumentException() {
        // Given
        MongoMapperScannerConfigurer configurer = new MongoMapperScannerConfigurer();

        // When
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, configurer::afterPropertiesSet);

        // Then
        assertEquals("Property 'basePackage' is required", error.getMessage());
    }

    @Test
    @DisplayName(
        "Given direct scanner settings, when post processing the registry, then scans matching "
            + "marker interfaces"
    )
    @Story("Direct scanner settings scan matching marker interfaces")
    void givenDirectScannerSettings_whenPostProcessingTheRegistry_thenScansMatchingMarkerInterfaces() {
        // Given
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        BeanDefinitionRegistry registry = new DefaultListableBeanFactory();
        MongoMapperScannerConfigurer configurer = new MongoMapperScannerConfigurer();
        configurer.setApplicationContext(applicationContext);
        configurer.setBasePackage(FIXTURE_PACKAGE);
        configurer.setMarkerInterface(FixtureMarker.class);
        configurer.setMapperFactoryBeanClass(TestMapperFactoryBean.class);
        configurer.setLazyInitialization("false");

        // When
        assertDoesNotThrow(() -> configurer.postProcessBeanFactory(applicationContext.getBeanFactory()));
        configurer.postProcessBeanDefinitionRegistry(registry);
        BeanDefinition definition = this.findDefinitionByConstructorArg(registry, MarkerFixtureMapper.class.getName());

        // Then
        assertTrue(registry.getBeanDefinitionCount() >= 1);
        assertEquals(false, definition.isLazyInit());
        assertEquals(
            MarkerFixtureMapper.class.getName(),
            definition.getConstructorArgumentValues().getGenericArgumentValues().get(0).getValue()
        );
    }

    @Test
    @DisplayName(
        "Given placeholder based scanner settings, when post processing the registry, then "
            + "resolves placeholders and applies the custom name generator"
    )
    @Story("registerPlaceholderConfigurer")
    void givenPlaceholderBasedScannerSettings_whenPostProcessingRegistry_thenResolvesPlaceholdersAndAppliesCustomNames(
    ) {
        // Given
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        BeanDefinitionRegistry registry = new DefaultListableBeanFactory();
        MongoMapperScannerConfigurer configurer = new MongoMapperScannerConfigurer();
        configurer.setBeanName("scannerConfigurer");
        configurer.setApplicationContext(applicationContext);
        configurer.setProcessPropertyPlaceHolders(true);
        configurer.setAnnotationClass(MongoMapper.class);
        configurer.setNameGenerator(new PrefixedBeanNameGenerator());
        this.registerPlaceholderConfigurer(applicationContext);
        this.registerConfigurerBeanDefinition(applicationContext);
        applicationContext.refresh();

        // When
        configurer.postProcessBeanDefinitionRegistry(registry);

        // Then
        assertTrue(registry.getBeanDefinitionCount() >= 1);
        BeanDefinition definition = this.findDefinitionByConstructorArg(
            registry,
            AnnotatedFixtureMapper.class.getName()
        );
        assertEquals(true, definition.isLazyInit());
        assertEquals(
            AnnotatedFixtureMapper.class.getName(),
            definition.getConstructorArgumentValues().getGenericArgumentValues().get(0).getValue()
        );
    }

    private void registerPlaceholderConfigurer(GenericApplicationContext applicationContext) {
        Properties properties = new Properties();
        properties.setProperty("scan.package", FIXTURE_PACKAGE);
        properties.setProperty("scan.lazy", "true");
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setProperties(properties);
        applicationContext.getBeanFactory().registerSingleton("placeholderConfigurer", configurer);
    }

    private void registerConfigurerBeanDefinition(GenericApplicationContext applicationContext) {
        BeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(PlaceholderHolder.class)
            .addPropertyValue("basePackage", new TypedStringValue("${scan.package}"))
            .addPropertyValue("lazyInitialization", new TypedStringValue("${scan.lazy}"))
            .getBeanDefinition();
        applicationContext.registerBeanDefinition("scannerConfigurer", definition);
    }

    private BeanDefinition findDefinitionByConstructorArg(BeanDefinitionRegistry registry, String mapperInterface) {
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition definition = registry.getBeanDefinition(beanName);
            Object value = definition.getConstructorArgumentValues().getGenericArgumentValues().get(0).getValue();
            if (mapperInterface.equals(value)) {
                return definition;
            }
        }

        throw new IllegalStateException("No bean definition found for mapper: " + mapperInterface);
    }

    public static final class PrefixedBeanNameGenerator implements BeanNameGenerator {

        @Override
        public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
            String beanClassName = definition.getBeanClassName();
            String simpleName = StringUtils.getFilename(StringUtils.replace(beanClassName, ".", "/"));
            return "prefixed$" + simpleName;
        }
    }

    static class TestMapperFactoryBean extends MapperFactoryBean {

        TestMapperFactoryBean(Class<?> mapperInterface) {
            super(mapperInterface);
        }
    }

    public static final class PlaceholderHolder {

        public void setBasePackage(String basePackage) {
        }

        public void setLazyInitialization(String lazyInitialization) {
        }
    }
}
