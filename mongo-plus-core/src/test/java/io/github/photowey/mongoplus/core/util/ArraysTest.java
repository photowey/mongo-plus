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
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ArraysTest - Tests for {@link Arrays}.
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("ArraysTest")
class ArraysTest {

    @Test
    @DisplayName("Given null or empty byte array When isEmpty Then returns true")
    @Story("Check null or empty byte array")
    void givenNullOrEmptyByteArray_whenIsEmpty_thenReturnsTrue() {
        assertTrue(Arrays.isEmpty((byte[]) null));
        assertTrue(Arrays.isEmpty(new byte[0]));
        assertFalse(Arrays.isEmpty(new byte[] {1, 2, 3}));
    }

    @Test
    @DisplayName("Given null or empty object array When isEmpty Then returns true")
    @Story("Check null or empty object array")
    void givenNullOrEmptyObjectArray_whenIsEmpty_thenReturnsTrue() {
        assertTrue(Arrays.isEmpty((String[]) null));
        assertTrue(Arrays.isEmpty(new String[0]));
        assertFalse(Arrays.isEmpty(new String[] {"a", "b"}));
    }

    @Test
    @DisplayName("Given null or empty int array When isEmpty Then returns true")
    @Story("Check null or empty int array")
    void givenNullOrEmptyIntArray_whenIsEmpty_thenReturnsTrue() {
        assertTrue(Arrays.isEmpty((int[]) null));
        assertTrue(Arrays.isEmpty(new int[0]));
        assertFalse(Arrays.isEmpty(new int[] {1, 2, 3}));
    }

    @Test
    @DisplayName("Given null or empty long array When isEmpty Then returns true")
    @Story("Check null or empty long array")
    void givenNullOrEmptyLongArray_whenIsEmpty_thenReturnsTrue() {
        assertTrue(Arrays.isEmpty((long[]) null));
        assertTrue(Arrays.isEmpty(new long[0]));
        assertFalse(Arrays.isEmpty(new long[] {1L, 2L, 3L}));
    }

    @Test
    @DisplayName("Given non-empty object array When isNotEmpty Then returns true")
    @Story("Check non-empty object array")
    void givenNonEmptyObjectArray_whenIsNotEmpty_thenReturnsTrue() {
        assertFalse(Arrays.isNotEmpty((String[]) null));
        assertFalse(Arrays.isNotEmpty(new String[0]));
        assertTrue(Arrays.isNotEmpty(new String[] {"a"}));
    }

    @Test
    @DisplayName("Given varargs When asList Then returns mutable list")
    @Story("Convert varargs to mutable list")
    void givenVarargs_whenAsList_thenReturnsMutableList() {
        List<String> nullList = Arrays.asList((String[]) null);
        assertNotNull(nullList);
        assertTrue(nullList.isEmpty());

        List<String> emptyList = Arrays.asList(new String[0]);
        assertNotNull(emptyList);
        assertTrue(emptyList.isEmpty());

        List<String> list = Arrays.asList("a", "b", "c");
        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));

        list.set(0, "A");
        assertEquals("A", list.get(0));
    }

    @Test
    @DisplayName("Given list When toArray Then returns array of given type")
    @Story("Convert list to typed array")
    void givenList_whenToArray_thenReturnsArrayOfType() {
        String[] emptyArray = Arrays.toArray(Collections.emptyList(), String.class);
        assertEquals(0, emptyArray.length);

        String[] nullArray = Arrays.toArray(null, String.class);
        assertEquals(0, nullArray.length);

        List<String> list = java.util.Arrays.asList("a", "b", "c");
        String[] array = Arrays.toArray(list, String.class);
        assertEquals(3, array.length);
        assertEquals("a", array[0]);
        assertEquals("b", array[1]);
        assertEquals("c", array[2]);
    }

    @Test
    @DisplayName("Given byte array and element When contains Then returns true if element in array")
    @Story("Check byte array contains element")
    void givenByteArrayAndElement_whenContains_thenReturnsWhetherContained() {
        assertFalse(Arrays.contains((byte[]) null, (byte) 1));
        assertFalse(Arrays.contains(new byte[0], (byte) 1));
        assertTrue(Arrays.contains(new byte[] {1, 2, 3}, (byte) 2));
        assertFalse(Arrays.contains(new byte[] {1, 2, 3}, (byte) 4));
    }

    @Test
    @DisplayName("Given object array When contains Then returns true if element present")
    @Story("Check object array contains element")
    void givenObjectArray_whenContains_thenReturnsTrueIfElementPresent() {
        assertFalse(Arrays.contains((String[]) null, "a"));
        assertFalse(Arrays.contains(new String[0], "a"));
        assertTrue(Arrays.contains(new String[] {"a", "b", "c"}, "b"));
        assertFalse(Arrays.contains(new String[] {"a", "b", "c"}, "d"));

        assertTrue(Arrays.contains(new String[] {"a", null, "c"}, null));
        assertFalse(Arrays.contains(new String[] {"a", "b", "c"}, null));
    }

    @Test
    @DisplayName("Given Arrays class When instantiate via reflection Then throws AssertionError")
    @Story("Utility class rejects reflection instantiation")
    void givenArraysClass_whenInstantiateViaReflection_thenThrowsAssertionError() {
        AssertionError error = assertThrows(AssertionError.class, () -> {
            try {
                Constructor<Arrays> ctor = Arrays.class.getDeclaredConstructor();
                ctor.setAccessible(true);
                ctor.newInstance();
            } catch (InvocationTargetException e) {
                throw e.getCause();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(error.getMessage().contains("No io.github.photowey.mongoplus.core.util.Arrays"));
    }
}
