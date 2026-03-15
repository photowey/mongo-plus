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
package io.github.photowey.mongoplus.executor.compiler.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.dsl.ast.enums.Operator;
import io.github.photowey.mongoplus.executor.compiler.LegacyQuerySegmentCompiler;
import io.github.photowey.mongoplus.executor.compiler.QueryCompiler;
import io.github.photowey.mongoplus.query.builder.QueryBuilder;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.core.condition.Condition;
import io.github.photowey.mongoplus.wrapper.core.segment.QuerySegment;

/**
 * MongoQueryCompiler - Implementation of QueryCompiler for MongoDB.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class MongoQueryCompiler implements QueryCompiler, LegacyQuerySegmentCompiler {

    @Override
    public Query compile(AbstractWrapper<?> wrapper) {
        return QueryBuilder.build(wrapper);
    }

    @Override
    public Query compile(QuerySegment segmentRoot) {
        if (Objects.isNull(segmentRoot)) {
            return new Query();
        }

        Query query = new Query();
        Criteria criteria = this.compileSegment(segmentRoot);
        if (Objects.nonNull(criteria)) {
            query.addCriteria(criteria);
        }

        return query;
    }

    private Criteria compileSegment(QuerySegment segment) {
        switch (segment.getType()) {
            case CONDITION:
                return this.compileCondition(segment.getCondition());
            case AND:
                return this.compileAnd(segment);
            case OR:
                return this.compileOr(segment);
            case ROOT:
                return this.compileRoot(segment);
            default:
                return null;
        }
    }

    private Criteria compileRoot(QuerySegment root) {
        List<Criteria> criteriaList = new ArrayList<>();
        for (QuerySegment child : root.getChildren()) {
            Criteria criteria = compileSegment(child);
            if (Objects.nonNull(criteria)) {
                criteriaList.add(criteria);
            }
        }
        if (Collections.isEmpty(criteriaList)) {
            return null;
        }
        if (criteriaList.size() == 1) {
            return criteriaList.get(0);
        }

        return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
    }

    private Criteria compileAnd(QuerySegment andSegment) {
        List<Criteria> criteriaList = new ArrayList<>();
        for (QuerySegment child : andSegment.getChildren()) {
            Criteria criteria = compileSegment(child);
            if (Objects.nonNull(criteria)) {
                criteriaList.add(criteria);
            }
        }
        if (Collections.isEmpty(criteriaList)) {
            return null;
        }
        if (criteriaList.size() == 1) {
            return criteriaList.get(0);
        }

        return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
    }

    private Criteria compileOr(QuerySegment orSegment) {
        List<Criteria> criteriaList = new ArrayList<>();
        for (QuerySegment child : orSegment.getChildren()) {
            Criteria criteria = compileSegment(child);
            if (Objects.nonNull(criteria)) {
                criteriaList.add(criteria);
            }
        }
        if (Collections.isEmpty(criteriaList)) {
            return null;
        }
        if (criteriaList.size() == 1) {
            return criteriaList.get(0);
        }

        return new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
    }

    private Criteria compileCondition(Condition condition) {
        if (Objects.isNull(condition)) {
            return null;
        }

        String field = condition.getField();
        Operator operator = condition.getOperator();
        Object value = condition.getValue();

        if (Objects.isNull(field) || Objects.isNull(operator)) {
            return null;
        }

        Criteria criteria = Criteria.where(field);

        switch (operator) {
            case EQ:
                return criteria.is(value);
            case NE:
                return criteria.ne(value);
            case GT:
                return criteria.gt(value);
            case GTE:
                return criteria.gte(value);
            case LT:
                return criteria.lt(value);
            case LTE:
                return criteria.lte(value);
            case IN:
                return criteria.in(toInArray(value));
            case NIN:
                return criteria.nin(toInArray(value));
            case LIKE:
                return criteria.regex(".*" + this.escapeRegex(value) + ".*");
            case LIKE_LEFT:
                return criteria.regex("^" + this.escapeRegex(value) + ".*");
            case LIKE_RIGHT:
                return criteria.regex(".*" + this.escapeRegex(value) + "$");
            case REGEX:
                return criteria.regex(Objects.nonNull(value) ? value.toString() : "");
            case EXISTS:
                return criteria.exists(Objects.nonNull(value) && (Boolean) value);
            case BETWEEN:
                return criteria.gte(value).lte(condition.getSecondValue());
            case IS_NULL:
                return criteria.is(null);
            case IS_NOT_NULL:
                return criteria.ne(null);
            case ALL:
                return criteria.all((Iterable<?>) value);
            case SIZE:
                return criteria.size((Integer) value);
            default:
                return criteria.is(value);
        }
    }

    /**
     * Convert IN/NIN value to Object[] so Criteria gets elements,
     * not a single collection (which would produce $in: [ [a,b] ] instead of $in: [a,b]).
     */
    private static Object[] toInArray(Object value) {
        if (value == null) {
            return new Object[0];
        }
        if (value instanceof Iterable) {
            return StreamSupport.stream(((Iterable<?>) value).spliterator(), false)
                .toArray(Object[]::new);
        }

        return new Object[] {value};
    }

    private String escapeRegex(Object value) {
        if (Objects.isNull(value)) {
            return "";
        }

        return Pattern.quote(value.toString());
    }
}
