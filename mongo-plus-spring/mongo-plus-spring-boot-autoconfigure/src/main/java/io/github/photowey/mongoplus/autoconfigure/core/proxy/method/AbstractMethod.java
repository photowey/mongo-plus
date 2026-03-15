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
package io.github.photowey.mongoplus.autoconfigure.core.proxy.method;

import io.github.photowey.mongoplus.autoconfigure.core.enums.Command;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandler;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistry;

/**
 * AbstractMethod - Injectable mapper method (MyBatis-Plus style).
 * Each concrete method (e.g. SelectByIdMethod, InsertMethod) produces one {@link MapperMethod}
 * and registers it via {@link MethodHandlerRegistry#register(MapperMethod)}.
 *
 * <p>All methods (built-in and custom) are unified as MapperMethod.</p>
 *
 * <p>Example:</p>
 * <pre>
 * public class FindByStatusMethod extends AbstractMethod {
 *     &#64;Override public String getMethodName() { return "findByStatus"; }
 *     &#64;Override protected Command getCommand() { return Command.SELECT; }
 *     &#64;Override protected MethodHandler createHandler() {
 *         return (getter, entityClass, args) -&gt; { ... };
 *     }
 * }
 * </pre>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
public abstract class AbstractMethod {

    /**
     * The mapper method name (e.g. "selectById", "insert").
     */
    public abstract String getMethodName();

    /**
     * Command type for this method (SELECT, INSERT, UPDATE, DELETE, BATCH).
     */
    protected abstract Command getCommand();

    /**
     * Create the handler for this method.
     */
    protected abstract MethodHandler createHandler();

    // ----------------------------------------------------------------

    /**
     * Inject this method as a MapperMethod into the registry (unified entry).
     */
    public void inject(MethodHandlerRegistry registry) {
        registry.register(new MapperMethod(this.method(), this.command(), this.createHandler()));
    }

    // ----------------------------------------------------------------

    public Command command() {
        return this.getCommand();
    }

    public String method() {
        return this.getMethodName();
    }
}
