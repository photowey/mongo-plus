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
package io.github.photowey.mongoplus.autoconfigure.core.scanner;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import io.github.photowey.mongoplus.annotation.MongoMapper;
import io.github.photowey.mongoplus.autoconfigure.core.bean.MapperFactoryBean;
import io.github.photowey.mongoplus.autoconfigure.core.scanner.fixture.AnnotatedFixtureMapper;
import io.github.photowey.mongoplus.autoconfigure.core.scanner.fixture.FixtureMarker;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ClassPathMongoMapperScannerTest - Unit tests for ClassPathMongoMapperScanner filter registration and scanning.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/13
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("ClassPathMongoMapperScannerTest")
class ClassPathMongoMapperScannerTest {

    private static final String FIXTURE_PACKAGE =
        "io.github.photowey.mongoplus.autoconfigure.core.scanner.fixture";

    @Test
    @DisplayName(
        "Given annotation filter, when scanning, then registers annotated mapper with "
            + "default factory bean"
    )
    @Story("Annotation filter scan registers mapper with default factory bean")
    void givenAnnotationFilter_whenScanning_thenRegistersAnnotatedMapperWithDefaultFactoryBean() {
        // Given
        SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
        ClassPathMongoMapperScanner scanner = new ClassPathMongoMapperScanner(registry);
        scanner.setAnnotationClass(MongoMapper.class);
        scanner.setMapperFactoryBean(null);
        scanner.setMapperFactoryBeanClass(null);
        scanner.setLazyInitialization(true);
        scanner.registerFilters();

        // When
        Set<BeanDefinitionHolder> holders = scanner.doScan(FIXTURE_PACKAGE);
        BeanDefinition definition = registry.getBeanDefinition(holders.iterator().next().getBeanName());

        // Then
        assertEquals(1, holders.size());
        assertEquals(true, definition.isLazyInit());
        assertEquals(
            MapperFactoryBean.class,
            ((AbstractBeanDefinition) definition).getBeanClass()
        );
        assertEquals(
            AnnotatedFixtureMapper.class.getName(),
            definition.getConstructorArgumentValues().getGenericArgumentValues().get(0).getValue()
        );
    }

    @Test
    @DisplayName(
        "Given marker filter and custom factory bean, when scanning, then registers marker "
            + "mapper with custom bean"
    )
    @Story("Marker filter scan registers mapper with custom factory bean")
    void givenMarkerFilterAndCustomFactoryBean_whenScanning_thenRegistersMarkerMapperWithCustomBean() {
        // Given
        SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
        ClassPathMongoMapperScanner scanner = new ClassPathMongoMapperScanner(registry);
        scanner.setMarkerInterface(FixtureMarker.class);
        scanner.setMapperFactoryBeanClass(TestMapperFactoryBean.class);
        scanner.registerFilters();

        // When
        Set<BeanDefinitionHolder> holders = scanner.doScan(FIXTURE_PACKAGE);
        BeanDefinition definition = registry.getBeanDefinition(holders.iterator().next().getBeanName());

        // Then
        assertEquals(1, holders.size());
        assertEquals(
            TestMapperFactoryBean.class,
            ((AbstractBeanDefinition) definition).getBeanClass()
        );
    }

    @Test
    @DisplayName(
        "Given accept-all mode, when scanning twice, then registers interfaces once and "
            + "rejects duplicate bean names"
    )
    @Story("Accept-all scan registers once and rejects duplicate bean names")
    void givenAcceptAllMode_whenScanningTwice_thenRegistersInterfacesOnceAndRejectsDuplicateBeanNames() {
        // Given
        SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
        ClassPathMongoMapperScanner scanner = new ClassPathMongoMapperScanner(
            registry,
            new PathMatchingResourcePatternResolver(),
            new StandardEnvironment()
        );
        scanner.registerFilters();

        // When
        Set<BeanDefinitionHolder> firstScan = scanner.doScan(FIXTURE_PACKAGE);
        Set<BeanDefinitionHolder> secondScan = scanner.doScan(FIXTURE_PACKAGE);

        // Then
        assertTrue(firstScan.size() >= 3);
        assertEquals(0, secondScan.size());
        String beanNames = String.join(",", registry.getBeanDefinitionNames());
        assertTrue(beanNames.contains("AnnotatedFixtureMapper") || beanNames.contains("annotatedFixtureMapper"));
        assertTrue(beanNames.contains("MarkerFixtureMapper") || beanNames.contains("markerFixtureMapper"));
        assertTrue(beanNames.contains("PlainFixtureMapper") || beanNames.contains("plainFixtureMapper"));
    }

    private static final class TestMapperFactoryBean extends MapperFactoryBean {

        private TestMapperFactoryBean(Class<?> mapperInterface) {
            super(mapperInterface);
        }
    }
}
