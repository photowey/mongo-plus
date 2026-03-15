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
package io.github.photowey.mongoplus.plugin.context;

import java.io.Serializable;

import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.core.util.Arrays;
import io.github.photowey.mongoplus.plugin.enums.InvocationType;
import io.github.photowey.mongoplus.plugin.enums.QueryExecutionOperation;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QueryExecutionContext - Carries metadata for a query, simple, or batch execution intercepted by the framework.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * if (context.getWrapper() != null) {
 *     // inspect query conditions before execution
 * }
 * }</pre>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryExecutionContext implements InvocationContext, Serializable {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private InvocationType invocationType = InvocationType.QUERY;

    private QueryExecutionOperation operation;
    private Class<?> entityClass;
    private String collectionName;

    @Builder.Default
    private Object[] arguments = new Object[0];

    /**
     * Returns the first wrapper argument when the intercepted operation is wrapper-based.
     *
     * @return the wrapper argument, or {@code null} when the invocation does not carry one
     */
    public AbstractWrapper<?> getWrapper() {
        if (Arrays.isEmpty(this.arguments)) {
            return null;
        }
        for (Object argument : this.arguments) {
            if (argument instanceof AbstractWrapper) {
                return (AbstractWrapper<?>) argument;
            }
        }

        return null;
    }

    /**
     * Returns the page argument when the intercepted operation is paginated.
     *
     * @return the page argument, or {@code null} when the invocation does not carry one
     */
    public Page<?> getPage() {
        if (Arrays.isEmpty(this.arguments)) {
            return null;
        }
        for (Object argument : this.arguments) {
            if (argument instanceof Page) {
                return (Page<?>) argument;
            }
        }

        return null;
    }
}
