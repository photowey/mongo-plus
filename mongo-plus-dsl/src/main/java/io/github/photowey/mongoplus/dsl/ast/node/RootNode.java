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

import org.bson.Document;

import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.dsl.ast.enums.AstNodeType;
import io.github.photowey.mongoplus.dsl.ast.visitor.AstVisitor;

import lombok.EqualsAndHashCode;

/**
 * RootNode - Root of the AST.
 * Contains a single child which is the main query structure.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@EqualsAndHashCode(callSuper = true)
public class RootNode extends AbstractAstNode {

    private static final long serialVersionUID = 266438666878379501L;

    public RootNode() {
    }

    public RootNode(AstNode child) {
        this.addChild(child);
    }

    @Override
    public AstNodeType getType() {
        return AstNodeType.ROOT;
    }

    @Override
    public List<AstNode> getChildren() {
        // Root can have at most one child
        return Collections.isEmpty(this.children)
            ? Collections.emptyList()
            : Collections.singletonList(this.children.get(0));
    }

    @Override
    public void addChild(AstNode child) {
        if (Collections.isNotEmpty(this.children)) {
            throw new IllegalStateException("RootNode can have only one child");
        }

        super.addChild(child);
    }

    /**
     * Get the main query node.
     */
    public AstNode getQueryNode() {
        return Collections.isNotEmpty(this.children)
            ? this.children.get(0)
            : null;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Document toBson() {
        if (Collections.isEmpty(this.children)) {
            return new Document();
        }

        return this.children.get(0).toBson();
    }
}
