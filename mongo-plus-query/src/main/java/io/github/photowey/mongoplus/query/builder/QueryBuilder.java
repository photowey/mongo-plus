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
package io.github.photowey.mongoplus.query.builder;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.dsl.ast.node.RootNode;
import io.github.photowey.mongoplus.query.compiler.AstCompiler;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.core.condition.SortCondition;
import io.github.photowey.mongoplus.wrapper.core.enums.SortDirection;

/**
 * QueryBuilder - Builds MongoDB Query from Wrapper using AST.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class QueryBuilder {

    private static final AstCompiler AST_COMPILER = new AstCompiler();

    /**
     * Build MongoDB Query from Wrapper using AST compilation.
     */
    public static <T> Query build(AbstractWrapper<T> wrapper) {
        if (Objects.isNull(wrapper)) {
            return new Query();
        }

        RootNode ast = wrapper.buildAst();
        Query query = AST_COMPILER.compile(ast);

        if (Collections.isNotEmpty(wrapper.getSorts())) {
            Sort sort = buildSort(wrapper.getSorts());
            query.with(sort);
        }

        if (Objects.nonNull(wrapper.getSkip())) {
            query.skip(wrapper.getSkip());
        }
        if (Objects.nonNull(wrapper.getLimit())) {
            query.limit(Math.toIntExact(wrapper.getLimit()));
        }

        // Add projection
        if (Collections.isNotEmpty(wrapper.getProjections())) {
            for (String field : wrapper.getProjections()) {
                if (field.startsWith("-")) {
                    query.fields().exclude(field.substring(1));
                } else {
                    query.fields().include(field);
                }
            }
        }

        return query;
    }

    private static Sort buildSort(List<SortCondition> sorts) {
        List<Sort.Order> orders = new ArrayList<>();
        for (SortCondition sort : sorts) {
            Sort.Direction direction = sort.getDirection() == SortDirection.ASC
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
            orders.add(new Sort.Order(direction, sort.getField()));
        }

        return Sort.by(orders);
    }

    /**
     * Build BSON Document directly from Wrapper.
     */
    public static <T> Document buildBson(AbstractWrapper<T> wrapper) {
        if (Objects.isNull(wrapper)) {
            return new Document();
        }

        return AST_COMPILER.compileToBson(wrapper.buildAst());
    }

    // ----------------------------------------------------------------

    /**
     * Get the AST compiler for custom compilation.
     */
    public static AstCompiler getCompiler() {
        return AST_COMPILER;
    }

    public static AstCompiler astCompiler() {
        return getCompiler();
    }
}
