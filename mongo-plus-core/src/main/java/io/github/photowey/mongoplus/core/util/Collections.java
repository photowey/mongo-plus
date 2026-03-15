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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Collections - Enhanced collection utilities.
 *
 * <p>Provides additional collection operations beyond JDK java.util.Collections,
 * with null-safe methods following the same naming conventions.</p>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/07
 */
public final class Collections {

    private Collections() {
        AssertionErrors.throwz(Collections.class);
    }

    /**
     * Checks if the given collection is null or empty.
     *
     * @param collection the collection to check
     * @return true if null or empty
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    /**
     * Checks if the given map is null or empty.
     *
     * @param map the map to check
     * @return true if null or empty
     */
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return Objects.isNull(map) || map.isEmpty();
    }

    /**
     * Checks if the given collection is not null and not empty.
     *
     * @param collection the collection to check
     * @return true if not null and not empty
     */
    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    /**
     * Checks if the given map is not null and not empty.
     *
     * @param map the map to check
     * @return true if not null and not empty
     */
    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    /**
     * Returns a new mutable empty list.
     *
     * @return an empty list
     */
    public static <T> List<T> emptyList() {
        return new ArrayList<>(0);
    }

    /**
     * Returns a new mutable empty set.
     *
     * @return an empty set
     */
    public static <T> Set<T> emptySet() {
        return new HashSet<>(0);
    }

    /**
     * Returns a new mutable empty map.
     *
     * @return an empty map
     */
    public static <K, V> Map<K, V> emptyMap() {
        return new HashMap<>(0);
    }

    /**
     * Returns an immutable list containing only the specified element.
     *
     * @param element the single element
     * @return an immutable singleton list
     */
    public static <T> List<T> singletonList(T element) {
        return java.util.Collections.singletonList(element);
    }

    /**
     * Returns an immutable set containing only the specified element.
     *
     * @param element the single element
     * @return an immutable singleton set
     */
    public static <T> Set<T> singletonSet(T element) {
        return java.util.Collections.singleton(element);
    }

    /**
     * Returns an immutable map containing only the specified key-value mapping.
     *
     * @param key   the single key
     * @param value the single value
     * @return an immutable singleton map
     */
    public static <K, V> Map<K, V> singletonMap(K key, V value) {
        return java.util.Collections.singletonMap(key, value);
    }

    /**
     * Returns an unmodifiable list backed by the specified list.
     *
     * @param elements the list
     * @param <T>      the type of the elements
     * @return an unmodifiable list
     */
    public static <T> List<T> unmodifiableList(List<? extends T> elements) {
        return java.util.Collections.unmodifiableList(elements);
    }

    /**
     * Returns the first element of the collection, or null if empty.
     *
     * @param collection the collection
     * @return the first element, or null
     */
    public static <T> T first(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        if (collection instanceof List) {
            return ((List<T>) collection).get(0);
        }

        return collection.iterator().next();
    }

    /**
     * Returns the last element of the collection, or null if empty.
     *
     * @param collection the collection
     * @return the last element, or null
     */
    public static <T> T last(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }
        T last = null;
        for (T element : collection) {
            last = element;
        }

        return last;
    }

    /**
     * Returns the value to which the specified key is mapped, or defaultValue if null.
     *
     * @param map          the map
     * @param key          the key
     * @param defaultValue the default value
     * @return the value or default
     */
    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        if (isEmpty(map)) {
            return defaultValue;
        }
        V value = map.get(key);

        return Objects.defaultIfNull(value, defaultValue);
    }

    /**
     * Puts the key-value pair if the key is not already mapped or mapped to null.
     *
     * @param map   the map
     * @param key   the key
     * @param value the value
     * @return the previous value associated with key, or null
     */
    public static <K, V> V putIfAbsent(Map<K, V> map, K key, V value) {
        if (Objects.isNull(map)) {
            return null;
        }
        if (map.containsKey(key)) {
            return map.get(key);
        }
        map.put(key, value);

        return null;
    }
}
