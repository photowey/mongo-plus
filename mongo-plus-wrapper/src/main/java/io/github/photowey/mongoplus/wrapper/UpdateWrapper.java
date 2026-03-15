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
package io.github.photowey.mongoplus.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.photowey.mongoplus.core.metadata.FieldMetadata;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.dsl.ast.node.LogicalNode;
import io.github.photowey.mongoplus.wrapper.core.condition.Condition;
import io.github.photowey.mongoplus.wrapper.core.enums.Segment;

import lombok.Getter;

/**
 * UpdateWrapper - Wrapper for update operations with SET clauses.
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Getter
public class UpdateWrapper<T> extends AbstractWrapper<T> {

    /**
     * SET clauses: field -> value
     */
    private final Map<String, Object> setValues = new HashMap<>();

    /**
     * INC clauses: field -> increment value
     */
    private final Map<String, Number> incValues = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected UpdateWrapper<T> instance() {
        return new UpdateWrapper<>();
    }

    /**
     * SET field = value
     */
    public UpdateWrapper<T> set(String field, Object value) {
        this.setValues.put(field, value);
        return this;
    }

    /**
     * SET field = value
     */
    public UpdateWrapper<T> set(FieldMetadata<?> field, Object value) {
        return this.set(this.resolveField(field), value);
    }

    /**
     * SET field = null
     */
    public UpdateWrapper<T> setNull(String field) {
        this.setValues.put(field, null);
        return this;
    }

    /**
     * SET field = null
     */
    public UpdateWrapper<T> setNull(FieldMetadata<?> field) {
        return this.setNull(this.resolveField(field));
    }

    /**
     * INC field by value
     */
    public UpdateWrapper<T> inc(String field, Number value) {
        this.incValues.put(field, value);
        return this;
    }

    /**
     * INC field by value
     */
    public UpdateWrapper<T> inc(FieldMetadata<?> field, Number value) {
        return this.inc(this.resolveField(field), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateWrapper<T> and(Consumer<AbstractWrapper<T>> consumer) {
        UpdateWrapper<T> nested = new UpdateWrapper<>();
        consumer.accept(nested);
        this.conditions.add(
            Condition.builder()
                .segment(Segment.AND)
                .nestedWrapper(nested)
                .build()
        );
        if (Collections.isNotEmpty(nested.currentLogical.getChildren())) {
            this.currentLogical.addChild(nested.currentLogical);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateWrapper<T> or(Consumer<AbstractWrapper<T>> consumer) {
        UpdateWrapper<T> nested = new UpdateWrapper<>();
        consumer.accept(nested);
        this.conditions.add(
            Condition.builder()
                .segment(Segment.OR)
                .nestedWrapper(nested)
                .build()
        );
        LogicalNode orNode = LogicalNode.or();
        if (Collections.isNotEmpty(nested.currentLogical.getChildren())) {
            orNode.getChildren().addAll(nested.currentLogical.getChildren());
        }
        this.currentLogical.addChild(orNode);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateWrapper<T> clone() {
        UpdateWrapper<T> cloned = new UpdateWrapper<>();
        this.copyStateTo(cloned);
        cloned.setValues().putAll(this.getSetValues());
        cloned.incValues().putAll(this.getIncValues());

        return cloned;
    }

    /**
     * Determines whether this wrapper contains at least one update assignment.
     *
     * @return {@code true} when the wrapper contains set or increment operations
     */
    public boolean hasUpdates() {
        return Collections.isNotEmpty(this.setValues) || Collections.isNotEmpty(this.incValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateWrapper<T> paginate(long pageNum, long pageSize) {
        return (UpdateWrapper<T>) super.paginate(pageNum, pageSize);
    }

    // ----------------------------------------------------------------

    /**
     * Returns the collected set assignments.
     *
     * @return the collected set assignments
     */
    public Map<String, Object> setValues() {
        return this.setValues;
    }

    /**
     * Returns the collected increment assignments.
     *
     * @return the collected increment assignments
     */
    public Map<String, Number> incValues() {
        return this.incValues;
    }
}

