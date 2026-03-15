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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * StringsTest - Unit tests for Strings utility class.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("StringsTest")
class StringsTest {

    @Test
    @DisplayName("Given null or empty string When isEmpty Then returns true")
    @Story("Check null or empty string")
    void givenNullOrEmptyString_whenIsEmpty_thenReturnsTrue() {
        assertTrue(Strings.isEmpty(null));
        assertTrue(Strings.isEmpty(""));
        assertFalse(Strings.isEmpty("hello"));
        assertFalse(Strings.isEmpty(" "));
    }

    @Test
    @DisplayName("Given non-empty string When isNotEmpty Then returns true")
    @Story("Check non-empty string")
    void givenNonEmptyString_whenIsNotEmpty_thenReturnsTrue() {
        assertFalse(Strings.isNotEmpty(null));
        assertFalse(Strings.isNotEmpty(""));
        assertTrue(Strings.isNotEmpty("hello"));
        assertTrue(Strings.isNotEmpty(" "));
    }

    @Test
    @DisplayName("Given null empty or blank string When isBlank Then returns true")
    @Story("Check null empty or blank")
    void givenNullEmptyOrBlank_whenIsBlank_thenReturnsTrue() {
        assertTrue(Strings.isBlank(null));
        assertTrue(Strings.isBlank(""));
        assertTrue(Strings.isBlank(" "));
        assertTrue(Strings.isBlank("  "));
        assertTrue(Strings.isBlank("\t\n"));
        assertFalse(Strings.isBlank("hello"));
        assertFalse(Strings.isBlank(" hello "));
    }

    @Test
    @DisplayName("Given string with content When isNotBlank Then returns true")
    @Story("Check string with content")
    void givenStringWithContent_whenIsNotBlank_thenReturnsTrue() {
        assertFalse(Strings.isNotBlank(null));
        assertFalse(Strings.isNotBlank(""));
        assertFalse(Strings.isNotBlank(" "));
        assertTrue(Strings.isNotBlank("hello"));
        assertTrue(Strings.isNotBlank(" hello "));
    }

    @Test
    @DisplayName("Given null string When defaultIfNull Then returns default value")
    @Story("Default if null")
    void givenNullString_whenDefaultIfNull_thenReturnsDefault() {
        assertEquals("default", Strings.defaultIfNull(null, "default"));
        assertEquals("value", Strings.defaultIfNull("value", "default"));
        assertEquals("", Strings.defaultIfNull("", "default"));
    }

    @Test
    @DisplayName("Given null or empty When defaultIfEmpty Then returns default value")
    @Story("Default if empty")
    void givenNullOrEmpty_whenDefaultIfEmpty_thenReturnsDefault() {
        assertEquals("default", Strings.defaultIfEmpty(null, "default"));
        assertEquals("default", Strings.defaultIfEmpty("", "default"));
        assertEquals("value", Strings.defaultIfEmpty("value", "default"));
        assertEquals(" ", Strings.defaultIfEmpty(" ", "default"));
    }

    @Test
    @DisplayName("Given null empty or blank When defaultIfBlank Then returns default value")
    @Story("Default if blank")
    void givenNullEmptyOrBlank_whenDefaultIfBlank_thenReturnsDefault() {
        assertEquals("default", Strings.defaultIfBlank(null, "default"));
        assertEquals("default", Strings.defaultIfBlank("", "default"));
        assertEquals("default", Strings.defaultIfBlank(" ", "default"));
        assertEquals("value", Strings.defaultIfBlank("value", "default"));
    }

    @Test
    @DisplayName("Given string When capitalize Then first character is uppercase")
    @Story("Capitalize first character")
    void givenString_whenCapitalize_thenFirstCharUppercase() {
        assertNull(Strings.capitalize(null));
        assertEquals("", Strings.capitalize(""));
        assertEquals("Hello", Strings.capitalize("hello"));
        assertEquals("Hello", Strings.capitalize("Hello")); // already capitalized
        assertEquals("Hello world", Strings.capitalize("hello world"));
    }

    @Test
    @DisplayName("Given string When uncapitalize Then first character is lowercase")
    @Story("Uncapitalize first character")
    void givenString_whenUncapitalize_thenFirstCharLowercase() {
        assertNull(Strings.uncapitalize(null));
        assertEquals("", Strings.uncapitalize(""));
        assertEquals("hello", Strings.uncapitalize("Hello"));
        assertEquals("hello", Strings.uncapitalize("hello")); // already uncapitalized
        assertEquals("hello World", Strings.uncapitalize("Hello World"));
    }

    @Test
    @DisplayName("Given camelCase string When camelToUnderline Then returns snake_case")
    @Story("Convert camelCase to snake_case")
    void givenCamelCase_whenCamelToUnderline_thenReturnsSnakeCase() {
        assertNull(Strings.camelToUnderline(null));
        assertEquals("", Strings.camelToUnderline(""));
        assertEquals("user_name", Strings.camelToUnderline("userName"));
        assertEquals(
            "user_name_info",
            Strings.camelToUnderline("userNameInfo")
        );
        assertEquals("username", Strings.camelToUnderline("username")); // no uppercase
        assertEquals("username", Strings.camelToUnderline("Username")); // starts with uppercase - no leading underscore
    }

    @Test
    @DisplayName("Given snake_case string When underlineToCamel Then returns camelCase")
    @Story("Convert snake_case to camelCase")
    void givenSnakeCase_whenUnderlineToCamel_thenReturnsCamelCase() {
        assertNull(Strings.underlineToCamel(null));
        assertEquals("", Strings.underlineToCamel(""));
        assertEquals("userName", Strings.underlineToCamel("user_name"));
        assertEquals(
            "userNameInfo",
            Strings.underlineToCamel("user_name_info")
        );
        assertEquals("username", Strings.underlineToCamel("username")); // no underscore
    }

    @Test
    @DisplayName("Given string When trimToNull Then returns trimmed or null if blank")
    @Story("Trim to null if blank")
    void givenString_whenTrimToNull_thenReturnsTrimmedOrNull() {
        assertNull(Strings.trimToNull(null));
        assertNull(Strings.trimToNull(""));
        assertNull(Strings.trimToNull("   "));
        assertEquals("hello", Strings.trimToNull("hello"));
        assertEquals("hello", Strings.trimToNull(" hello "));
        assertEquals("hello", Strings.trimToNull("\thello\n"));
    }

    @Test
    @DisplayName("Given string When trimToEmpty Then returns trimmed or empty string")
    @Story("Trim to empty if blank")
    void givenString_whenTrimToEmpty_thenReturnsTrimmedOrEmpty() {
        assertEquals("", Strings.trimToEmpty(null));
        assertEquals("", Strings.trimToEmpty(""));
        assertEquals("", Strings.trimToEmpty("   "));
        assertEquals("hello", Strings.trimToEmpty("hello"));
        assertEquals("hello", Strings.trimToEmpty(" hello "));
    }

    @Test
    @DisplayName("Given string and char delimiter When split Then returns array of parts")
    @Story("Split by char delimiter")
    void givenStringAndCharDelimiter_whenSplit_thenReturnsArray() {
        assertEquals(0, Strings.split(null, ',').length);
        assertEquals(0, Strings.split("", ',').length);

        String[] result = Strings.split("a,b,c", ',');
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
        assertEquals("c", result[2]);
    }

    @Test
    @DisplayName("Given string and string delimiter When split Then returns array of parts")
    @Story("Split by string delimiter")
    void givenStringAndStringDelimiter_whenSplit_thenReturnsArray() {
        assertEquals(0, Strings.split(null, ",").length);
        assertEquals(0, Strings.split("", ",").length);

        String[] result = Strings.split("a,b,c", ",");
        assertEquals(3, result.length);
        assertArrayEquals(new String[] {"a", "b", "c"}, result);
    }

    @Test
    @DisplayName("Given Strings class When instantiate via reflection Then throws AssertionError")
    @Story("Utility class reflection instantiation throws")
    void givenStringsClass_whenInstantiateViaReflection_thenThrowsAssertionError() {
        AssertionError error = assertThrows(AssertionError.class, () -> {
            try {
                Constructor<Strings> ctor = Strings.class.getDeclaredConstructor();
                ctor.setAccessible(true);
                ctor.newInstance();
            } catch (InvocationTargetException e) {
                throw (AssertionError) e.getCause();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(
            error
                .getMessage()
                .contains("No io.github.photowey.mongoplus.core.util.Strings")
        );
    }
}
