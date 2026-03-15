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
package io.github.photowey.mongoplus.query.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.github.photowey.mongoplus.core.geo.Box;
import io.github.photowey.mongoplus.core.geo.Circle;
import io.github.photowey.mongoplus.core.geo.GeoShape;
import io.github.photowey.mongoplus.core.geo.Polygon;
import io.github.photowey.mongoplus.core.util.Arrays;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.dsl.ast.node.AstNode;
import io.github.photowey.mongoplus.dsl.ast.node.ConditionNode;
import io.github.photowey.mongoplus.dsl.ast.node.LogicalNode;
import io.github.photowey.mongoplus.dsl.ast.node.RootNode;
import io.github.photowey.mongoplus.dsl.ast.node.geo.GeoIntersectsNode;
import io.github.photowey.mongoplus.dsl.ast.node.geo.GeoWithinNode;
import io.github.photowey.mongoplus.dsl.ast.node.geo.NearNode;
import io.github.photowey.mongoplus.dsl.ast.visitor.AstVisitor;

/**
 * AstCompiler - Compiles query AST nodes into Spring Data MongoDB query objects.
 *
 * <p>The compiler acts as the bridge between the wrapper-built AST model and the executable MongoDB
 * {@link Query} / {@link Criteria} types.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * Query query = new AstCompiler().compile(wrapper.buildAst());
 * }</pre>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class AstCompiler implements AstVisitor<Criteria> {

    /**
     * Compiles a root AST node into a MongoDB {@link Query}.
     *
     * @param root the root AST node
     * @return the compiled MongoDB query
     */
    public Query compile(RootNode root) {
        Query query = new Query();

        Criteria criteria = root.accept(this);
        if (Objects.nonNull(criteria)) {
            query.addCriteria(criteria);
        }

        return query;
    }

    /**
     * Compiles any AST node into a MongoDB {@link Criteria}.
     *
     * @param node the AST node to compile
     * @return the compiled criteria, or {@code null} when the node is {@code null}
     */
    public Criteria compile(AstNode node) {
        if (Objects.isNull(node)) {
            return null;
        }

        return node.accept(this);
    }

    /**
     * Compiles a root AST node directly to its BSON representation.
     *
     * @param root the root AST node
     * @return the BSON representation of the AST
     */
    public Document compileToBson(RootNode root) {
        if (Objects.isNull(root)) {
            return new Document();
        }

        return root.toBson();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Criteria visit(RootNode node) {
        AstNode child = node.getQueryNode();
        if (Objects.isNull(child)) {
            return null;
        }

        return child.accept(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Criteria visit(ConditionNode node) {
        return this.conditionToCriteria(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Criteria visit(LogicalNode node) {
        List<Criteria> childCriteria = new ArrayList<>();
        for (AstNode child : node.getChildren()) {
            Criteria criteria = child.accept(this);
            if (Objects.nonNull(criteria)) {
                childCriteria.add(criteria);
            }
        }

        if (childCriteria.isEmpty()) {
            return null;
        }

        switch (node.getType()) {
            case AND:
                return new Criteria().andOperator(childCriteria.toArray(new Criteria[0]));
            case OR:
                return new Criteria().orOperator(childCriteria.toArray(new Criteria[0]));
            case NOT:
                return new Criteria().norOperator(childCriteria.toArray(new Criteria[0]));
            default:
                throw new IllegalStateException("Unknown logical type: " + node.getType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Criteria visit(NearNode node) {
        Criteria criteria = Criteria.where(node.getField());
        Point point = new Point(node.getLongitude(), node.getLatitude());

        if (node.getMaxDistance() > 0) {
            return criteria.near(point).maxDistance(node.getMaxDistance());
        }

        return criteria.near(point);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Criteria visit(GeoWithinNode node) {
        String field = node.getField();
        GeoShape shape = node.getShape();
        Document geoWithinDoc = new Document();

        switch (shape.getType()) {
            case CIRCLE:
                Circle circle = (Circle) shape;
                geoWithinDoc.put(
                    MongoPlusConstants.CENTER_SPHERE,
                    Arrays.asList(
                        Arrays.asList(
                            circle.getCenter().getLongitude(),
                            circle.getCenter().getLatitude()
                        ),
                        circle.getRadiusInRadians()
                    )
                );
                break;
            case BOX:
                Box box = (Box) shape;
                geoWithinDoc.put(MongoPlusConstants.BOX, box.toBoxCoordinates());
                break;
            case POLYGON:
                Polygon polygon = (Polygon) shape;
                geoWithinDoc.put(MongoPlusConstants.POLYGON, polygon.toPolygonCoordinates());
                break;
            default:
                geoWithinDoc.put(MongoPlusConstants.GEOMETRY, shape.toGeoJson());
        }

        return Criteria.where(field)
            .is(new Document(MongoPlusConstants.GEO_WITHIN, geoWithinDoc));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Criteria visit(GeoIntersectsNode node) {
        String field = node.getField();
        GeoShape shape = node.getShape();
        Document intersectsDoc = new Document(MongoPlusConstants.GEOMETRY, shape.toGeoJson());

        return Criteria.where(field)
            .is(new Document(MongoPlusConstants.GEO_INTERSECTS, intersectsDoc));
    }

    /**
     * Converts a condition node into a Spring Data {@link Criteria} instance.
     *
     * @param node the condition node to translate
     * @return the translated criteria
     */
    private Criteria conditionToCriteria(ConditionNode node) {
        String field = node.getField();
        Object value = node.getValue();

        switch (node.getOperator()) {
            case EQ:
                return Criteria.where(field).is(value);
            case NE:
                return Criteria.where(field).ne(value);
            case GT:
                return Criteria.where(field).gt(value);
            case GTE:
                return Criteria.where(field).gte(value);
            case LT:
                return Criteria.where(field).lt(value);
            case LTE:
                return Criteria.where(field).lte(value);
            case IN:
                return Criteria.where(field).in(this.toInArray(value));
            case NIN:
                return Criteria.where(field).nin(this.toInArray(value));
            case LIKE:
                return Criteria.where(field).regex(".*" + this.escapeRegex(value) + ".*");
            case LIKE_LEFT:
                return Criteria.where(field).regex("^" + this.escapeRegex(value) + ".*");
            case LIKE_RIGHT:
                return Criteria.where(field).regex(".*" + this.escapeRegex(value) + "$");
            case REGEX:
                return Criteria.where(field).regex(Objects.nonNull(value) ? value.toString() : "");
            case EXISTS:
                return Criteria.where(field).exists((Boolean) value);
            case BETWEEN:
                return Criteria.where(field)
                    .gte(value)
                    .lte(node.getSecondValue());
            case IS_NULL:
                return Criteria.where(field).is(null);
            case IS_NOT_NULL:
                return Criteria.where(field).ne(null);
            case ALL:
                return Criteria.where(field).all((Iterable<?>) value);
            case SIZE:
                return Criteria.where(field).size((Integer) value);
            default:
                return Criteria.where(field).is(value);
        }
    }

    /**
     * Expands a collection-style IN value to the object-array form expected by Spring Data.
     *
     * @param value the original IN or NIN value
     * @return the expanded object array
     */
    private Object[] toInArray(Object value) {
        if (Objects.isNull(value)) {
            return new Object[0];
        }
        if (value instanceof Iterable) {
            return StreamSupport.stream(((Iterable<?>) value).spliterator(), false)
                .toArray(Object[]::new);
        }

        return new Object[] {value};
    }

    /**
     * Escapes a user-provided regex fragment before it is embedded into a query pattern.
     *
     * @param value the original regex fragment source
     * @return the escaped regex fragment
     */
    private String escapeRegex(Object value) {
        if (Objects.isNull(value)) {
            return "";
        }

        return Pattern.quote(value.toString());
    }
}
