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

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;
import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;

/**
 * ProjectStage - Models a MongoDB {@code $project} stage.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class ProjectStage implements Stage {

    private final Map<String, Object> fields = new HashMap<>();

    /**
     * Creates an empty projection stage.
     */
    public ProjectStage() {
    }

    /**
     * Creates a projection stage and includes the supplied fields.
     *
     * @param fieldNames the field names to include
     */
    public ProjectStage(String... fieldNames) {
        this.include(fieldNames);
    }

    /**
     * Includes the supplied fields in the projection.
     *
     * @param fieldNames the field names to include
     * @return this stage
     */
    public ProjectStage include(String... fieldNames) {
        for (String field : fieldNames) {
            this.fields.put(field, 1);
        }
        return this;
    }

    /**
     * Excludes the supplied fields from the projection.
     *
     * @param fieldNames the field names to exclude
     * @return this stage
     */
    public ProjectStage exclude(String... fieldNames) {
        for (String field : fieldNames) {
            this.fields.put(field, 0);
        }
        return this;
    }

    /**
     * Adds a computed projection field.
     *
     * @param alias      the output field alias
     * @param expression the MongoDB aggregation expression
     * @return this stage
     */
    public ProjectStage computed(String alias, String expression) {
        this.fields.put(alias, expression);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StageType getType() {
        return StageType.PROJECT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document toBson() {
        return new Document(MongoPlusConstants.PROJECT, new Document(this.fields));
    }
}
