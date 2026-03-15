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
import java.util.List;

/**
 * Arrays - Enhanced array utilities.
 *
 * <p>Provides additional array operations beyond JDK java.util.Arrays,
 * with null-safe methods following the same naming conventions.</p>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/07
 */
public final class Arrays {

    private Arrays() {
        AssertionErrors.throwz(Arrays.class);
    }

    /**
     * Checks if the given byte array is null or empty.
     *
     * @param array the array to check
     * @return true if null or empty
     */
    public static boolean isEmpty(byte[] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    /**
     * Checks if the given array is null or empty.
     *
     * @param array the array to check
     * @return true if null or empty
     */
    public static <T> boolean isEmpty(T[] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    /**
     * Checks if the given int array is null or empty.
     *
     * @param array the array to check
     * @return true if null or empty
     */
    public static boolean isEmpty(int[] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    /**
     * Checks if the given long array is null or empty.
     *
     * @param array the array to check
     * @return true if null or empty
     */
    public static boolean isEmpty(long[] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    /**
     * Checks if the given array is not null and not empty.
     *
     * @param array the array to check
     * @return true if not null and not empty
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns a fixed-size list backed by the specified array.
     * This is a null-safe wrapper around {@link java.util.Arrays#asList(Object[])}.
     *
     * @param array the array to back the list
     * @return a list view of the array, or empty list if array is null
     */
    @SafeVarargs
    public static <T> List<T> asList(T... array) {
        if (isEmpty(array)) {
            return new ArrayList<>();
        }

        return java.util.Arrays.asList(array);
    }

    /**
     * Converts a collection to an array of the specified type.
     * This is a null-safe alternative to Collection#toArray().
     *
     * @param list the list to convert
     * @param type the component type of the resulting array
     * @return an array containing the elements of the list
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list, Class<T> type) {
        if (Collections.isEmpty(list)) {
            return (T[]) java.lang.reflect.Array.newInstance(type, 0);
        }

        return list.toArray((T[]) java.lang.reflect.Array.newInstance(type, list.size()));
    }

    /**
     * Checks if the given byte array contains the specified value.
     *
     * @param array the array to search
     * @param value the value to find
     * @return true if found
     */
    public static boolean contains(byte[] array, byte value) {
        if (isEmpty(array)) {
            return false;
        }
        for (byte element : array) {
            if (element == value) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given array contains the specified value.
     *
     * @param array the array to search
     * @param value the value to find
     * @return true if found
     */
    public static <T> boolean contains(T[] array, T value) {
        if (isEmpty(array)) {
            return false;
        }
        for (T element : array) {
            if (Objects.isNull(value) ? Objects.isNull(element) : value.equals(element)) {
                return true;
            }
        }

        return false;
    }
}
