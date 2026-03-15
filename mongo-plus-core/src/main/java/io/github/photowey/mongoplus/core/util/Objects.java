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

import java.util.Comparator;

/**
 * Objects - Enhanced object utilities.
 *
 * <p>Provides additional object operations beyond JDK java.util.Objects,
 * with null-safe methods following the same naming conventions.</p>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/07
 */
public final class Objects {

    private Objects() {
        AssertionErrors.throwz(Objects.class);
    }

    /**
     * Checks if the given object is null.
     *
     * @param object the object to check
     * @return true if null
     */
    public static <T> boolean isNull(T object) {
        return java.util.Objects.isNull(object);
    }

    /**
     * Checks if the given object is not null.
     *
     * @param object the object to check
     * @return true if not null
     */
    public static <T> boolean nonNull(T object) {
        return java.util.Objects.nonNull(object);
    }

    /**
     * Returns the object if not null, otherwise returns the default value.
     *
     * @param object       the object
     * @param defaultValue the default value
     * @return the object or default value
     */
    public static <T> T defaultIfNull(T object, T defaultValue) {
        return nonNull(object) ? object : defaultValue;
    }

    /**
     * Requires that the object is not null, throws NullPointerException otherwise.
     *
     * @param object the object to check
     */
    public static <T> void requireNonNull(T object) {
        java.util.Objects.requireNonNull(object);
    }

    /**
     * Requires that the object is not null with a formatted message.
     *
     * @param object  the object to check
     * @param message the message template
     * @param args    the message arguments
     */
    public static <T> void requireNonNull(T object, String message, Object... args) {
        java.util.Objects.requireNonNull(object, String.format(message, args));
    }

    /**
     * Compares two objects for equality, handling nulls safely.
     *
     * @param a the first object
     * @param b the second object
     * @return true if both are null or equal
     */
    public static boolean equals(Object a, Object b) {
        return java.util.Objects.equals(a, b);
    }

    /**
     * Compares two objects using a comparator, handling nulls safely.
     * Null values are considered less than non-null values.
     *
     * @param a          the first object
     * @param b          the second object
     * @param comparator the comparator
     * @return negative, zero, or positive
     */
    public static <T> int compare(T a, T b, Comparator<? super T> comparator) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }

        return comparator.compare(a, b);
    }

    /**
     * Generates a hash code for a single object.
     *
     * @param object the object
     * @return the hash code, or 0 if null
     */
    public static int hashCode(Object object) {
        return nonNull(object) ? object.hashCode() : 0;
    }

    /**
     * Generates a hash code for multiple objects.
     *
     * @param objects the objects
     * @return the combined hash code
     */
    public static int hash(Object... objects) {
        return java.util.Objects.hash(objects);
    }

    /**
     * Returns the string representation of the object, or "null" if null.
     *
     * @param object the object
     * @return the string representation
     */
    public static String toString(Object object) {
        return String.valueOf(object);
    }

    /**
     * Returns the string representation of the object, or default value if null.
     *
     * @param object       the object
     * @param defaultValue the default value
     * @return the string representation or default
     */
    public static String toString(Object object, String defaultValue) {
        return nonNull(object) ? object.toString() : defaultValue;
    }
}
