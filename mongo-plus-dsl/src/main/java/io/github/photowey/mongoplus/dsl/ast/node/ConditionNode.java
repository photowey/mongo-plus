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

import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;

import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.dsl.ast.enums.AstNodeType;
import io.github.photowey.mongoplus.dsl.ast.enums.Operator;
import io.github.photowey.mongoplus.dsl.ast.visitor.AstVisitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * ConditionNode - Represents a single condition in the AST.
 * Leaf node containing field, operator, and value.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ConditionNode extends AbstractAstNode {

    private static final long serialVersionUID = 5518794193014635854L;

    private String field;
    private Operator operator;
    private Object value;
    /**
     * For BETWEEN
     */
    private Object secondValue;

    @Override
    public AstNodeType getType() {
        return AstNodeType.CONDITION;
    }

    @Override
    public List<AstNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public void addChild(AstNode child) {
        throw new UnsupportedOperationException("ConditionNode is a leaf node and cannot have children");
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Document toBson() {
        Document document = new Document();
        switch (this.operator) {
            case EQ:
                document.put(this.field, this.value);
                break;
            case NE:
                document.put(this.field, new Document(MongoPlusConstants.NE, this.value));
                break;
            case GT:
                document.put(this.field, new Document(MongoPlusConstants.GT, this.value));
                break;
            case GTE:
                document.put(this.field, new Document(MongoPlusConstants.GTE, this.value));
                break;
            case LT:
                document.put(this.field, new Document(MongoPlusConstants.LT, this.value));
                break;
            case LTE:
                document.put(this.field, new Document(MongoPlusConstants.LTE, this.value));
                break;
            case IN:
                document.put(this.field, new Document(MongoPlusConstants.IN, this.value));
                break;
            case NIN:
                document.put(this.field, new Document(MongoPlusConstants.NIN, this.value));
                break;
            case LIKE:
                document.put(this.field, new Document(MongoPlusConstants.REGEX, ".*" + escapeRegex(this.value) + ".*"));
                break;
            case LIKE_LEFT:
                document.put(this.field, new Document(MongoPlusConstants.REGEX, "^" + escapeRegex(this.value) + ".*"));
                break;
            case LIKE_RIGHT:
                document.put(this.field, new Document(MongoPlusConstants.REGEX, ".*" + escapeRegex(this.value) + "$"));
                break;
            case REGEX:
                document.put(
                    field,
                    new Document(MongoPlusConstants.REGEX, Objects.nonNull(this.value) ? this.value.toString() : "")
                );
                break;
            case EXISTS:
                document.put(this.field, new Document(MongoPlusConstants.EXISTS, this.value));
                break;
            case BETWEEN:
                document.put(
                    field,
                    new Document(MongoPlusConstants.GTE, this.value).append(MongoPlusConstants.LTE, this.secondValue)
                );
                break;
            case IS_NULL:
                document.put(this.field, null);
                break;
            case IS_NOT_NULL:
                document.put(this.field, new Document(MongoPlusConstants.NE, null));
                break;
            case ALL:
                document.put(this.field, new Document(MongoPlusConstants.ALL, this.value));
                break;
            case SIZE:
                document.put(this.field, new Document(MongoPlusConstants.SIZE, this.value));
                break;
            default:
                document.put(this.field, this.value);
        }

        return document;
    }

    private String escapeRegex(Object value) {
        if (Objects.isNull(this.value)) {
            return "";
        }

        return Pattern.quote(value.toString());
    }

    /**
     * Factory methods for convenient creation.
     */
    public static ConditionNode eq(String field, Object value) {
        return builder()
            .field(field)
            .operator(Operator.EQ)
            .value(value)
            .build();
    }

    public static ConditionNode gt(String field, Object value) {
        return builder()
            .field(field)
            .operator(Operator.GT)
            .value(value)
            .build();
    }

    public static ConditionNode lt(String field, Object value) {
        return builder()
            .field(field)
            .operator(Operator.LT)
            .value(value)
            .build();
    }

    public static ConditionNode like(String field, String value) {
        return builder()
            .field(field)
            .operator(Operator.LIKE)
            .value(value)
            .build();
    }
}
