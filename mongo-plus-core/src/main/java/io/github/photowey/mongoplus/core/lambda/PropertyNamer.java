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

import io.github.photowey.mongoplus.core.util.AssertionErrors;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.core.util.Strings;

/**
 * PropertyNamer - Converts method name to property name.
 * E.g., getName -> name, isActive -> active
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public final class PropertyNamer {

    private PropertyNamer() {
        AssertionErrors.throwz(PropertyNamer.class);
    }

    /**
     * Convert method name to property name.
     * Handles: getXxx -> xxx, isXxx -> xxx, setXxx -> xxx
     *
     * @param methodName the method name (e.g., "getName", "isActive")
     * @return the property name (e.g., "name", "active")
     */
    public static String methodToProperty(String methodName) {
        if (Strings.isEmpty(methodName)) {
            return "";
        }

        // Remove get/is/set prefix
        String name = methodName;
        if (methodName.startsWith("get") && methodName.length() > 3) {
            name = methodName.substring(3);
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            name = methodName.substring(2);
        } else if (methodName.startsWith("set") && methodName.length() > 3) {
            name = methodName.substring(3);
        }

        // Convert first character to lowercase
        if (Strings.isEmpty(name)) {
            return "";
        }

        return firstCharToLowerCase(name);
    }

    /**
     * Convert the first character to lowercase.
     */
    private static String firstCharToLowerCase(String str) {
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        char first = str.charAt(0);
        if (first >= 'A' && first <= 'Z') {
            return Character.toLowerCase(first) + str.substring(1);
        }
        return str;
    }

    /**
     * Check if the method is a property getter.
     */
    public static boolean isGetter(String methodName) {
        return Objects.nonNull(methodName)
            && ((methodName.startsWith("get")
            && methodName.length() > 3)
            || (methodName.startsWith("is")
            && methodName.length() > 2));
    }

    /**
     * Check if the method is a property setter.
     */
    public static boolean isSetter(String methodName) {
        return Objects.nonNull(methodName)
            && methodName.startsWith("set")
            && methodName.length() > 3;
    }
}
