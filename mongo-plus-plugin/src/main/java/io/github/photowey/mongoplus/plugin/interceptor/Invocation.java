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
package io.github.photowey.mongoplus.plugin.interceptor;

import io.github.photowey.mongoplus.plugin.context.InvocationContext;

/**
 * Invocation - Represents the state of an invocation as it moves through the interceptor chain.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * Object result = interceptor.intercept(invocation);
 * }</pre>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
public interface Invocation {

    /**
     * Returns the current invocation context.
     *
     * @return the current invocation context
     */
    InvocationContext getContext();

    /**
     * Proceeds to the next interceptor or the terminal framework action.
     *
     * @return the invocation result
     * @throws Throwable when execution fails
     */
    Object proceed() throws Throwable;
}
