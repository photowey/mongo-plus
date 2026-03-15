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

import io.github.photowey.mongoplus.plugin.enums.InvocationType;

/**
 * InvocationContext - Marks the metadata exposed to an execution interceptor for a single intercepted invocation.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * if (context.getInvocationType() == InvocationType.QUERY) {
 *     // inspect query metadata here
 * }
 * }</pre>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
public interface InvocationContext {

    /**
     * Returns the high-level execution category of the current invocation.
     *
     * @return the invocation type
     */
    InvocationType getInvocationType();
}
