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

import io.github.photowey.mongoplus.aggregation.stage.wrapper.AggregationWrapper;
import io.github.photowey.mongoplus.autoconfigure.core.enums.Command;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandler;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.method.AbstractMethod;

/**
 * AggregateMethod - Built-in aggregate(AggregationWrapper, Class), registered as MapperMethod.
 * Delegates to {@link io.github.photowey.mongoplus.executor.AggregationExecutor#execute}.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
@SuppressWarnings("unchecked")
public final class AggregateMethod extends AbstractMethod {

    public static final String METHOD_NAME = "aggregate";

    @Override
    public String getMethodName() {
        return METHOD_NAME;
    }

    @Override
    protected Command getCommand() {
        return Command.SELECT;
    }

    @Override
    protected MethodHandler createHandler() {
        return (getter, entityClass, args) -> {
            AggregationWrapper<Object> wrapper = (AggregationWrapper<Object>) args[0];
            Class<?> outputClass = (Class<?>) args[1];
            return getter.aggregationExecutor().execute(
                wrapper.build(),
                (Class<Object>) entityClass,
                outputClass
            );
        };
    }
}
