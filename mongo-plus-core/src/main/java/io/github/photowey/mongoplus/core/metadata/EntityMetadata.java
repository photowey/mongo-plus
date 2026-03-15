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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EntityMetadata - Metadata for entity class mapping.
 * Contains collection name, field mappings, and ID field info.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityMetadata implements Serializable {

    private static final long serialVersionUID = 8288640810458834884L;

    /**
     * The entity class.
     */
    private Class<?> entityClass;

    /**
     * The MongoDB collection name.
     */
    private String collectionName;

    /**
     * Field mapping: property name -> database field name.
     * E.g., "userName" -> "user_name"
     */
    @Builder.Default
    private Map<String, String> fieldMapping = new HashMap<>();

    /**
     * The ID field name (property name).
     */
    private String idField;

    /**
     * The ID field name in database.
     */
    private String idColumn;

    /**
     * Get the database column name for a property.
     *
     * @param propertyName the property name (e.g., "userName")
     * @return the column name (e.g., "user_name"), or propertyName if not mapped
     */
    public String getColumnName(String propertyName) {
        return this.fieldMapping.getOrDefault(propertyName, propertyName);
    }

    /**
     * Get the property name for a database column (reverse lookup).
     *
     * @param columnName the column name
     * @return the property name, or columnName if not found
     */
    public String getPropertyName(String columnName) {
        for (Map.Entry<String, String> entry : this.fieldMapping.entrySet()) {
            if (entry.getValue().equals(columnName)) {
                return entry.getKey();
            }
        }

        return columnName;
    }

    /**
     * Add a field mapping.
     *
     * @param propertyName the property name
     * @param columnName   the database column name
     */
    public void addFieldMapping(String propertyName, String columnName) {
        this.fieldMapping.put(propertyName, columnName);
    }

    /**
     * Check if the entity has a specific field mapping.
     *
     * @param propertyName the property name
     * @return true if mapping exists
     */
    public boolean hasFieldMapping(String propertyName) {
        return this.fieldMapping.containsKey(propertyName);
    }
}
