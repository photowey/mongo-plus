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
package io.github.photowey.mongoplus.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ObjectsTest - Unit tests for Objects utility class.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("ObjectsTest")
class ObjectsTest {

    @Test
    @DisplayName("Given null When isNull Then returns true")
    @Story("Check null")
    void givenNull_whenIsNull_thenReturnsTrue() {
        assertTrue(Objects.isNull(null));
        assertFalse(Objects.isNull("hello"));
        assertFalse(Objects.isNull(0));
        assertFalse(Objects.isNull(new Object()));
    }

    @Test
    @DisplayName("Given non-null When nonNull Then returns true")
    @Story("Check non-null")
    void givenNonNull_whenNonNull_thenReturnsTrue() {
        assertFalse(Objects.nonNull(null));
        assertTrue(Objects.nonNull("hello"));
        assertTrue(Objects.nonNull(0));
        assertTrue(Objects.nonNull(new Object()));
    }

    @Test
    @DisplayName("Given null When defaultIfNull Then returns default value")
    @Story("Default value for null")
    void givenNull_whenDefaultIfNull_thenReturnsDefault() {
        assertEquals("default", Objects.defaultIfNull(null, "default"));
        assertEquals("value", Objects.defaultIfNull("value", "default"));
        assertEquals(0, Objects.defaultIfNull(null, 0));
        assertEquals(42, Objects.defaultIfNull(42, 0));
    }

    @Test
    @DisplayName("Given null When requireNonNull Then throws NullPointerException")
    @Story("Require non-null or throw NPE")
    void givenNull_whenRequireNonNull_thenThrowsNpe() {
        assertDoesNotThrow(() -> Objects.requireNonNull("value"));
        assertThrows(NullPointerException.class, () ->
            Objects.requireNonNull(null)
        );
    }

    @Test
    @DisplayName("Given null When requireNonNull with message Then throws NPE with message")
    @Story("Require non-null with message")
    void givenNull_whenRequireNonNullWithMessage_thenThrowsNpeWithMessage() {
        assertDoesNotThrow(() ->
            Objects.requireNonNull("value", "Error: %s", "arg")
        );
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Objects.requireNonNull(null, "Error: %s", "arg")
        );
        assertEquals("Error: arg", exception.getMessage());
    }

    @Test
    @DisplayName("Given two values When equals Then returns true only if both null or equal")
    @Story("Null-safe equals")
    void givenTwoValues_whenEquals_thenReturnsCorrectly() {
        // Both null
        assertTrue(Objects.equals(null, null));

        // One null
        assertFalse(Objects.equals(null, "value"));
        assertFalse(Objects.equals("value", null));

        // Both non-null, equal
        assertTrue(Objects.equals("value", "value"));
        assertTrue(Objects.equals(123, 123));

        // Both non-null, not equal
        assertFalse(Objects.equals("value1", "value2"));
        assertFalse(Objects.equals(123, 456));
    }

    @Test
    @DisplayName("Given two values and comparator When compare Then returns correct sign")
    @Story("Null-safe compare with comparator")
    void givenTwoValuesAndComparator_whenCompare_thenReturnsCorrectSign() {
        Comparator<String> comparator = String::compareTo;

        // Both null
        assertEquals(0, Objects.compare(null, null, comparator));

        // One null (null is considered less)
        assertTrue(Objects.compare(null, "a", comparator) < 0);
        assertTrue(Objects.compare("a", null, comparator) > 0);

        // Both non-null
        assertEquals(0, Objects.compare("a", "a", comparator));
        assertTrue(Objects.compare("a", "b", comparator) < 0);
        assertTrue(Objects.compare("b", "a", comparator) > 0);
    }

    @Test
    @DisplayName("Given object When hashCode Then returns object hash or zero for null")
    @Story("Null-safe hashCode")
    void givenObject_whenHashCode_thenReturnsHashOrZero() {
        assertEquals(0, Objects.hashCode(null));
        assertEquals("hello".hashCode(), Objects.hashCode("hello"));
        assertEquals(123, Objects.hashCode(123));
    }

    @Test
    @DisplayName("Given varargs When hash Then returns combined hash")
    @Story("Combined hash from varargs")
    void givenVarargs_whenHash_thenReturnsCombinedHash() {
        // Single element
        assertEquals(java.util.Objects.hash("a"), Objects.hash("a"));

        // Multiple elements
        assertEquals(
            java.util.Objects.hash("a", "b", "c"),
            Objects.hash("a", "b", "c")
        );

        // With null
        assertEquals(
            java.util.Objects.hash(null, "a"),
            Objects.hash(null, "a")
        );
    }

    @Test
    @DisplayName("Given object When toString Then returns string or null literal")
    @Story("Null-safe toString")
    void givenObject_whenToString_thenReturnsStringOrNullLiteral() {
        assertEquals("null", Objects.toString(null));
        assertEquals("hello", Objects.toString("hello"));
        assertEquals("123", Objects.toString(123));
    }

    @Test
    @DisplayName("Given object When toString with default Then returns string or default")
    @Story("ToString with default")
    void givenObject_whenToStringWithDefault_thenReturnsStringOrDefault() {
        assertEquals("default", Objects.toString(null, "default"));
        assertEquals("hello", Objects.toString("hello", "default"));
        assertEquals("123", Objects.toString(123, "default"));
    }

    @Test
    @DisplayName("Given Objects class When instantiate via reflection Then throws AssertionError")
    @Story("Utility class not instantiable via reflection")
    void givenObjectsClass_whenInstantiateViaReflection_thenThrowsAssertionError() {
        AssertionError error = assertThrows(AssertionError.class, () -> {
            try {
                Constructor<Objects> ctor = Objects.class.getDeclaredConstructor();
                ctor.setAccessible(true);
                ctor.newInstance();
            } catch (InvocationTargetException e) {
                throw e.getCause();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(
            error
                .getMessage()
                .contains("No io.github.photowey.mongoplus.core.util.Objects")
        );
    }
}
