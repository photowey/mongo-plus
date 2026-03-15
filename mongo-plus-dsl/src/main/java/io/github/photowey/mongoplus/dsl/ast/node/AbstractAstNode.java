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
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

/**
 * AbstractAstNode - Base implementation of AstNode.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Getter
@Setter
public abstract class AbstractAstNode implements AstNode {

    protected AstNode parent;
    protected List<AstNode> children = new ArrayList<>();

    @Override
    public void addChild(AstNode child) {
        if (child instanceof AbstractAstNode) {
            child.setParent(this);
        }

        this.children.add(child);
    }

    /**
     * Traverse the tree with visitor.
     */
    public void traverse(Consumer<AstNode> visitor) {
        visitor.accept(this);
        for (AstNode child : this.children) {
            if (child instanceof AbstractAstNode) {
                ((AbstractAstNode) child).traverse(visitor);
            } else {
                visitor.accept(child);
            }
        }
    }

    /**
     * Get all leaf nodes (conditions).
     */
    public List<AstNode> getLeafNodes() {
        List<AstNode> leaves = new ArrayList<>();
        this.traverse(node -> {
            if (node.isLeaf()) {
                leaves.add(node);
            }
        });

        return leaves;
    }
}
