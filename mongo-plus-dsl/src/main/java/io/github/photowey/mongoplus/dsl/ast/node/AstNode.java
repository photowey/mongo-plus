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

import java.io.Serializable;
import java.util.List;

import org.bson.Document;

import io.github.photowey.mongoplus.dsl.ast.enums.AstNodeType;
import io.github.photowey.mongoplus.dsl.ast.visitor.AstVisitor;

/**
 * AstNode - Base interface for all AST nodes.
 * This is the core abstraction for query intermediate representation.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface AstNode extends Serializable {

    /**
     * Get the type of this node.
     */
    AstNodeType getType();

    /**
     * Get parent node (for tree traversal).
     */
    AstNode getParent();

    /**
     * Set parent node.
     */
    void setParent(AstNode parent);

    /**
     * Get child nodes.
     */
    List<AstNode> getChildren();

    /**
     * Add a child node.
     */
    void addChild(AstNode child);

    /**
     * Accept a visitor (Visitor pattern).
     */
    <R> R accept(AstVisitor<R> visitor);

    /**
     * Convert to MongoDB BSON Document.
     * Default implementation, can be overridden.
     */
    default Document toBson() {
        throw new UnsupportedOperationException("toBson not implemented for " + getClass().getSimpleName());
    }

    /**
     * Check if this is a leaf node.
     */
    default boolean isLeaf() {
        return getChildren().isEmpty();
    }

    /**
     * Check if this is a logical node (AND/OR/NOT).
     */
    default boolean isLogical() {
        AstNodeType type = getType();
        return type == AstNodeType.AND || type == AstNodeType.OR || type == AstNodeType.NOT;
    }
}
