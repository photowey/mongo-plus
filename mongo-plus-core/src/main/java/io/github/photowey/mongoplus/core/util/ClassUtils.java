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
 * ClassUtils - Class loading and introspection utilities.
 *
 * <p>Provides class loading and type checking operations to replace
 * Spring's ClassUtils dependency, with null-safe methods.</p>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/07
 */
public final class ClassUtils {

    private static final char PACKAGE_SEPARATOR = '.';
    private static final char INNER_CLASS_SEPARATOR = '$';

    private ClassUtils() {
        AssertionErrors.throwz(ClassUtils.class);
    }

    /**
     * Loads a class by name using the default class loader.
     *
     * @param className the fully qualified class name
     * @return the Class object
     * @throws IllegalArgumentException if class not found
     */
    public static Class<?> forName(String className) {
        return forName(className, getDefaultClassLoader());
    }

    /**
     * Loads a class by name using the specified class loader.
     *
     * @param className   the fully qualified class name
     * @param classLoader the class loader to use
     * @return the Class object
     * @throws IllegalArgumentException if class not found
     */
    public static Class<?> forName(String className, ClassLoader classLoader) {
        if (Strings.isBlank(className)) {
            throw new IllegalArgumentException("Class name must not be blank");
        }
        try {
            return loadClass(className, classLoader);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found: " + className, e);
        }
    }

    /**
     * Checks if the specified class is present in the classpath.
     *
     * @param className the fully qualified class name
     * @return true if class is present
     */
    public static boolean isPresent(String className) {
        return isPresent(className, getDefaultClassLoader());
    }

    /**
     * Checks if the specified class is present in the classpath using given loader.
     *
     * @param className   the fully qualified class name
     * @param classLoader the class loader to use
     * @return true if class is present
     */
    public static boolean isPresent(String className, ClassLoader classLoader) {
        if (Strings.isBlank(className)) {
            return false;
        }
        try {
            loadClass(className, classLoader);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Returns the short name (without package) of the given class.
     *
     * @param className the fully qualified class name
     * @return the short class name
     */
    public static String getShortName(String className) {
        if (Strings.isBlank(className)) {
            return className;
        }
        int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        int nameStartIndex = (lastDotIndex != -1 ? lastDotIndex + 1 : 0);
        int innerIndex = className.indexOf(INNER_CLASS_SEPARATOR, nameStartIndex);
        String shortName = className.substring(nameStartIndex);
        if (innerIndex != -1) {
            shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
        }

        return shortName;
    }

    /**
     * Returns the short name (without package) of the given class.
     *
     * @param clazz the class
     * @return the short class name
     */
    public static String getShortName(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return null;
        }

        return getShortName(clazz.getName());
    }

    /**
     * Returns the package name of the given class.
     *
     * @param className the fully qualified class name
     * @return the package name, or empty string if no package
     */
    public static String getPackageName(String className) {
        if (Strings.isBlank(className)) {
            return "";
        }
        int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        return (lastDotIndex != -1 ? className.substring(0, lastDotIndex) : "");
    }

    /**
     * Returns the package name of the given class.
     *
     * @param clazz the class
     * @return the package name
     */
    public static String getPackageName(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return null;
        }

        return getPackageName(clazz.getName());
    }

    /**
     * Checks if the given class is assignable from the other class.
     * That is, checks if target can be assigned to a variable of type source.
     *
     * @param source the source class
     * @param target the target class to check
     * @return true if target is assignable to source
     */
    public static boolean isAssignable(Class<?> source, Class<?> target) {
        if (Objects.isNull(source) || Objects.isNull(target)) {
            return false;
        }
        if (source.isAssignableFrom(target)) {
            return true;
        }
        // Handle primitive wrapper conversions
        if (source.isPrimitive()) {
            return isPrimitiveWrapperOf(target, source);
        }
        if (target.isPrimitive()) {
            return isPrimitiveWrapperOf(source, target);
        }

        return false;
    }

    /**
     * Determines if the given class is a primitive wrapper for the primitive type.
     *
     * @param clazz     the class to check
     * @param primitive the primitive type
     * @return true if clazz is a wrapper for primitive
     */
    public static boolean isPrimitiveWrapperOf(Class<?> clazz, Class<?> primitive) {
        if (!primitive.isPrimitive()) {
            return false;
        }
        if (primitive == boolean.class) {
            return clazz == Boolean.class;
        }
        if (primitive == byte.class) {
            return clazz == Byte.class;
        }
        if (primitive == char.class) {
            return clazz == Character.class;
        }
        if (primitive == short.class) {
            return clazz == Short.class;
        }
        if (primitive == int.class) {
            return clazz == Integer.class;
        }
        if (primitive == long.class) {
            return clazz == Long.class;
        }
        if (primitive == float.class) {
            return clazz == Float.class;
        }
        if (primitive == double.class) {
            return clazz == Double.class;
        }

        return false;
    }

    /**
     * Checks if the given class is a primitive or primitive wrapper type.
     *
     * @param clazz the class to check
     * @return true if primitive or wrapper
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return false;
        }

        return clazz.isPrimitive() || isPrimitiveWrapper(clazz);
    }

    /**
     * Checks if the given class is a primitive wrapper type.
     *
     * @param clazz the class to check
     * @return true if a primitive wrapper
     */
    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        return clazz == Boolean.class
            || clazz == Byte.class
            || clazz == Character.class
            || clazz == Short.class
            || clazz == Integer.class
            || clazz == Long.class
            || clazz == Float.class
            || clazz == Double.class;
    }

    private static Class<?> loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        // Handle array notation
        if (className.startsWith("[")) {
            return Class.forName(className, false, classLoader);
        }
        // Try primitive types first
        Class<?> primitiveClass = resolvePrimitiveClassName(className);
        if (primitiveClass != null) {
            return primitiveClass;
        }
        // Try standard class loading
        return Class.forName(className, false, classLoader);
    }

    private static Class<?> resolvePrimitiveClassName(String name) {
        if ("boolean".equals(name)) {
            return boolean.class;
        }
        if ("byte".equals(name)) {
            return byte.class;
        }
        if ("char".equals(name)) {
            return char.class;
        }
        if ("short".equals(name)) {
            return short.class;
        }
        if ("int".equals(name)) {
            return int.class;
        }
        if ("long".equals(name)) {
            return long.class;
        }
        if ("float".equals(name)) {
            return float.class;
        }
        if ("double".equals(name)) {
            return double.class;
        }
        if ("void".equals(name)) {
            return void.class;
        }

        return null;
    }

    private static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ignored) {
            // ignored
        }
        if (Objects.isNull(cl)) {
            cl = ClassUtils.class.getClassLoader();
        }

        return cl;
    }
}
