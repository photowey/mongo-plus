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
package io.github.photowey.mongoplus.core.lambda;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PropertyNamerTest - Unit tests for property naming and accessor detection helpers.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("PropertyNamerTest")
class PropertyNamerTest {

    @Test
    @DisplayName(
        "Given accessor style method names, when converting to property names, then strips the "
            + "expected prefix"
    )
    @Story("Convert accessor method names to property names")
    void givenAccessorStyleMethodNames_whenConvertingToPropertyNames_thenStripsTheExpectedPrefix() {
        // Given

        // When
        String getterProperty = PropertyNamer.methodToProperty("getName");
        String booleanProperty = PropertyNamer.methodToProperty("isActive");
        String setterProperty = PropertyNamer.methodToProperty("setStatus");
        String plainProperty = PropertyNamer.methodToProperty("value");
        String singleCharacter = PropertyNamer.methodToProperty("A");
        String empty = PropertyNamer.methodToProperty("");

        // Then
        assertEquals("name", getterProperty);
        assertEquals("active", booleanProperty);
        assertEquals("status", setterProperty);
        assertEquals("value", plainProperty);
        assertEquals("a", singleCharacter);
        assertEquals("", empty);
    }

    @Test
    @DisplayName(
        "Given potential accessor method names, when checking getter and setter detection, then "
            + "returns the expected flags"
    )
    @Story("Detect getter and setter from method names")
    void givenPotentialAccessorMethodNames_whenCheckingGetterAndSetterDetection_thenReturnsTheExpectedFlags() {
        // Given

        // When
        boolean getter = PropertyNamer.isGetter("getName");
        boolean booleanGetter = PropertyNamer.isGetter("isActive");
        boolean invalidGetter = PropertyNamer.isGetter("name");
        boolean setter = PropertyNamer.isSetter("setName");
        boolean invalidSetter = PropertyNamer.isSetter("se");

        // Then
        assertTrue(getter);
        assertTrue(booleanGetter);
        assertFalse(invalidGetter);
        assertTrue(setter);
        assertFalse(invalidSetter);
    }
}
