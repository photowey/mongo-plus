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
package io.github.photowey.mongoplus.wrapper.core.segment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.wrapper.core.condition.Condition;
import io.github.photowey.mongoplus.wrapper.core.enums.Segment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QuerySegment - Tree structure for query conditions (INTRO3 QuerySegment).
 * Supports complex AND/OR nesting.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuerySegment implements Serializable {

    private static final long serialVersionUID = -1137239643643986901L;

    /**
     * Segment type: ROOT, AND, OR, CONDITION.
     */
    @Builder.Default
    private Segment type = Segment.CONDITION;

    /**
     * The actual condition when type is CONDITION.
     */
    private Condition condition;

    /**
     * Child segments when type is AND or OR.
     */
    @Builder.Default
    private List<QuerySegment> children = new ArrayList<>();

    /**
     * Parent segment for tree traversal.
     */
    private QuerySegment parent;

    /**
     * Create a root segment.
     */
    public static QuerySegment root() {
        return QuerySegment.builder()
            .type(Segment.ROOT)
            .children(new ArrayList<>())
            .build();
    }

    /**
     * Create a condition segment.
     */
    public static QuerySegment condition(Condition condition) {
        return QuerySegment.builder()
            .type(Segment.CONDITION)
            .condition(condition)
            .build();
    }

    /**
     * Create an AND segment.
     */
    public static QuerySegment and() {
        return QuerySegment.builder()
            .type(Segment.AND)
            .children(new ArrayList<>())
            .build();
    }

    /**
     * Create AND segment and add children.
     */
    public QuerySegment and(Consumer<QuerySegment> consumer) {
        QuerySegment andSegment = QuerySegment.and();
        consumer.accept(andSegment);
        return addChild(andSegment);
    }

    /**
     * Create an OR segment.
     */
    public static QuerySegment or() {
        return QuerySegment.builder()
            .type(Segment.OR)
            .children(new ArrayList<>())
            .build();
    }

    /**
     * Create OR segment and add children.
     */
    public QuerySegment or(Consumer<QuerySegment> consumer) {
        QuerySegment orSegment = QuerySegment.or();
        consumer.accept(orSegment);
        return addChild(orSegment);
    }

    /**
     * Add a child segment.
     */
    public QuerySegment addChild(QuerySegment child) {
        child.setParent(this);
        this.children.add(child);
        return this;
    }

    /**
     * Add a condition as child.
     */
    public QuerySegment addCondition(Condition condition) {
        return addChild(QuerySegment.condition(condition));
    }

    /**
     * Traverse the segment tree with visitor pattern.
     */
    public void traverse(SegmentVisitor visitor) {
        visitor.visit(this);
        for (QuerySegment child : this.children) {
            child.traverse(visitor);
        }
    }

    /**
     * Check if this segment is a logical operator (AND/OR).
     */
    public boolean isLogical() {
        return type == Segment.AND || type == Segment.OR;
    }

    /**
     * Check if this segment is a leaf condition.
     */
    public boolean isCondition() {
        return type == Segment.CONDITION;
    }

    /**
     * Check if this segment is the root.
     */
    public boolean isRoot() {
        return type == Segment.ROOT;
    }

    /**
     * Get all conditions recursively (leaf nodes only).
     */
    public List<Condition> getAllConditions() {
        List<Condition> result = new ArrayList<>();
        this.traverse(segment -> {
            if (segment.isCondition() && Objects.nonNull(segment.condition)) {
                result.add(segment.condition);
            }
        });

        return result;
    }

    /**
     * SegmentVisitor - Visitor interface for tree traversal.
     */
    @FunctionalInterface
    public interface SegmentVisitor {
        void visit(QuerySegment segment);
    }
}
