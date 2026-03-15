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
package io.github.photowey.mongoplus.dsl.ast.node;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.dsl.ast.enums.AstNodeType;
import io.github.photowey.mongoplus.dsl.ast.visitor.AstVisitor;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * LogicalNode - Represents AND/OR/NOT logical combinations.
 * Composite node containing multiple child conditions.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class LogicalNode extends AbstractAstNode {

    private static final long serialVersionUID = -4234894792706535085L;

    private final AstNodeType logicalType;

    public LogicalNode(AstNodeType type) {
        if (type != AstNodeType.AND && type != AstNodeType.OR && type != AstNodeType.NOT) {
            throw new IllegalArgumentException("LogicalNode type must be AND, OR, or NOT");
        }
        this.logicalType = type;
    }

    public static LogicalNode and() {
        return new LogicalNode(AstNodeType.AND);
    }

    public static LogicalNode or() {
        return new LogicalNode(AstNodeType.OR);
    }

    public static LogicalNode not() {
        return new LogicalNode(AstNodeType.NOT);
    }

    @Override
    public AstNodeType getType() {
        return logicalType;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Document toBson() {
        List<Object> conditions = new ArrayList<>();
        for (AstNode child : children) {
            conditions.add(child.toBson());
        }

        if (logicalType == AstNodeType.NOT) {
            // NOT has single child
            if (Collections.isNotEmpty(conditions)) {
                return new Document(MongoPlusConstants.NOT, conditions.get(0));
            }
            return new Document();
        }

        String operator = (logicalType == AstNodeType.AND)
            ? MongoPlusConstants.AND
            : MongoPlusConstants.OR;

        return new Document(operator, conditions);
    }

    /**
     * Add a condition and return this for chaining.
     */
    public LogicalNode addCondition(ConditionNode condition) {
        this.addChild(condition);
        return this;
    }

    /**
     * Add a nested logical node.
     */
    public LogicalNode addLogical(LogicalNode logical) {
        this.addChild(logical);
        return this;
    }
}
