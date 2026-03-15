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
package io.github.photowey.mongoplus.aggregation.stage.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bson.Document;

import io.github.photowey.mongoplus.aggregation.stage.GroupStage;
import io.github.photowey.mongoplus.aggregation.stage.LimitStage;
import io.github.photowey.mongoplus.aggregation.stage.LookupStage;
import io.github.photowey.mongoplus.aggregation.stage.MatchStage;
import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.ProjectStage;
import io.github.photowey.mongoplus.aggregation.stage.SkipStage;
import io.github.photowey.mongoplus.aggregation.stage.SortStage;
import io.github.photowey.mongoplus.aggregation.stage.Stage;
import io.github.photowey.mongoplus.core.lambda.LambdaUtils;
import io.github.photowey.mongoplus.core.lambda.SFunction;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.query.builder.QueryBuilder;
import io.github.photowey.mongoplus.wrapper.LambdaQueryWrapper;

/**
 * AggregationWrapper - Provides a fluent DSL for building MongoDB aggregation pipelines.
 *
 * <p>The wrapper collects ordered {@link Stage stages} and keeps group-state so accumulator methods
 * can append to the current {@code $group} block.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * AggregationWrapper<UserDocument> wrapper = AggregationWrapper.aggregation(UserDocument.class)
 *     .match(query -> query.eq(UserDocument::getStatus, 1))
 *     .group(UserDocument::getDepartmentId)
 *     .count("total")
 *     .sortDesc("total")
 *     .limit(10);
 * Pipeline pipeline = wrapper.build();
 * }</pre>
 *
 * @param <T> the input entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class AggregationWrapper<T> {

    private final Class<T> entityClass;
    private final List<Stage> stages = new ArrayList<>();
    private GroupStage currentGroup;

    /**
     * Creates a wrapper for the supplied entity type.
     *
     * @param entityClass the entity class associated with the pipeline
     */
    public AggregationWrapper(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Creates a new aggregation wrapper for the supplied entity type.
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * AggregationWrapper<UserDocument> wrapper = AggregationWrapper.aggregation(UserDocument.class);
     * }</pre>
     *
     * @param entityClass the entity class associated with the pipeline
     * @param <T>         the entity type
     * @return a new aggregation wrapper instance
     */
    public static <T> AggregationWrapper<T> aggregation(Class<T> entityClass) {
        return new AggregationWrapper<>(entityClass);
    }

    /**
     * Adds a {@code $match} stage built from a temporary lambda query wrapper.
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * wrapper.match(query -> query.gte(UserDocument::getAge, 18));
     * }</pre>
     *
     * @param consumer the callback that fills the temporary query wrapper
     * @return this wrapper
     */
    public AggregationWrapper<T> match(Consumer<LambdaQueryWrapper<T>> consumer) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        consumer.accept(wrapper);
        Document criteria = QueryBuilder.buildBson(wrapper);
        this.stages.add(new MatchStage(criteria));

        return this;
    }

    /**
     * Starts a {@code $group} stage using a raw field name as the grouping key.
     *
     * @param field the field name used as the grouping key
     * @return this wrapper
     */
    public AggregationWrapper<T> group(String field) {
        this.currentGroup = new GroupStage(field);
        this.stages.add(this.currentGroup);

        return this;
    }

    /**
     * Starts a {@code $group} stage using a lambda field reference.
     *
     * @param column the lambda field reference used as the grouping key
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> AggregationWrapper<T> group(SFunction<T, R> column) {
        return this.group(LambdaUtils.resolve(column));
    }

    /**
     * Adds a {@code $sum} accumulator that uses the field name as both source and alias.
     *
     * @param field the source field name
     * @return this wrapper
     */
    public AggregationWrapper<T> sum(String field) {
        this.ensureGroup();
        this.currentGroup.addSum(field, field);

        return this;
    }

    /**
     * Adds a {@code $sum} accumulator using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> AggregationWrapper<T> sum(SFunction<T, R> column) {
        return this.sum(LambdaUtils.resolve(column));
    }

    /**
     * Adds a {@code $sum} accumulator with an explicit alias.
     *
     * @param field the source field name
     * @param alias the output alias
     * @return this wrapper
     */
    public AggregationWrapper<T> sum(String field, String alias) {
        this.ensureGroup();
        this.currentGroup.addSum(field, alias);

        return this;
    }

    /**
     * Adds an {@code $avg} accumulator that uses the field name as both source and alias.
     *
     * @param field the source field name
     * @return this wrapper
     */
    public AggregationWrapper<T> avg(String field) {
        this.ensureGroup();
        this.currentGroup.addAvg(field, field);

        return this;
    }

    /**
     * Adds an {@code $avg} accumulator using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> AggregationWrapper<T> avg(SFunction<T, R> column) {
        return this.avg(LambdaUtils.resolve(column));
    }

    /**
     * Adds an {@code $avg} accumulator with an explicit alias.
     *
     * @param field the source field name
     * @param alias the output alias
     * @return this wrapper
     */
    public AggregationWrapper<T> avg(String field, String alias) {
        this.ensureGroup();
        this.currentGroup.addAvg(field, alias);

        return this;
    }

    /**
     * Adds a {@code $max} accumulator that uses the field name as both source and alias.
     *
     * @param field the source field name
     * @return this wrapper
     */
    public AggregationWrapper<T> max(String field) {
        this.ensureGroup();
        this.currentGroup.addMax(field, field);

        return this;
    }

    /**
     * Adds a {@code $max} accumulator using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> AggregationWrapper<T> max(SFunction<T, R> column) {
        return this.max(LambdaUtils.resolve(column));
    }

    /**
     * Adds a {@code $max} accumulator with an explicit alias.
     *
     * @param field the source field name
     * @param alias the output alias
     * @return this wrapper
     */
    public AggregationWrapper<T> max(String field, String alias) {
        this.ensureGroup();
        this.currentGroup.addMax(field, alias);

        return this;
    }

    /**
     * Adds a {@code $min} accumulator that uses the field name as both source and alias.
     *
     * @param field the source field name
     * @return this wrapper
     */
    public AggregationWrapper<T> min(String field) {
        this.ensureGroup();
        this.currentGroup.addMin(field, field);

        return this;
    }

    /**
     * Adds a {@code $min} accumulator using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> AggregationWrapper<T> min(SFunction<T, R> column) {
        return this.min(LambdaUtils.resolve(column));
    }

    /**
     * Adds a {@code $min} accumulator with an explicit alias.
     *
     * @param field the source field name
     * @param alias the output alias
     * @return this wrapper
     */
    public AggregationWrapper<T> min(String field, String alias) {
        this.ensureGroup();
        this.currentGroup.addMin(field, alias);

        return this;
    }

    /**
     * Adds a count accumulator using {@code count} as the default alias.
     *
     * @return this wrapper
     */
    public AggregationWrapper<T> count() {
        this.ensureGroup();
        this.currentGroup.addCount("count");

        return this;
    }

    /**
     * Adds a count accumulator with an explicit alias.
     *
     * @param alias the output alias
     * @return this wrapper
     */
    public AggregationWrapper<T> count(String alias) {
        this.ensureGroup();
        this.currentGroup.addCount(alias);

        return this;
    }

    /**
     * Returns the current wrapper unchanged because aliases are applied by the accumulator methods.
     *
     * @param alias the alias requested by the caller
     * @return this wrapper
     */
    public AggregationWrapper<T> as(String alias) {
        return this;
    }

    /**
     * Adds a {@code $project} stage that includes the supplied fields.
     *
     * @param fields the field names to include
     * @return this wrapper
     */
    public AggregationWrapper<T> project(String... fields) {
        this.stages.add(new ProjectStage(fields));

        return this;
    }

    /**
     * Adds a {@code $project} stage using lambda field references.
     *
     * @param columns the lambda field references to include
     * @param <R>     the field type
     * @return this wrapper
     */
    @SafeVarargs
    public final <R> AggregationWrapper<T> project(SFunction<T, R>... columns) {
        String[] fields = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            fields[i] = LambdaUtils.resolve(columns[i]);
        }

        return this.project(fields);
    }

    /**
     * Adds an ascending {@code $sort} stage.
     *
     * @param field the field name to sort by
     * @return this wrapper
     */
    public AggregationWrapper<T> sortAsc(String field) {
        this.stages.add(new SortStage(field, 1));

        return this;
    }

    /**
     * Adds an ascending {@code $sort} stage using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> AggregationWrapper<T> sortAsc(SFunction<T, R> column) {
        return this.sortAsc(LambdaUtils.resolve(column));
    }

    /**
     * Adds a descending {@code $sort} stage.
     *
     * @param field the field name to sort by
     * @return this wrapper
     */
    public AggregationWrapper<T> sortDesc(String field) {
        this.stages.add(new SortStage(field, -1));

        return this;
    }

    /**
     * Adds a descending {@code $sort} stage using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> AggregationWrapper<T> sortDesc(SFunction<T, R> column) {
        return this.sortDesc(LambdaUtils.resolve(column));
    }

    /**
     * Adds a {@code $limit} stage.
     *
     * @param n the maximum number of aggregated documents to keep
     * @return this wrapper
     */
    public AggregationWrapper<T> limit(int n) {
        this.stages.add(new LimitStage(n));

        return this;
    }

    /**
     * Adds a {@code $skip} stage.
     *
     * @param n the number of aggregated documents to skip
     * @return this wrapper
     */
    public AggregationWrapper<T> skip(int n) {
        this.stages.add(new SkipStage(n));

        return this;
    }

    /**
     * Adds a {@code $lookup} stage for cross-collection joins.
     *
     * @param from         the foreign collection name
     * @param localField   the local join field
     * @param foreignField the foreign join field
     * @param as           the target array field name
     * @return this wrapper
     */
    public AggregationWrapper<T> lookup(
        String from,
        String localField,
        String foreignField,
        String as
    ) {
        this.stages.add(new LookupStage(from, localField, foreignField, as));
        return this;
    }

    /**
     * Builds an immutable {@link Pipeline} from the accumulated stages.
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * Pipeline pipeline = wrapper.build();
     * }</pre>
     *
     * @return the immutable pipeline
     */
    public Pipeline build() {
        return Pipeline.of(this.stages);
    }

    /**
     * Returns an unmodifiable view of the currently collected stages.
     *
     * @return the current stage list
     */
    public List<Stage> stages() {
        return Collections.unmodifiableList(this.stages);
    }

    /**
     * Returns the entity class associated with this wrapper.
     *
     * @return the entity class
     */
    public Class<T> entityClass() {
        return this.entityClass;
    }

    /**
     * Verifies that a {@code $group} stage is active before accumulator methods are invoked.
     */
    private void ensureGroup() {
        if (Objects.isNull(this.currentGroup)) {
            throw new IllegalStateException("No active group. Call group() first.");
        }
    }
}
