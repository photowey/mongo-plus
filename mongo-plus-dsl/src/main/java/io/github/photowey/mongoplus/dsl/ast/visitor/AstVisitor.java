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
package io.github.photowey.mongoplus.dsl.ast.visitor;

import io.github.photowey.mongoplus.dsl.ast.node.ConditionNode;
import io.github.photowey.mongoplus.dsl.ast.node.LogicalNode;
import io.github.photowey.mongoplus.dsl.ast.node.RootNode;
import io.github.photowey.mongoplus.dsl.ast.node.geo.GeoIntersectsNode;
import io.github.photowey.mongoplus.dsl.ast.node.geo.GeoWithinNode;
import io.github.photowey.mongoplus.dsl.ast.node.geo.NearNode;

/**
 * AstVisitor - Visitor interface for AST traversal.
 * Implements the Visitor pattern for tree operations.
 *
 * @param <R> the return type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface AstVisitor<R> {
    /**
     * Visit root node.
     */
    R visit(RootNode node);

    /**
     * Visit condition node (leaf).
     */
    R visit(ConditionNode node);

    /**
     * Visit logical node (AND/OR/NOT).
     */
    R visit(LogicalNode node);

    /**
     * Visit near node ($near).
     */
    R visit(NearNode node);

    /**
     * Visit geoWithin node ($geoWithin).
     */
    R visit(GeoWithinNode node);

    /**
     * Visit geoIntersects node ($geoIntersects).
     */
    R visit(GeoIntersectsNode node);
}
