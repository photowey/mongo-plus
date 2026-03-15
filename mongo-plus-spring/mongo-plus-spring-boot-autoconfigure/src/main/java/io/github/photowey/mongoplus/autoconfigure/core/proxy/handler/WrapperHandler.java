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
package io.github.photowey.mongoplus.autoconfigure.core.proxy.handler;

import io.github.photowey.mongoplus.executor.getter.ExecutorGetter;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;

/**
 * WrapperHandler - Base handler for wrapper-based methods.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
public abstract class WrapperHandler implements MethodHandler {

    @Override
    @SuppressWarnings("unchecked")
    public Object execute(ExecutorGetter getter, Class<?> entityClass, Object[] args) {
        return this.execute(getter, (Class<Object>) entityClass, (AbstractWrapper<Object>) args[0]);
    }

    protected abstract <T> Object execute(ExecutorGetter getter, Class<T> entityClass, AbstractWrapper<T> wrapper);
}
