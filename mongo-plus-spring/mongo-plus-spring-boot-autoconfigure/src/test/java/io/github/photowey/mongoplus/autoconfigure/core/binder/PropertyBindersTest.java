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
package io.github.photowey.mongoplus.autoconfigure.core.binder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import io.github.photowey.mongoplus.autoconfigure.core.property.MongoPlusProperties;
import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PropertyBindersTest - Unit tests for PropertyBinders map and environment binding helpers.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/13
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("PropertyBindersTest")
class PropertyBindersTest {

    private static final String OPTION_PREFIX = MongoPlusConstants.Configuration.MONGO_PLUS_PREFIX + ".option";

    @Test
    @DisplayName(
        "Given map context, when binding to a new option class, then throws because no "
            + "value is bound for class creation"
    )
    @Story("Binding to new option class throws when no value bound")
    void givenMapContext_whenBindingToANewOptionClass_thenThrowsBecauseNoValueIsBoundForClassCreation() {
        // Given
        Map<String, Object> source = this.source();

        // When
        NoSuchElementException error = assertThrows(
            NoSuchElementException.class,
            () -> PropertyBinders.bind(
                source,
                OPTION_PREFIX,
                MongoPlusProperties.Option.class
            )
        );

        // Then
        assertEquals("No value bound", error.getMessage());
    }

    @Test
    @DisplayName(
        "Given property source and environment, when binding existing targets, then completes "
            + "without throwing"
    )
    @Story("Binding existing targets completes without throwing")
    void givenPropertySourceAndEnvironment_whenBindingExistingTargets_thenCompletesWithoutThrowing() {
        // Given
        Map<String, Object> source = this.source();
        ConfigurationPropertySource propertySource = new MapConfigurationPropertySource(source);
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("mongo-plus-test", source));
        MongoPlusProperties.Option mapTarget = new MongoPlusProperties.Option();
        MongoPlusProperties.Option propertySourceTarget = new MongoPlusProperties.Option();
        MongoPlusProperties.Option environmentTarget = new MongoPlusProperties.Option();

        // When
        assertDoesNotThrow(() -> PropertyBinders.bind(source, OPTION_PREFIX, mapTarget));
        assertDoesNotThrow(() -> PropertyBinders.bind(propertySource, OPTION_PREFIX, propertySourceTarget));
        assertDoesNotThrow(() -> PropertyBinders.bind(environment, OPTION_PREFIX, environmentTarget));

        // Then
        assertInstanceOf(MongoPlusProperties.Option.class, mapTarget);
        assertInstanceOf(MongoPlusProperties.Option.class, propertySourceTarget);
        assertInstanceOf(MongoPlusProperties.Option.class, environmentTarget);
    }

    @Test
    @DisplayName(
        "Given environment class binding, when requesting a new option instance, then throws "
            + "because no value is bound"
    )
    @Story("Environment class binding throws when no value bound for new instance")
    void givenEnvironmentClassBinding_whenRequestingANewOptionInstance_thenThrowsBecauseNoValueIsBound() {
        // Given
        Map<String, Object> source = this.source();
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("mongo-plus-test", source));

        // When
        NoSuchElementException error = assertThrows(
            NoSuchElementException.class,
            () -> PropertyBinders.bind(
                environment,
                OPTION_PREFIX,
                MongoPlusProperties.Option.class
            )
        );

        // Then
        assertEquals("No value bound", error.getMessage());
    }

    @Test
    @DisplayName("Given private constructor, when instantiated reflectively, then throws AssertionError")
    @Story("Reflective instantiation of private constructor throws AssertionError")
    void givenPrivateConstructor_whenInstantiatedReflectively_thenThrowsAssertionError() throws Exception {
        // Given
        Constructor<PropertyBinders> constructor = PropertyBinders.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // When
        InvocationTargetException error = assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Then
        assertInstanceOf(AssertionError.class, error.getCause());
        assertTrue(error.getCause().getMessage().contains(PropertyBinders.class.getName()));
    }

    private Map<String, Object> source() {
        return Map.<String, Object>of(
            "mongo-plus.option.enabled", true,
            "mongo-plus.option.max-connection-per-host", 32
        );
    }
}
