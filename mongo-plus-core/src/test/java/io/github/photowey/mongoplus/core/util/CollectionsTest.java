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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CollectionsTest - Unit tests for Collections utility class.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("CollectionsTest")
class CollectionsTest {

    @Test
    @DisplayName("Given null or empty collection When isEmpty Then returns true")
    @Story("Check null or empty collection")
    void givenNullOrEmptyCollection_whenIsEmpty_thenReturnsTrue() {
        assertTrue(Collections.isEmpty((Collection<?>) null));
        assertTrue(Collections.isEmpty(new ArrayList<>()));
        assertTrue(Collections.isEmpty(new HashSet<>()));
        assertFalse(Collections.isEmpty(Arrays.asList("a")));
        assertFalse(Collections.isEmpty(Arrays.asList("a", "b")));
    }

    @Test
    @DisplayName("Given null or empty map When isEmpty Then returns true")
    @Story("Check null or empty map")
    void givenNullOrEmptyMap_whenIsEmpty_thenReturnsTrue() {
        assertTrue(Collections.isEmpty((Map<?, ?>) null));
        assertTrue(Collections.isEmpty(new HashMap<>()));
        assertFalse(Collections.isEmpty(Collections.singletonMap("key", "value")));
    }

    @Test
    @DisplayName("Given non-empty collection When isNotEmpty Then returns true")
    @Story("Check non-empty collection")
    void givenNonEmptyCollection_whenIsNotEmpty_thenReturnsTrue() {
        assertFalse(Collections.isNotEmpty((Collection<?>) null));
        assertFalse(Collections.isNotEmpty(new ArrayList<>()));
        assertTrue(Collections.isNotEmpty(Arrays.asList("a")));
    }

    @Test
    @DisplayName("Given non-empty map When isNotEmpty Then returns true")
    @Story("Check non-empty map")
    void givenNonEmptyMap_whenIsNotEmpty_thenReturnsTrue() {
        assertFalse(Collections.isNotEmpty((Map<?, ?>) null));
        assertFalse(Collections.isNotEmpty(new HashMap<>()));
        assertTrue(Collections.isNotEmpty(Collections.singletonMap("key", "value")));
    }

    @Test
    @DisplayName("When emptyList Then returns mutable empty list")
    @Story("Create mutable empty list")
    void whenEmptyList_thenReturnsMutableEmptyList() {
        List<String> list = Collections.emptyList();
        assertNotNull(list);
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("When emptySet Then returns mutable empty set")
    @Story("Create mutable empty set")
    void whenEmptySet_thenReturnsMutableEmptySet() {
        Set<String> set = Collections.emptySet();
        assertNotNull(set);
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
    }

    @Test
    @DisplayName("When emptyMap Then returns mutable empty map")
    @Story("Create mutable empty map")
    void whenEmptyMap_thenReturnsMutableEmptyMap() {
        Map<String, String> map = Collections.emptyMap();
        assertNotNull(map);
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    @DisplayName("Given element When singletonList Then returns immutable list with one element")
    @Story("Create singleton list")
    void givenElement_whenSingletonList_thenReturnsImmutableList() {
        List<String> list = Collections.singletonList("element");
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("element", list.get(0));
        assertThrows(UnsupportedOperationException.class, () -> list.add("another"));
    }

    @Test
    @DisplayName("Given element When singletonSet Then returns immutable set with one element")
    @Story("Create singleton set")
    void givenElement_whenSingletonSet_thenReturnsImmutableSet() {
        Set<String> set = Collections.singletonSet("element");
        assertNotNull(set);
        assertEquals(1, set.size());
        assertTrue(set.contains("element"));
        assertThrows(UnsupportedOperationException.class, () -> set.add("another"));
    }

    @Test
    @DisplayName("Given key and value When singletonMap Then returns immutable map")
    @Story("Create singleton map")
    void givenKeyAndValue_whenSingletonMap_thenReturnsImmutableMap() {
        Map<String, Integer> map = Collections.singletonMap("key", 42);
        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals(42, map.get("key"));
        assertThrows(UnsupportedOperationException.class, () -> map.put("key2", 2));
    }

    @Test
    @DisplayName("Given non-empty collection When first Then returns first element")
    @Story("Get first element from collection")
    void givenNonEmptyCollection_whenFirst_thenReturnsFirstElement() {
        assertNull(Collections.first(null));
        assertNull(Collections.first(new ArrayList<>()));

        List<String> list = Arrays.asList("a", "b", "c");
        assertEquals("a", Collections.first(list));

        Set<String> set = new HashSet<>(Arrays.asList("a", "b"));
        assertNotNull(Collections.first(set));
        assertTrue(set.contains(Collections.first(set)));
    }

    @Test
    @DisplayName("Given non-empty collection When last Then returns last element")
    @Story("Get last element from collection")
    void givenNonEmptyCollection_whenLast_thenReturnsLastElement() {
        assertNull(Collections.last(null));
        assertNull(Collections.last(new ArrayList<>()));

        List<String> list = Arrays.asList("a", "b", "c");
        assertEquals("c", Collections.last(list));

        Set<String> set = new HashSet<>(Arrays.asList("a", "b"));
        assertNotNull(Collections.last(set));
        assertTrue(set.contains(Collections.last(set)));
    }

    @Test
    @DisplayName("Given map and key When getOrDefault Then returns value or default")
    @Story("Get map value or default")
    void givenMapAndKey_whenGetOrDefault_thenReturnsValueOrDefault() {
        Map<String, Integer> map = new HashMap<>();
        map.put("existing", 42);

        assertEquals(42, Collections.getOrDefault(map, "existing", 0));
        assertEquals(0, Collections.getOrDefault(map, "missing", 0));
        assertEquals(0, Collections.getOrDefault(null, "key", 0));
        assertEquals(0, Collections.getOrDefault(new HashMap<>(), "key", 0));
    }

    @Test
    @DisplayName("Given map and absent key When putIfAbsent Then puts and returns null")
    @Story("Put if absent for map")
    void givenMapAndAbsentKey_whenPutIfAbsent_thenPutsAndReturnsNull() {
        Map<String, Integer> map = new HashMap<>();

        assertNull(Collections.putIfAbsent(map, "key", 42));
        assertEquals(42, map.get("key"));

        assertEquals(42, Collections.putIfAbsent(map, "key", 100));
        assertEquals(42, map.get("key"));

        assertNull(Collections.putIfAbsent(null, "key", 42));
    }

    @Test
    @DisplayName("Given Collections class When instantiate via reflection Then throws AssertionError")
    @Story("Utility class rejects reflection instantiation")
    void givenCollectionsClass_whenInstantiateViaReflection_thenThrowsAssertionError() {
        AssertionError error = assertThrows(AssertionError.class, () -> {
            try {
                Constructor<Collections> ctor = Collections.class.getDeclaredConstructor();
                ctor.setAccessible(true);
                ctor.newInstance();
            } catch (InvocationTargetException e) {
                throw e.getCause();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(error.getMessage().contains("No io.github.photowey.mongoplus.core.util.Collections"));
    }
}
