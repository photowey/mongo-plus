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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import io.github.photowey.mongoplus.core.geo.GeoShape;
import io.github.photowey.mongoplus.core.lambda.LambdaUtils;
import io.github.photowey.mongoplus.core.lambda.SFunction;
import io.github.photowey.mongoplus.core.metadata.FieldMetadata;
import io.github.photowey.mongoplus.core.util.Arrays;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.dsl.ast.enums.Operator;
import io.github.photowey.mongoplus.dsl.ast.node.AstNode;
import io.github.photowey.mongoplus.dsl.ast.node.ConditionNode;
import io.github.photowey.mongoplus.dsl.ast.node.LogicalNode;
import io.github.photowey.mongoplus.dsl.ast.node.RootNode;
import io.github.photowey.mongoplus.dsl.ast.node.geo.GeoIntersectsNode;
import io.github.photowey.mongoplus.dsl.ast.node.geo.GeoWithinNode;
import io.github.photowey.mongoplus.dsl.ast.node.geo.NearNode;
import io.github.photowey.mongoplus.dsl.condition.ConditionDSL;
import io.github.photowey.mongoplus.dsl.support.QueryDSL;
import io.github.photowey.mongoplus.wrapper.core.condition.Condition;
import io.github.photowey.mongoplus.wrapper.core.condition.SortCondition;
import io.github.photowey.mongoplus.wrapper.core.enums.Segment;
import io.github.photowey.mongoplus.wrapper.core.enums.SortDirection;

import lombok.Data;

/**
 * AbstractWrapper - Serves as the shared state holder for query and update wrappers.
 *
 * <p>In addition to collecting flat conditions, the wrapper maintains an AST representation that can
 * later be compiled by the query module.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * QueryWrapper<UserDocument> wrapper = Wrappers.query(UserDocument.class)
 *     .eq("status", 1)
 *     .orderByDesc("createdAt")
 *     .paginate(1L, 20L);
 * }</pre>
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Data
public abstract class AbstractWrapper<T>
    implements ConditionDSL<T, AbstractWrapper<T>>, QueryDSL<T, AbstractWrapper<T>> {

    protected Class<T> entityClass;
    protected List<Condition> conditions = new ArrayList<>();
    protected List<SortCondition> sorts = new ArrayList<>();
    protected Long limit;
    protected Long skip;
    protected List<String> projections = new ArrayList<>();

    // AST support
    protected RootNode astRoot;
    protected LogicalNode currentLogical;

    /**
     * Creates a wrapper with an empty AST root and an active top-level AND node.
     */
    public AbstractWrapper() {
        this.astRoot = new RootNode();
        this.currentLogical = LogicalNode.and();
        this.astRoot.addChild(this.currentLogical);
    }

    /**
     * Binds the wrapper to an entity class so field metadata can be resolved later.
     *
     * @param entityClass the entity class bound to this wrapper
     * @return this wrapper
     */
    public AbstractWrapper<T> setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> eq(String field, Object value) {
        return this.addCondition(field, Operator.EQ, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> eq(SFunction<T, V> column, Object value) {
        return eq(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> ne(String field, Object value) {
        return this.addCondition(field, Operator.NE, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> ne(SFunction<T, V> column, Object value) {
        return ne(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> gt(String field, Object value) {
        return this.addCondition(field, Operator.GT, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> gt(SFunction<T, V> column, Object value) {
        return gt(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> gte(String field, Object value) {
        return this.addCondition(field, Operator.GTE, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> gte(SFunction<T, V> column, Object value) {
        return gte(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> lt(String field, Object value) {
        return this.addCondition(field, Operator.LT, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> lt(SFunction<T, V> column, Object value) {
        return lt(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> lte(String field, Object value) {
        return this.addCondition(field, Operator.LTE, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> lte(SFunction<T, V> column, Object value) {
        return lte(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> between(String field, Object min, Object max) {
        this.conditions.add(
            Condition.builder()
                .field(field)
                .operator(Operator.BETWEEN)
                .value(min)
                .secondValue(max)
                .build()
        );
        this.currentLogical.addChild(
            ConditionNode.builder()
                .field(field)
                .operator(Operator.BETWEEN)
                .value(min)
                .secondValue(max)
                .build()
        );

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> between(SFunction<T, V> column, Object min, Object max) {
        return between(LambdaUtils.resolve(column), min, max);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> in(String field, Collection<?> values) {
        return this.addCondition(field, Operator.IN, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> in(SFunction<T, V> column, Collection<?> values) {
        return in(LambdaUtils.resolve(column), values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> in(String field, Object... values) {
        return in(field, Arrays.asList(values));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> in(SFunction<T, V> column, Object... values) {
        return in(LambdaUtils.resolve(column), values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> notIn(String field, Collection<?> values) {
        return this.addCondition(field, Operator.NIN, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> notIn(SFunction<T, V> column, Collection<?> values) {
        return notIn(LambdaUtils.resolve(column), values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> like(String field, Object value) {
        return this.addCondition(field, Operator.LIKE, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> like(SFunction<T, V> column, Object value) {
        return like(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> likeLeft(String field, Object value) {
        return this.addCondition(field, Operator.LIKE_LEFT, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> likeLeft(SFunction<T, V> column, Object value) {
        return likeLeft(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> likeRight(String field, Object value) {
        return this.addCondition(field, Operator.LIKE_RIGHT, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> likeRight(SFunction<T, V> column, Object value) {
        return likeRight(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> isNull(String field) {
        return this.addCondition(field, Operator.IS_NULL, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> isNull(SFunction<T, V> column) {
        return isNull(LambdaUtils.resolve(column));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> isNotNull(String field) {
        return this.addCondition(field, Operator.IS_NOT_NULL, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> isNotNull(SFunction<T, V> column) {
        return isNotNull(LambdaUtils.resolve(column));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> and(Consumer<AbstractWrapper<T>> consumer) {
        AbstractWrapper<T> nested = instance();
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
    public AbstractWrapper<T> or(Consumer<AbstractWrapper<T>> consumer) {
        AbstractWrapper<T> nested = instance();
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
    public AbstractWrapper<T> exists(String field, boolean exists) {
        return this.addCondition(field, Operator.EXISTS, exists);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> exists(SFunction<T, V> column, boolean exists) {
        return this.exists(LambdaUtils.resolve(column), exists);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWrapper<T> all(String field, Collection<?> values) {
        return this.addCondition(field, Operator.ALL, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> AbstractWrapper<T> all(SFunction<T, V> column, Collection<?> values) {
        return this.all(LambdaUtils.resolve(column), values);
    }

    /**
     * Resolves a persisted field path from field metadata.
     *
     * @param fieldMetadata the field metadata to resolve
     * @return the resolved field path
     */
    @Override
    public String resolveField(FieldMetadata<?> fieldMetadata) {
        if (Objects.isNull(fieldMetadata)) {
            throw new IllegalArgumentException("Field metadata must not be null");
        }

        return fieldMetadata.resolvePath();
    }

    /**
     * Adds a simple condition to both the flat condition list and the AST model.
     *
     * @param field    the field name
     * @param operator the comparison operator
     * @param value    the comparison value
     * @return this wrapper
     */
    protected AbstractWrapper<T> addCondition(String field, Operator operator, Object value) {
        this.conditions.add(
            Condition.builder()
                .field(field)
                .operator(operator)
                .value(value)
                .build()
        );
        this.currentLogical.addChild(
            ConditionNode.builder()
                .field(field)
                .operator(operator)
                .value(value)
                .build()
        );

        return this;
    }

    /**
     * Adds a regex condition using a raw field name.
     *
     * @param field   the field name
     * @param pattern the regex pattern
     * @return this wrapper
     */
    public AbstractWrapper<T> regex(String field, String pattern) {
        return this.addCondition(field, Operator.REGEX, pattern);
    }

    /**
     * Adds a regex condition using field metadata.
     *
     * @param field   the field metadata
     * @param pattern the regex pattern
     * @return this wrapper
     */
    public AbstractWrapper<T> regex(FieldMetadata<?> field, String pattern) {
        return this.regex(this.resolveField(field), pattern);
    }

    /**
     * Adds a size condition using a raw field name.
     *
     * @param field the field name
     * @param size  the expected collection size
     * @return this wrapper
     */
    public AbstractWrapper<T> size(String field, int size) {
        return this.addCondition(field, Operator.SIZE, size);
    }

    /**
     * Adds a size condition using field metadata.
     *
     * @param field the field metadata
     * @param size  the expected collection size
     * @return this wrapper
     */
    public AbstractWrapper<T> size(FieldMetadata<?> field, int size) {
        return this.size(this.resolveField(field), size);
    }

    /**
     * Includes the supplied fields in the query projection.
     *
     * @param fields the included field names
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> select(String... fields) {
        if (Arrays.isNotEmpty(fields)) {
            this.projections.addAll(Arrays.asList(fields));
        }

        return this;
    }

    /**
     * Includes the supplied field metadata values in the query projection.
     *
     * @param fields the included field metadata values
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> select(FieldMetadata<?>... fields) {
        if (Arrays.isNotEmpty(fields)) {
            for (FieldMetadata<?> field : fields) {
                this.select(this.resolveField(field));
            }
        }

        return this;
    }

    /**
     * Excludes the supplied fields from the query projection.
     *
     * @param fields the excluded field names
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> exclude(String... fields) {
        if (Arrays.isNotEmpty(fields)) {
            for (String f : fields) {
                this.projections.add(
                    "-" + (f.startsWith("-") ? f.substring(1) : f)
                );
            }
        }

        return this;
    }

    /**
     * Excludes the supplied field metadata values from the query projection.
     *
     * @param fields the excluded field metadata values
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> exclude(FieldMetadata<?>... fields) {
        if (Arrays.isNotEmpty(fields)) {
            for (FieldMetadata<?> field : fields) {
                this.exclude(this.resolveField(field));
            }
        }

        return this;
    }

    /**
     * Adds an ascending sort definition using a raw field name.
     *
     * @param field the field name
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> orderByAsc(String field) {
        this.sorts.add(new SortCondition(field, SortDirection.ASC));
        return this;
    }

    /**
     * Adds an ascending sort definition using field metadata.
     *
     * @param field the field metadata
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> orderByAsc(FieldMetadata<?> field) {
        return this.orderByAsc(this.resolveField(field));
    }

    /**
     * Adds a descending sort definition using a raw field name.
     *
     * @param field the field name
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> orderByDesc(String field) {
        this.sorts.add(new SortCondition(field, SortDirection.DESC));
        return this;
    }

    /**
     * Adds a descending sort definition using field metadata.
     *
     * @param field the field metadata
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> orderByDesc(FieldMetadata<?> field) {
        return this.orderByDesc(this.resolveField(field));
    }

    /**
     * Add $near condition - find documents near a point.
     *
     * @param field     the location field
     * @param longitude the center longitude
     * @param latitude  the center latitude
     * @return this wrapper
     */
    public AbstractWrapper<T> near(String field, double longitude, double latitude) {
        return this.addGeoCondition(
            field,
            Operator.NEAR,
            new double[] {longitude, latitude, -1}
        );
    }

    /**
     * Add $near condition with max distance.
     *
     * @param field             the location field
     * @param longitude         the center longitude
     * @param latitude          the center latitude
     * @param maxDistanceMeters the maximum distance in meters
     * @return this wrapper
     */
    public AbstractWrapper<T> near(
        String field,
        double longitude,
        double latitude,
        double maxDistanceMeters
    ) {
        return this.addGeoCondition(
            field,
            Operator.NEAR,
            new double[] {longitude, latitude, maxDistanceMeters}
        );
    }

    /**
     * Add $geoWithin condition.
     *
     * @param field the location field
     * @param shape the geographic shape
     * @return this wrapper
     */
    public AbstractWrapper<T> geoWithin(String field, GeoShape shape) {
        return this.addGeoCondition(field, Operator.GEO_WITHIN, shape);
    }

    /**
     * Add $geoIntersects condition.
     *
     * @param field the location field
     * @param shape the geographic shape
     * @return this wrapper
     */
    public AbstractWrapper<T> geoIntersects(String field, GeoShape shape) {
        return this.addGeoCondition(field, Operator.GEO_INTERSECTS, shape);
    }

    /**
     * Adds a geospatial condition to both the flat condition list and the AST model.
     *
     * @param field    the field name
     * @param operator the geospatial operator
     * @param value    the geospatial payload
     * @return this wrapper
     */
    protected AbstractWrapper<T> addGeoCondition(String field, Operator operator, Object value) {
        this.conditions.add(
            Condition.builder()
                .field(field)
                .operator(operator)
                .value(value)
                .build()
        );
        this.currentLogical.addChild(
            ConditionNode.builder()
                .field(field)
                .operator(operator)
                .value(value)
                .build()
        );

        return this;
    }

    /**
     * Adds a sort definition using a raw field name.
     *
     * @param ascending whether the sort should be ascending
     * @param field     the field name
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> orderBy(boolean ascending, String field) {
        this.sorts.add(new SortCondition(field, ascending ? SortDirection.ASC : SortDirection.DESC));

        return this;
    }

    /**
     * Adds a sort definition using field metadata.
     *
     * @param ascending whether the sort should be ascending
     * @param field     the field metadata
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> orderBy(boolean ascending, FieldMetadata<?> field) {
        return this.orderBy(ascending, this.resolveField(field));
    }

    /**
     * Limits the number of matching documents.
     *
     * @param limit the maximum result size
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> limit(long limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Returns the configured result limit.
     *
     * @return the configured result limit
     */
    public Long limit() {
        return limit;
    }

    /**
     * Skips a number of matching documents.
     *
     * @param skip the number of documents to skip
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> skip(long skip) {
        this.skip = skip;
        return this;
    }

    /**
     * Returns the configured result offset.
     *
     * @return the configured result offset
     */
    public Long skip() {
        return skip;
    }

    /**
     * Applies page-number based pagination settings.
     *
     * @param pageNum  the current page number, starting from {@code 1}
     * @param pageSize the page size
     * @return this wrapper
     */
    @Override
    public AbstractWrapper<T> paginate(long pageNum, long pageSize) {
        if (pageNum < 1) {
            pageNum = 1L;
        }
        this.skip = (pageNum - 1) * pageSize;
        this.limit = pageSize;

        return this;
    }

    /**
     * Applies page-number based pagination settings.
     *
     * @param pageNum the current page number, starting from {@code 1}
     * @param pageSize the page size
     * @return this wrapper
     */
    public AbstractWrapper<T> page(long pageNum, long pageSize) {
        return this.paginate(pageNum, pageSize);
    }

    /**
     * Get the AST root node for compilation.
     */
    public AstNode getAst() {
        return astRoot;
    }

    /**
     * Build and return the query AST.
     */
    public RootNode buildAst() {
        return astRoot;
    }

    /**
     * Copies the current wrapper state to another wrapper instance.
     *
     * @param target the target wrapper that should receive the copied state
     */
    protected void copyStateTo(AbstractWrapper<T> target) {
        target.setEntityClass(this.entityClass);
        target.conditions().addAll(this.copyConditions());
        target.sorts().addAll(this.copySortConditions());
        target.projections().addAll(this.projections);
        target.setLimit(this.limit);
        target.setSkip(this.skip);
        RootNode copiedRoot = this.copyAstRoot();
        target.setAstRoot(copiedRoot);
        if (Objects.nonNull(copiedRoot.getQueryNode()) && copiedRoot.getQueryNode() instanceof LogicalNode) {
            target.setCurrentLogical((LogicalNode) copiedRoot.getQueryNode());
        } else {
            target.setCurrentLogical(LogicalNode.and());
            target.getAstRoot().addChild(target.getCurrentLogical());
        }
    }

    /**
     * Creates a deep copy of the flat condition list.
     *
     * @return the copied conditions
     */
    private List<Condition> copyConditions() {
        List<Condition> copied = new ArrayList<>();
        for (Condition condition : this.conditions) {
            copied.add(this.copyCondition(condition));
        }

        return copied;
    }

    /**
     * Creates a deep copy of a single condition.
     *
     * @param condition the condition to copy
     * @return the copied condition
     */
    private Condition copyCondition(Condition condition) {
        if (Objects.isNull(condition)) {
            return null;
        }

        AbstractWrapper<?> nestedWrapper = condition.getNestedWrapper();

        return Condition.builder()
            .field(condition.getField())
            .operator(condition.getOperator())
            .value(condition.getValue())
            .secondValue(condition.getSecondValue())
            .segment(condition.getSegment())
            .nestedWrapper(Objects.nonNull(nestedWrapper) ? nestedWrapper.clone() : null)
            .build();
    }

    /**
     * Creates a deep copy of the sort definitions.
     *
     * @return the copied sort definitions
     */
    private List<SortCondition> copySortConditions() {
        List<SortCondition> copied = new ArrayList<>();
        for (SortCondition sort : this.sorts) {
            copied.add(
                SortCondition.builder()
                    .field(sort.getField())
                    .direction(sort.getDirection())
                    .build()
            );
        }

        return copied;
    }

    /**
     * Creates a deep copy of the AST root.
     *
     * @return the copied AST root
     */
    private RootNode copyAstRoot() {
        RootNode copied = new RootNode();
        AstNode queryNode = this.astRoot.getQueryNode();
        if (Objects.nonNull(queryNode)) {
            copied.addChild(this.copyAstNode(queryNode));
        }

        return copied;
    }

    /**
     * Creates a deep copy of a single AST node.
     *
     * @param node the AST node to copy
     * @return the copied AST node
     */
    private AstNode copyAstNode(AstNode node) {
        if (Objects.isNull(node)) {
            return null;
        }
        if (node instanceof RootNode) {
            RootNode copied = new RootNode();
            AstNode queryNode = ((RootNode) node).getQueryNode();
            if (Objects.nonNull(queryNode)) {
                copied.addChild(this.copyAstNode(queryNode));
            }

            return copied;
        }
        if (node instanceof LogicalNode) {
            LogicalNode source = (LogicalNode) node;
            LogicalNode copied = new LogicalNode(source.getLogicalType());
            for (AstNode child : source.getChildren()) {
                copied.addChild(this.copyAstNode(child));
            }

            return copied;
        }
        if (node instanceof ConditionNode) {
            ConditionNode source = (ConditionNode) node;
            return ConditionNode.builder()
                .field(source.getField())
                .operator(source.getOperator())
                .value(source.getValue())
                .secondValue(source.getSecondValue())
                .build();
        }
        if (node instanceof NearNode) {
            NearNode source = (NearNode) node;
            return NearNode.builder()
                .field(source.getField())
                .longitude(source.getLongitude())
                .latitude(source.getLatitude())
                .maxDistance(source.getMaxDistance())
                .minDistance(source.getMinDistance())
                .build();
        }
        if (node instanceof GeoWithinNode) {
            GeoWithinNode source = (GeoWithinNode) node;
            return GeoWithinNode.builder()
                .field(source.getField())
                .shape(source.getShape())
                .build();
        }
        if (node instanceof GeoIntersectsNode) {
            GeoIntersectsNode source = (GeoIntersectsNode) node;
            return GeoIntersectsNode.builder()
                .field(source.getField())
                .shape(source.getShape())
                .build();
        }

        throw new IllegalStateException("Unsupported AST node copy type: " + node.getClass().getName());
    }

    /**
     * Creates a new empty wrapper of the concrete subtype.
     *
     * @return a new wrapper instance
     */
    protected abstract AbstractWrapper<T> instance();

    /**
     * Creates a copy of the current wrapper state.
     *
     * @return the cloned wrapper
     */
    public abstract AbstractWrapper<T> clone();

    // ----------------------------------------------------------------

    /**
     * Returns the entity class currently bound to this wrapper.
     *
     * @return the bound entity class
     */
    public Class<T> entityClass() {
        return entityClass;
    }

    /**
     * Returns the flat condition list collected by this wrapper.
     *
     * @return the collected conditions
     */
    public List<Condition> conditions() {
        return conditions;
    }

    /**
     * Returns the collected sort definitions.
     *
     * @return the collected sort definitions
     */
    public List<SortCondition> sorts() {
        return sorts;
    }

    /**
     * Returns the configured projection list.
     *
     * @return the configured projection list
     */
    public List<String> projections() {
        return projections;
    }

    /**
     * Returns the root AST node stored by this wrapper.
     *
     * @return the root AST node
     */
    public RootNode astRoot() {
        return astRoot;
    }

    /**
     * Returns the logical node that currently receives appended AST children.
     *
     * @return the current logical AST node
     */
    public LogicalNode currentLogical() {
        return currentLogical;
    }
}









