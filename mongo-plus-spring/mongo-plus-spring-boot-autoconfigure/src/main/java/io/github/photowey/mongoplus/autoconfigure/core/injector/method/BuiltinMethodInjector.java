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
package io.github.photowey.mongoplus.autoconfigure.core.injector.method;

import java.util.Arrays;
import java.util.List;

import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistry;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.method.AbstractMethod;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.method.MapperMethod;

/**
 * BuiltinMethodInjector - Injects all built-in methods as {@link MapperMethod}.
 * Each built-in (SelectByIdMethod, InsertMethod, etc.) extends AbstractMethod and is registered as one MapperMethod.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
public final class BuiltinMethodInjector {

    private static final List<AbstractMethod> BUILTINS = Arrays.asList(
        new SelectByIdMethod(),
        new SelectListMethod(),
        new SelectOneMethod(),
        new SelectCountMethod(),
        new ExistsMethod(),
        new SelectPageMethod(),
        new AggregateMethod(),
        new InsertMethod(),
        new UpdateByIdMethod(),
        new UpdateMethod(),
        new DeleteByIdMethod(),
        new DeleteMethod(),
        new InsertBatchMethod(),
        new UpdateBatchMethod(),
        new DeleteBatchMethod()
    );

    /**
     * Inject all built-in mapper methods into the registry (each becomes a MapperMethod).
     */
    public void inject(MethodHandlerRegistry registry) {
        registry.addInjectors(BUILTINS);
    }
}
