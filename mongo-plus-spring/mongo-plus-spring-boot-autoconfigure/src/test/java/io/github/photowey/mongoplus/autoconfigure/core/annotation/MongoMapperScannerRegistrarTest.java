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
package io.github.photowey.mongoplus.autoconfigure.core.annotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.type.AnnotationMetadata;

import io.github.photowey.mongoplus.autoconfigure.core.bean.MapperFactoryBean;
import io.github.photowey.mongoplus.autoconfigure.core.mapper.MongoMapperScannerConfigurer;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MongoMapperScannerRegistrarTest - Unit tests for registrar bean definition registration behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("MongoMapperScannerRegistrarTest")
class MongoMapperScannerRegistrarTest {

    @Test
    @DisplayName(
        "Given metadata without MongoMapperScan, when registering bean definitions, then skips "
            + "registration"
    )
    @Story("Skip registration when metadata lacks MongoMapperScan")
    void givenMetadataWithoutMongoMapperScan_whenRegisteringBeanDefinitions_thenSkipsRegistration() {
        // Given
        MongoMapperScannerRegistrar registrar = new MongoMapperScannerRegistrar();
        DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
        AnnotationMetadata metadata = AnnotationMetadata.introspect(PlainConfig.class);

        // When
        registrar.registerBeanDefinitions(metadata, registry);

        // Then
        assertEquals(0, registry.getBeanDefinitionCount());
    }

    @Test
    @DisplayName(
        "Given customized MongoMapperScan, when registering bean definitions, then stores all "
            + "configured scanner properties"
    )
    @Story("Store all configured scanner properties for customized MongoMapperScan")
    void givenCustomizedMongoMapperScan_whenRegisteringBeanDefinitions_thenStoresAllConfiguredScannerProperties() {
        // Given
        MongoMapperScannerRegistrar registrar = new MongoMapperScannerRegistrar();
        DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
        AnnotationMetadata metadata = AnnotationMetadata.introspect(CustomizedScanConfig.class);
        String beanName = CustomizedScanConfig.class.getName()
            + "#"
            + MongoMapperScannerRegistrar.class.getSimpleName()
            + "#0";

        // When
        registrar.registerBeanDefinitions(metadata, registry);
        BeanDefinition definition = registry.getBeanDefinition(beanName);

        // Then
        assertEquals(MongoMapperScannerConfigurer.class.getName(), definition.getBeanClassName());
        assertEquals(
            "alpha.pkg,beta.pkg,alpha.pkg,beta.pkg," + PackageMarker.class.getPackage().getName(),
            this.property(definition, "basePackage")
        );
        assertEquals(Deprecated.class, this.property(definition, "annotationClass"));
        assertEquals(Runnable.class, this.property(definition, "markerInterface"));
        assertEquals("true", this.property(definition, "lazyInitialization"));
        assertEquals(CustomMapperFactoryBean.class, this.property(definition, "mapperFactoryBeanClass"));
        assertInstanceOf(TestBeanNameGenerator.class, this.property(definition, "nameGenerator"));
    }

    @Test
    @DisplayName(
        "Given default MongoMapperScan, when registering bean definitions, then falls back to the "
            + "declaring package"
    )
    @Story("Fall back to declaring package for default MongoMapperScan")
    void givenDefaultMongoMapperScan_whenRegisteringBeanDefinitions_thenFallsBackToTheDeclaringPackage() {
        // Given
        MongoMapperScannerRegistrar registrar = new MongoMapperScannerRegistrar();
        DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
        AnnotationMetadata metadata = AnnotationMetadata.introspect(DefaultScanConfig.class);
        String beanName = DefaultScanConfig.class.getName()
            + "#"
            + MongoMapperScannerRegistrar.class.getSimpleName()
            + "#0";

        // When
        registrar.registerBeanDefinitions(metadata, registry);
        BeanDefinition definition = registry.getBeanDefinition(beanName);

        // Then
        assertEquals(DefaultScanConfig.class.getPackage().getName(), this.property(definition, "basePackage"));
        assertEquals(io.github.photowey.mongoplus.annotation.MongoMapper.class,
            this.property(definition, "annotationClass"));
        assertEquals(io.github.photowey.mongoplus.mapper.MongoMapper.class,
            this.property(definition, "markerInterface"));
        assertNull(this.property(definition, "mapperFactoryBeanClass"));
    }

    @Test
    @DisplayName(
        "Given repeating MongoMapperScan annotations, when registering bean definitions, then "
            + "registers each scan with distinct bean names"
    )
    @Story("beanName")
    void givenRepeatingMongoMapperScanAnnotations_whenRegisteringBeanDefinitions_thenRegistersDistinctBeans(
    ) {
        // Given
        MongoMapperScannerRegistrar.RepeatingRegistrar registrar =
            new MongoMapperScannerRegistrar.RepeatingRegistrar();
        BeanDefinitionRegistry registry = new DefaultListableBeanFactory();
        AnnotationMetadata metadata = AnnotationMetadata.introspect(RepeatingScanConfig.class);

        // When
        registrar.registerBeanDefinitions(metadata, registry);

        // Then
        assertEquals(2, registry.getBeanDefinitionCount());
        assertTrue(registry.containsBeanDefinition(this.beanName(metadata, 0)));
        assertTrue(registry.containsBeanDefinition(this.beanName(metadata, 1)));
        assertEquals(
            "first.pkg,first.pkg",
            this.property(registry.getBeanDefinition(this.beanName(metadata, 0)), "basePackage")
        );
        assertEquals(
            PackageMarker.class.getPackage().getName(),
            this.property(registry.getBeanDefinition(this.beanName(metadata, 1)), "basePackage")
        );
    }

    private String beanName(AnnotationMetadata metadata, int index) {
        return metadata.getClassName()
            + "#"
            + MongoMapperScannerRegistrar.class.getSimpleName()
            + "#"
            + index;
    }

    private Object property(BeanDefinition definition, String name) {
        if (definition.getPropertyValues().getPropertyValue(name) == null) {
            return null;
        }

        return definition.getPropertyValues().getPropertyValue(name).getValue();
    }

    private static final class PlainConfig {
    }

    @MongoMapperScan(
        basePackages = {"alpha.pkg", "beta.pkg"},
        basePackageClasses = {PackageMarker.class},
        nameGenerator = TestBeanNameGenerator.class,
        annotationClass = Deprecated.class,
        markerInterface = Runnable.class,
        factoryBean = CustomMapperFactoryBean.class,
        lazyInitialization = "true"
    )
    private static final class CustomizedScanConfig {
    }

    @MongoMapperScan
    private static final class DefaultScanConfig {
    }

    @MongoMapperScans(
        {
            @MongoMapperScan("first.pkg"),
            @MongoMapperScan(basePackageClasses = PackageMarker.class)
        }
    )
    private static final class RepeatingScanConfig {
    }

    private static final class PackageMarker {
    }

    public static final class TestBeanNameGenerator implements BeanNameGenerator {

        @Override
        public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
            return "custom";
        }
    }

    static class CustomMapperFactoryBean extends MapperFactoryBean {

        CustomMapperFactoryBean(Class<?> mapperInterface) {
            super(mapperInterface);
        }
    }
}
