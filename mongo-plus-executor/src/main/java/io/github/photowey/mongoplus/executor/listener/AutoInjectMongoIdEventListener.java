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
package io.github.photowey.mongoplus.executor.listener;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import io.github.photowey.mongoplus.annotation.BusinessId;
import io.github.photowey.mongoplus.annotation.MongoId;
import io.github.photowey.mongoplus.core.id.IdentifyGenerator;
import io.github.photowey.mongoplus.core.id.SnowflakeIdGenerator;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.core.util.Strings;

import lombok.Data;

/**
 * AutoInjectMongoIdEventListener - Event listener for automatic ID generation.
 *
 * <p>
 * Handles three scenarios:
 * 1. @MongoId with type != NONE: Generate ID using specified strategy
 * 2. @BusinessId: Copy business ID value to _id field
 * 3. Neither annotation: Let MongoDB generate ObjectId (default behavior)
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class AutoInjectMongoIdEventListener extends AbstractMongoEventListener<Object> {

    private final IdentifyGenerator identifyGenerator;
    private final ConcurrentHashMap<Class<?>, EntityIdMetadata> metadataCache = new ConcurrentHashMap<>();

    public AutoInjectMongoIdEventListener() {
        this(new SnowflakeIdGenerator());
    }

    public AutoInjectMongoIdEventListener(IdentifyGenerator identifyGenerator) {
        this.identifyGenerator = identifyGenerator;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object entity = event.getSource();
        if (Objects.isNull(entity)) {
            return;
        }

        EntityIdMetadata metadata = this.getMetadata(entity.getClass());
        if (Objects.isNull(metadata)) {
            return;
        }

        Object idValue = this.getFieldValue(entity, metadata.getIdField());

        // If ID is already set, do nothing
        if (Objects.nonNull(idValue) && Strings.isNotEmpty(idValue.toString())) {
            return;
        }

        if (Objects.nonNull(metadata.getMongoIdAnnotation())) {
            MongoId.IdType idType = metadata.getMongoIdAnnotation().type();
            Object generatedId = this.generateId(idType, metadata.getIdField().getType());
            if (Objects.nonNull(generatedId)) {
                this.setFieldValue(entity, metadata.getIdField(), generatedId);
            }

            return;
        }

        if (Objects.nonNull(metadata.getBusinessIdField())) {
            Object businessIdValue = this.getFieldValue(entity, metadata.getBusinessIdField());
            if (Objects.nonNull(businessIdValue) && Strings.isNotEmpty(businessIdValue.toString())) {
                this.setFieldValue(entity, metadata.getIdField(), businessIdValue);
            }
        }
    }

    /**
     * Generate ID based on the specified type.
     *
     * @param idType      the ID generation type
     * @param idFieldType the ID field type
     * @return the generated ID or null if no generation needed
     */
    private Object generateId(MongoId.IdType idType, Class<?> idFieldType) {
        switch (idType) {
            case NONE:
                return null;
            case AUTO:
                if (Long.class.equals(idFieldType) || long.class.equals(idFieldType)) {
                    return this.identifyGenerator.nextId();
                } else if (String.class.equals(idFieldType)) {
                    return this.identifyGenerator.nextIdString();
                }

                // Let MongoDB handle ObjectId
                return null;
            case ASSIGN_ID:
                if (Long.class.equals(idFieldType) || long.class.equals(idFieldType)) {
                    return this.identifyGenerator.nextId();
                } else if (String.class.equals(idFieldType)) {
                    return this.identifyGenerator.nextIdString();
                }
                throw new IllegalStateException(
                    "ASSIGN_ID only supports Long/long or String ID fields, but got: " + idFieldType
                );
            case ASSIGN_UUID:
                return UUID.randomUUID().toString().replace("-", "");
            default:
                return null;
        }
    }

    /**
     * Get or create metadata for the entity class.
     *
     * @param entityClass the entity class
     * @return the entity ID metadata
     */
    private EntityIdMetadata getMetadata(Class<?> entityClass) {
        return this.metadataCache.computeIfAbsent(entityClass, this::extractMetadata);
    }

    /**
     * Extract ID metadata from entity class.
     *
     * @param entityClass the entity class
     * @return the extracted metadata
     */
    private EntityIdMetadata extractMetadata(Class<?> entityClass) {
        EntityIdMetadata metadata = new EntityIdMetadata();
        int businessIdCount = 0;

        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);

            if (this.isIdField(field)) {
                metadata.setIdField(field);
                metadata.setMongoIdAnnotation(field.getAnnotation(MongoId.class));
            }

            BusinessId businessId = field.getAnnotation(BusinessId.class);
            if (Objects.nonNull(businessId) && businessId.copyToId()) {
                metadata.setBusinessIdField(field);
                businessIdCount++;
            }
        }

        if (businessIdCount > 1) {
            throw new IllegalStateException(
                "Entity class [" + entityClass.getName() + "] has multiple @BusinessId annotations. "
                    + "Only one field can be annotated with @BusinessId."
            );
        }

        if (Objects.isNull(metadata.getIdField()) && Objects.nonNull(entityClass.getSuperclass())) {
            return this.extractMetadata(entityClass.getSuperclass());
        }

        return Objects.nonNull(metadata.getIdField()) ? metadata : null;
    }

    /**
     * Check if the field is an ID field.
     *
     * @param field the field to check
     * @return true if it's an ID field
     */
    private boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(MongoId.class);
    }

    /**
     * Entity ID metadata holder.
     * Uses Lombok @Data to generate getters/setters.
     */
    @Data
    private static final class EntityIdMetadata {

        private Field idField;
        private MongoId mongoIdAnnotation;
        private Field businessIdField;
    }

    /**
     * Get field value from entity.
     *
     * @param entity the entity object
     * @param field  the field
     * @return the field value
     */
    private Object getFieldValue(Object entity, Field field) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get field value: " + field.getName(), e);
        }
    }

    /**
     * Set field value on entity.
     *
     * @param entity the entity object
     * @param field  the field
     * @param value  the value to set
     */
    private void setFieldValue(Object entity, Field field, Object value) {
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set field value: " + field.getName(), e);
        }
    }
}
