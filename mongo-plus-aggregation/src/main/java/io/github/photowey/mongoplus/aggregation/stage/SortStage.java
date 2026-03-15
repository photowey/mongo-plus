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
package io.github.photowey.mongoplus.aggregation.stage;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bson.Document;

import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;
import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;

/**
 * SortStage - Models a MongoDB {@code $sort} stage.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class SortStage implements Stage {

    private final Map<String, Object> sortFields = new LinkedHashMap<>();

    /**
     * Creates an empty sort stage.
     */
    public SortStage() {
    }

    /**
     * Creates a sort stage with a single field definition.
     *
     * @param field     the field name to sort by
     * @param direction the MongoDB sort direction, usually {@code 1} or {@code -1}
     */
    public SortStage(String field, int direction) {
        this.sortFields.put(field, direction);
    }

    /**
     * Adds an ascending sort definition.
     *
     * @param field the field name to sort by
     * @return this stage
     */
    public SortStage asc(String field) {
        this.sortFields.put(field, 1);
        return this;
    }

    /**
     * Adds a descending sort definition.
     *
     * @param field the field name to sort by
     * @return this stage
     */
    public SortStage desc(String field) {
        this.sortFields.put(field, -1);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StageType getType() {
        return StageType.SORT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document toBson() {
        return new Document(
            MongoPlusConstants.SORT,
            new Document(this.sortFields)
        );
    }
}
