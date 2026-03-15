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
package io.github.photowey.mongoplus.core.metadata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.photowey.mongoplus.core.util.AssertionErrors;
import io.github.photowey.mongoplus.core.util.Objects;

/**
 * EntityResolver - Resolves entity class to EntityMetadata with caching.
 * Delegates actual metadata extraction to a pluggable resolver strategy.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public final class EntityResolver {

    private static final EntityMetadataResolver DEFAULT_RESOLVER =
        new DefaultEntityMetadataResolver();
    private static final Map<Class<?>, EntityMetadata> METADATA_CACHE =
        new ConcurrentHashMap<>();

    private static volatile EntityMetadataResolver entityMetadataResolver =
        DEFAULT_RESOLVER;

    private EntityResolver() {
        AssertionErrors.throwz(EntityResolver.class);
    }

    /**
     * Resolve entity class to metadata (with caching).
     *
     * @param entityClass the entity class
     * @return the entity metadata
     */
    public static EntityMetadata resolve(Class<?> entityClass) {
        return METADATA_CACHE.computeIfAbsent(
            entityClass,
            key -> entityMetadataResolver.resolve(key)
        );
    }

    /**
     * Install a custom metadata resolver strategy.
     * Resets the cache so future resolutions use the new strategy.
     *
     * @param resolver the resolver strategy
     */
    public static void setEntityMetadataResolver(EntityMetadataResolver resolver) {
        entityMetadataResolver = Objects.nonNull(resolver)
            ? resolver
            : DEFAULT_RESOLVER;
        clearCache();
    }

    /**
     * Get the currently installed metadata resolver strategy.
     *
     * @return the current resolver strategy
     */
    public static EntityMetadataResolver getEntityMetadataResolver() {
        return entityMetadataResolver;
    }

    /**
     * Reset the resolver strategy back to the framework-neutral default.
     */
    public static void resetEntityMetadataResolver() {
        setEntityMetadataResolver(DEFAULT_RESOLVER);
    }

    /**
     * Get cached metadata (returns null if not cached).
     *
     * @param entityClass the entity class
     * @return the cached metadata or null
     */
    public static EntityMetadata getCached(Class<?> entityClass) {
        return METADATA_CACHE.get(entityClass);
    }

    /**
     * Check if metadata is cached for the class.
     *
     * @param entityClass the entity class
     * @return true if cached
     */
    public static boolean isCached(Class<?> entityClass) {
        return METADATA_CACHE.containsKey(entityClass);
    }

    /**
     * Clear all cached metadata.
     */
    public static void clearCache() {
        METADATA_CACHE.clear();
    }
}
