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

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.photowey.mongoplus.core.util.AssertionErrors;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.core.util.Strings;

/**
 * LambdaUtils - Utility for resolving lambda to field names with two-level caching.
 * Level 1: Full lambda signature -> field name
 * Level 2: Class -> (method name -> field name)
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public final class LambdaUtils {

    /**
     * Level 1 cache: full lambda signature -> field name
     * Key format: implClass + "." + implMethodName
     */
    private static final Map<String, String> LAMBDA_CACHE = new ConcurrentHashMap<>();

    /**
     * Level 2 cache: Class -> (method name -> field name)
     */
    private static final Map<Class<?>, Map<String, String>> CLASS_FIELD_CACHE = new ConcurrentHashMap<>();

    private LambdaUtils() {
        AssertionErrors.throwz(LambdaUtils.class);
    }

    /**
     * Resolve field name from SFunction with caching.
     *
     * @param <T> the entity type
     * @param <R> the return type
     * @param fn  the serializable function
     * @return the field name (e.g., "name" from User::getName)
     */
    public static <T, R> String resolve(SFunction<T, R> fn) {
        if (Objects.isNull(fn)) {
            return null;
        }
        SerializedLambda lambda = LambdaMeta.extract(fn);
        String implClass = LambdaMeta.getImplClassName(lambda);
        String implMethod = LambdaMeta.getImplMethodName(lambda);

        // Level 1 cache: full signature
        String cacheKey = implClass + "." + implMethod;
        String cached = LAMBDA_CACHE.get(cacheKey);
        if (Strings.isNotEmpty(cached)) {
            return cached;
        }

        String fieldName = PropertyNamer.methodToProperty(implMethod);

        if (!PropertyNamer.isGetter(implMethod)) {
            throw new IllegalArgumentException(
                "Method " + implMethod + " is not a valid property getter (must start with 'get' or 'is')"
            );
        }

        // Update Level 1 cache
        LAMBDA_CACHE.put(cacheKey, fieldName);

        // Update Level 2 cache: class -> method -> field
        try {
            Class<?> clazz = Class.forName(implClass);
            CLASS_FIELD_CACHE.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>())
                .put(implMethod, fieldName);
        } catch (ClassNotFoundException e) {
            // Ignore class loading error, L1 cache still works
        }

        return fieldName;
    }

    /**
     * Resolve field name without throwing exception for non-getter methods.
     * Returns empty string if not a valid getter.
     *
     * @param <T> the entity type
     * @param <R> the return type
     * @param fn  the serializable function
     * @return the field name or empty string
     */
    public static <T, R> String resolveQuietly(SFunction<T, R> fn) {
        try {
            return resolve(fn);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get cached field name for a class and method (Level 2 cache).
     *
     * @param clazz      the entity class
     * @param methodName the method name (e.g., "getName")
     * @return the field name or null if not cached
     */
    public static String getCachedFieldName(Class<?> clazz, String methodName) {
        Map<String, String> methodMap = CLASS_FIELD_CACHE.get(clazz);
        if (Collections.isNotEmpty(methodMap)) {
            return methodMap.get(methodName);
        }

        return null;
    }

    /**
     * Pre-cache all getter methods of a class (for optimization).
     *
     * @param clazz the entity class to pre-cache
     */
    public static void preCacheClass(Class<?> clazz) {
        CLASS_FIELD_CACHE.computeIfAbsent(clazz, k -> {
            Map<String, String> ctx = new ConcurrentHashMap<>();
            for (Method method : clazz.getMethods()) {
                String methodName = method.getName();
                if (PropertyNamer.isGetter(methodName)) {
                    String fieldName = PropertyNamer.methodToProperty(methodName);
                    ctx.put(methodName, fieldName);
                }
            }

            return ctx;
        });
    }

    /**
     * Clear all caches.
     */
    public static void clearCache() {
        LAMBDA_CACHE.clear();
        CLASS_FIELD_CACHE.clear();
    }

    /**
     * Get cache statistics.
     */
    public static String getCacheStats() {
        return "LambdaCache: " + LAMBDA_CACHE.size() + ", ClassFieldCache: " + CLASS_FIELD_CACHE.size();
    }
}
