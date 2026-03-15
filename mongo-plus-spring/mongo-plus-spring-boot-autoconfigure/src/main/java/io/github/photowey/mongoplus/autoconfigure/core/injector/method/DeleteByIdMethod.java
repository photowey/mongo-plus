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

import java.io.Serializable;

import io.github.photowey.mongoplus.autoconfigure.core.enums.Command;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandler;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.method.AbstractMethod;
import io.github.photowey.mongoplus.core.metadata.EntityResolver;

/**
 * DeleteByIdMethod - Built-in deleteById, registered as MapperMethod.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
public final class DeleteByIdMethod extends AbstractMethod {

    public static final String METHOD_NAME = "deleteById";

    @Override
    public String getMethodName() {
        return METHOD_NAME;
    }

    @Override
    protected Command getCommand() {
        return Command.DELETE;
    }

    @Override
    protected MethodHandler createHandler() {
        return (getter, entityClass, args) ->
            getter.queryExecutor().deleteById((Serializable) args[0], getCollectionName(entityClass));
    }

    private static String getCollectionName(Class<?> entityClass) {
        return EntityResolver.resolve(entityClass).getCollectionName();
    }
}
