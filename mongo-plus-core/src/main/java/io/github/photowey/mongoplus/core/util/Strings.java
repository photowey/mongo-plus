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

/**
 * Strings - Enhanced string utilities.
 *
 * <p>Provides additional string operations beyond JDK String,
 * with null-safe methods following the same naming conventions.</p>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public final class Strings {

    private static final char UNDERLINE = '_';

    private Strings() {
        AssertionErrors.throwz(Strings.class);
    }

    /**
     * Checks if the given string is null or empty.
     *
     * @param str the string to check
     * @return true if null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Checks if the given string is not null and not empty.
     *
     * @param str the string to check
     * @return true if not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Checks if the given string is null, empty, or contains only whitespace.
     *
     * @param str the string to check
     * @return true if null, empty, or blank
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Checks if the given string is not null, not empty, and contains non-whitespace characters.
     *
     * @param str the string to check
     * @return true if not null, not empty, and not blank
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Returns the given string if it is not null, otherwise returns the default value.
     *
     * @param txt          the string
     * @param defaultValue the default value
     * @return the string or default value
     */
    public static String defaultIfNull(String txt, String defaultValue) {
        return Objects.nonNull(txt) ? txt : defaultValue;
    }

    /**
     * Returns the given string if it is not empty, otherwise returns the default value.
     *
     * @param txt          the string
     * @param defaultValue the default value
     * @return the string or default value
     */
    public static String defaultIfEmpty(String txt, String defaultValue) {
        return isNotEmpty(txt) ? txt : defaultValue;
    }

    /**
     * Returns the given string if it is not blank, otherwise returns the default value.
     *
     * @param txt          the string
     * @param defaultValue the default value
     * @return the string or default value
     */
    public static String defaultIfBlank(String txt, String defaultValue) {
        return isNotBlank(txt) ? txt : defaultValue;
    }

    /**
     * Converts the first character of the given string to uppercase.
     *
     * @param str the string to convert
     * @return the capitalized string, or null if input is null
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        char first = str.charAt(0);
        if (Character.isUpperCase(first)) {
            return str;
        }

        return Character.toUpperCase(first) + str.substring(1);
    }

    /**
     * Converts the first character of the given string to lowercase.
     *
     * @param txt the string to convert
     * @return the uncapitalized string, or null if input is null
     */
    public static String uncapitalize(String txt) {
        if (isEmpty(txt)) {
            return txt;
        }
        char first = txt.charAt(0);
        if (Character.isLowerCase(first)) {
            return txt;
        }

        return Character.toLowerCase(first) + txt.substring(1);
    }

    /**
     * Converts a camelCase string to snake_case.
     *
     * @param str the camelCase string
     * @return the snake_case string
     */
    public static String camelToUnderline(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    buf.append(UNDERLINE);
                }
                buf.append(Character.toLowerCase(c));
            } else {
                buf.append(c);
            }
        }

        return buf.toString();
    }

    /**
     * Converts a snake_case string to camelCase.
     *
     * @param txt the snake_case string
     * @return the camelCase string
     */
    public static String underlineToCamel(String txt) {
        if (isEmpty(txt)) {
            return txt;
        }

        StringBuilder sb = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);
            if (c == UNDERLINE) {
                nextUpperCase = true;
            } else if (nextUpperCase) {
                sb.append(Character.toUpperCase(c));
                nextUpperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * Trims the given string, returning null if the result is empty.
     *
     * @param str the string to trim
     * @return the trimmed string, or null if empty
     */
    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }
        String trimmed = str.trim();

        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Trims the given string, returning empty string if null.
     *
     * @param txt the string to trim
     * @return the trimmed string, or empty string if null
     */
    public static String trimToEmpty(String txt) {
        return Objects.isNull(txt) ? "" : txt.trim();
    }

    /**
     * Splits the given string by the specified delimiter.
     *
     * @param txt       the string to split
     * @param delimiter the delimiter
     * @return array of split parts, or empty array if input is null/empty
     */
    public static String[] split(String txt, char delimiter) {
        if (isEmpty(txt)) {
            return new String[0];
        }

        return txt.split(String.valueOf(delimiter));
    }

    /**
     * Splits the given string by the specified delimiter (regex).
     *
     * @param txt       the string to split
     * @param delimiter the delimiter regex
     * @return array of split parts, or empty array if input is null/empty
     */
    public static String[] split(String txt, String delimiter) {
        if (isEmpty(txt)) {
            return new String[0];
        }

        return txt.split(delimiter);
    }
}
