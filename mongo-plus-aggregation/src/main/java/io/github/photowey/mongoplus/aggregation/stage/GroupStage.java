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
 * GroupStage - Models a MongoDB {@code $group} stage with accumulator definitions.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class GroupStage implements Stage {

    private final Object groupKey;
    private final Map<String, Object> accumulators = new HashMap<>();

    /**
     * Creates a group stage keyed by a document field.
     *
     * @param field the field name used as the group identifier
     */
    public GroupStage(String field) {
        this.groupKey = "$" + field;
    }

    /**
     * Creates a group stage with a raw group key expression.
     *
     * @param groupKey the raw MongoDB group key expression
     */
    public GroupStage(Object groupKey) {
        this.groupKey = groupKey;
    }

    /**
     * Adds a {@code $sum} accumulator and returns the current stage.
     *
     * @param field the source field name
     * @param alias the output field alias
     * @return this stage
     */
    public GroupStage sum(String field, String alias) {
        this.accumulators.put(
            alias,
            new Document(MongoPlusConstants.SUM, "$" + field)
        );
        return this;
    }

    /**
     * Adds an {@code $avg} accumulator and returns the current stage.
     *
     * @param field the source field name
     * @param alias the output field alias
     * @return this stage
     */
    public GroupStage avg(String field, String alias) {
        this.accumulators.put(
            alias,
            new Document(MongoPlusConstants.AVG, "$" + field)
        );
        return this;
    }

    /**
     * Adds a document count accumulator and returns the current stage.
     *
     * @param alias the output field alias
     * @return this stage
     */
    public GroupStage count(String alias) {
        this.accumulators.put(alias, new Document(MongoPlusConstants.SUM, 1));
        return this;
    }

    /**
     * Adds a {@code $max} accumulator and returns the current stage.
     *
     * @param field the source field name
     * @param alias the output field alias
     * @return this stage
     */
    public GroupStage max(String field, String alias) {
        this.accumulators.put(
            alias,
            new Document(MongoPlusConstants.MAX, "$" + field)
        );
        return this;
    }

    /**
     * Adds a {@code $min} accumulator and returns the current stage.
     *
     * @param field the source field name
     * @param alias the output field alias
     * @return this stage
     */
    public GroupStage min(String field, String alias) {
        this.accumulators.put(
            alias,
            new Document(MongoPlusConstants.MIN, "$" + field)
        );
        return this;
    }

    /**
     * Adds a {@code $sum} accumulator for callers that mutate an existing group stage.
     *
     * @param field the source field name
     * @param alias the output field alias
     */
    public void addSum(String field, String alias) {
        this.accumulators.put(
            alias,
            new Document(MongoPlusConstants.SUM, "$" + field)
        );
    }

    /**
     * Adds an {@code $avg} accumulator for callers that mutate an existing group stage.
     *
     * @param field the source field name
     * @param alias the output field alias
     */
    public void addAvg(String field, String alias) {
        this.accumulators.put(
            alias,
            new Document(MongoPlusConstants.AVG, "$" + field)
        );
    }

    /**
     * Adds a document count accumulator for callers that mutate an existing group stage.
     *
     * @param alias the output field alias
     */
    public void addCount(String alias) {
        this.accumulators.put(alias, new Document(MongoPlusConstants.SUM, 1));
    }

    /**
     * Adds a {@code $max} accumulator for callers that mutate an existing group stage.
     *
     * @param field the source field name
     * @param alias the output field alias
     */
    public void addMax(String field, String alias) {
        this.accumulators.put(
            alias,
            new Document(MongoPlusConstants.MAX, "$" + field)
        );
    }

    /**
     * Adds a {@code $min} accumulator for callers that mutate an existing group stage.
     *
     * @param field the source field name
     * @param alias the output field alias
     */
    public void addMin(String field, String alias) {
        this.accumulators.put(
            alias,
            new Document(MongoPlusConstants.MIN, "$" + field)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StageType getType() {
        return StageType.GROUP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document toBson() {
        Document groupDoc = new Document();
        groupDoc.put(MongoPlusConstants.ID, this.groupKey);
        groupDoc.putAll(this.accumulators);

        return new Document(MongoPlusConstants.GROUP, groupDoc);
    }
}
